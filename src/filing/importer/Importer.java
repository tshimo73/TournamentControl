package filing.importer;

import database.DatabaseManager;
import java.io.File;
import tournaments.*;
import players.*;
import games.*;
import enums.FileTitle;
import enums.GameResult;
import enums.PlayerTitle;
import filing.InvalidFileExtensionException;
import filing.exporter.Exporter;
import java.sql.*;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tshim
 */
public class Importer {

    private final File file;
    private final List<Player> players = new ArrayList<>();
    private final List<Game> games = new ArrayList<>();
    private Scanner sc;
    private Tournament t;

    // keeps track of players in the tournament -- spcified in the id so that the can be added
    // as a player to the games
    HashMap<Integer, Player> playersWithID = new HashMap<>();

    public Importer(File file) {
        this.file = file;
    }

    /**
     * Scans the contents of the file to be able to load it onto the program
     *
     * @throws InvalidFileExtensionException
     */
    public void scanFile() throws InvalidFileExtensionException {

        if (!file.getName().endsWith(Exporter.FILE_EXTENSION)) {
            throw new InvalidFileExtensionException("The imported file is not a "
                    + "valid tournament file.");
        }

        try {
            int count = 1;
            sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] tokens = line.split("#");
                String title = tokens[0];

                if (title.equals(FileTitle.TOURNAMENT.toString())) {
                    String id = tokens[1], name = tokens[2], fed = tokens[3],
                            director = tokens[4], chiefArb = tokens[5],
                            depChiefArb = tokens[6], tournType = tokens[7],
                            startDate = tokens[8], endDate = tokens[9],
                            rounds = tokens[10];

                    t = new Tournament();

                    t.setId(id);
                    t.setName(name);
                    t.setDirector(director);
                    t.setFederation(fed);
                    t.setRounds(Integer.parseInt(rounds));
                    t.setTournamentType(TournamentType.valueOf(tournType.toUpperCase()));
                    t.setChiefArbiter(chiefArb);
                    t.setDeputyChiefArbiter(depChiefArb);
                    t.setStartDate(LocalDateTime.parse(startDate));
                    t.setEndDate(LocalDateTime.parse(endDate));

                } else if (title.equals(FileTitle.PLAYER.toString())) {

                    int id = Integer.parseInt(tokens[1]);
                    String fName = tokens[2], lName = tokens[3],
                            fideID = tokens[4], fed = tokens[5];

                    double score = Double.parseDouble(tokens[8]), rating = Double.parseDouble(tokens[6]),
                            tieBreak = Double.parseDouble(tokens[9]);
                    PlayerTitle pTitle = PlayerTitle.getTitleFromRating(rating);

                    Player p = new Player();

                    p.setId(id);
                    p.setFirstName(fName);
                    p.setLastName(lName);
                    p.setFideID(fideID);
                    p.setFederation(fed);
                    p.setRating(rating);
                    p.setTitle(pTitle);
                    p.setScore(score);
                    p.setTieBreak(tieBreak);

                    players.add(p);

                    playersWithID.put(id, p);
                } else if (title.equals(FileTitle.GAME.toString())) {

                    int id = Integer.parseInt(tokens[1]), round = Integer.parseInt(tokens[2]),
                            wID = Integer.parseInt(tokens[4]), bID = Integer.parseInt(tokens[5]);
                    GameResult res = GameResult.valueOf(tokens[6]);
                    String tournID = tokens[3], opening = tokens[7];

                    // If the game isnt apart of this tournament, ignore it and
                    // continue checking the other games
                    if (!tournID.equals(t.getId())) {
                        continue;
                    }

                    Game g = new Game();
                    g.setId(id);
                    g.setRound(round);
                    g.setBlack(playersWithID.get(bID));
                    g.setWhite(playersWithID.get(wID));
                    g.setResult(res);
                    g.setOpening(opening);
                    g.setTournament(t);

                    games.add(g);

                } else if (title.equals(FileTitle.END.toString())) {
                    break;
                } else {
                    // If a line is invalid, the whole tournament will be invalid
                    // because that line could be a crucial piece of data
                    System.out.println("Invalid Line From File: Line " + count);
                    System.out.println("\"" + line + "\"");
                    System.out.println();
                    System.out.println("Ensure that your TC file is not corrupted.");
                    break;
                }
                count++;
            }

            t.setGames(games);
            t.setPlayers(players);
            System.out.printf("File Scanned (%s)\n", file.getName());
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    public Tournament getLoadedTournament() {
        return t;
    }

    public boolean saveToDB() {
        String sqlTournament = "INSERT INTO tblTournaments (name, "
                + "federation, director, chief_arbiter, deputy_chief_arbiter,"
                + "tournament_type_id, start_date, end_date)"
                + " VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // adding all the statements so that i can run them all at the same time
            DatabaseManager.getConn().setAutoCommit(false);

            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sqlTournament);
            stmt.setString(1, t.getName());
            stmt.setString(2, t.getFederation());
            stmt.setString(3, t.getDirector());
            stmt.setString(4, t.getChiefArbiter());
            stmt.setString(5, t.getDeputyChiefArbiter());
            stmt.setInt(6, t.getTournamentType().getID());
            stmt.setTimestamp(7, Timestamp.valueOf(t.getStartDate()));
            stmt.setTimestamp(8, Timestamp.valueOf(t.getEndDate()));

            // executeUpdate returns the number of rows affected
            if (stmt.executeUpdate() >= 1) {
                PreparedStatement stmtForTID = DatabaseManager.getConn().prepareStatement(
                        "SELECT * FROM tblTournaments WHERE name = ?"
                );

                stmtForTID.setString(1, t.getName());

                ResultSet rs = stmtForTID.executeQuery();

                if (rs.next()) {
                    String sqlGames = "INSERT INTO tblGames (tournament_id, round_number"
                            + ", white_player_id, black_player_id, result) VALUES("
                            + "?, ?, ?, ?, ?)";
                    PreparedStatement stmtGames = DatabaseManager.getConn().prepareStatement(sqlGames);

                    for (Game g : t.getGames()) {

                        stmtGames.setInt(1, rs.getInt("id"));
                        stmtGames.setInt(2, g.getRound());
                        stmtGames.setInt(3, g.getWhite().getId());
                        stmtGames.setInt(4, g.getBlack().getId());
                        stmtGames.setString(5, g.getResult().getScore());

                        stmtGames.addBatch();
                    }

                    stmtGames.executeBatch();
                }
                DatabaseManager.getConn().commit();
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("\nFailed to save imported tournament to database.\n");
            System.out.println(ex.getMessage() + "\n");
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {

            try {
                DatabaseManager.getConn().setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println("autocommit failed to reset");
            }
        }

    }

}
