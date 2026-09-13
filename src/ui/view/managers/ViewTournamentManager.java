package ui.view.managers;

import database.DatabaseManager;
import entities.GameEntity;
import entities.PlayerEntity;
import enums.Federation;
import enums.GameResult;
import filing.exporter.Exporter;
import games.Game;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import players.Player;
import tournaments.Tournament;
import ui.views.tournaments.ViewTournament;

public class ViewTournamentManager {

    private Exporter ex;
    private PlayerEntity pe = new PlayerEntity();
    private GameEntity ge = new GameEntity();
    private Tournament t;

    public ViewTournamentManager(Tournament t) {
        this.t = t;
    }

    public File export(File dest) {
        t.setPlayers(getPlayers());
        t.setGames(getGames());

        ex = new Exporter(t, dest);

        return ex.export();
    }

    public void getLeaderboard(DefaultTableModel model) {
        try {
            // used embedded queries because i had two player id fields so creating
            // the link was a hassle. embeddeds was easy
            Statement stmt = DatabaseManager.getConn().createStatement();
            String sql = String.format("SELECT TOP 20 first_name & \" \" & last_name AS [FullName], "
                    + "fide_id, rating, federation, score, tiebreak, "
                    // win embedded query
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.id = tblGames.black_player_id AND result = \"%s\")"
                    + ")) AS [Wins], "
                    // draw embedded
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.id = tblGames.black_player_id AND result = \"%s\")"
                    + ")) AS [Draws], "
                    // loss embedded
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.id = tblGames.black_player_id AND result = \"%s\")"
                    + ")) AS [Losses] "
                    // rest of the query
                    + "FROM tblRegistrations WHERE tournament_id = \"%s\" "
                    + "ORDER BY score DESC, tiebreak DESC",
                    t.getId(), GameResult.WHITE_WIN.getScore(), GameResult.BLACK_WIN.getScore(), // win vars
                    t.getId(), GameResult.DRAW.getScore(), GameResult.DRAW.getScore(), // draw vars
                    t.getId(), GameResult.BLACK_WIN.getScore(), GameResult.WHITE_WIN.getScore(), // loss vars
                    t.getId()); // for the final where clause

            ResultSet rs = stmt.executeQuery(sql);

            int rank = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                    rank,
                    rs.getString("FullName"),
                    rs.getString("fide_id"),
                    rs.getDouble("rating"),
                    Federation.valueOf(rs.getString("federation")).getCountryName(),
                    rs.getDouble("score"),
                    rs.getDouble("tiebreak"),
                    rs.getInt("Wins"),
                    rs.getInt("Draws"),
                    rs.getInt("Losses")
                });
                rank++;
            }

        } catch (SQLException ex) {
            System.out.println("Failed to get leaderboard");
            Logger.getLogger(ViewTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns the wins, draws, and losses of each player in the form of a list.
     * list(0) is the wins, list(1) is the draws, list(2) is the losses
     * @param ps
     * @param gs
     * @return 
     */
    public HashMap<Player, List<Integer>> getWDL(List<Player> ps, List<Game> gs){
        HashMap<Player, List<Integer>> wdl = new HashMap<>();
        
        for(Player p : ps){
            List<Integer> stats = new ArrayList<>();
            int w = 0, d = 0, l = 0;
            
            for(Game g : gs){
                GameResult res = g.getResult();
                if(g.getWhite().equals(p)){
                    if(res == GameResult.WHITE_WIN){
                        w++;
                    } else if(res == GameResult.DRAW){
                        d++;
                    } else {
                        l++;
                    }
                } else if(g.getBlack().equals(p)){
                    if(res == GameResult.BLACK_WIN){
                        w++;
                    } else if(res == GameResult.DRAW){
                        d++;
                    } else {
                        l++;
                    }
                }
            }
            
            stats.add(0, w);
            stats.add(1, d);
            stats.add(2, l);
            
            wdl.put(p, stats);
        }
        
        return wdl;
    }

    public HashMap<String, Object> getImportedTournamentStats(Tournament t) {
        HashMap<String, Object> stats = new HashMap<>();
        List<Game> games = t.getGames();
        List<Player> players = t.getPlayers();
        double sumRating = 0.0;
        int numDraws = 0;

        stats.put("numGames", games.size());
        stats.put("numPlayers", players.size());

        for (Game g : games) {
            if (g.isDraw()) {
                numDraws++;
            }
        }

        stats.put("drawPercentage", (double) numDraws / games.size() * 100.00);
        stats.put("decisiveGames", games.size() - numDraws);

        for (Player p : players) {
            sumRating += p.getRating();
        }

        stats.put("aveRating", (double) sumRating / players.size());

        // sort leaderboard
        stats.put("leaderboard", sortPlayers(players));
        return stats;
    }

    private List<Player> sortPlayers(List<Player> ps) {
        Player[] sorted = ps.toArray(new Player[0]);

        for (int i = 0; i < sorted.length - 1; i++) {
            for (int j = 0; j < sorted.length; j++) {
                if (sorted[j].getScore() < sorted[i].getScore()) {
                    Player temp = sorted[i];
                    sorted[i] = sorted[j];
                    sorted[j] = temp;
                } else if (sorted[j].getScore() == sorted[i].getScore()) {
                    if (sorted[i].getTieBreak() > sorted[j].getTieBreak()) {
                        Player temp = sorted[i];
                        sorted[i] = sorted[j];
                        sorted[j] = temp;
                    }
                }
            }
        }
        
        return List.of(sorted);
    }

    private List<Player> getPlayers() {
        List<Player> ps = new ArrayList<>();
        for (HashMap<String, Object> playerRow : pe.selectWhere("tournament_id", "=", t.getId())) {
            ps.add(pe.mapRow(playerRow));
        }
        return ps;
    }

    private List<Game> getGames() {
        List<Game> games = new ArrayList<>();
        for (HashMap<String, Object> gameRow : ge.selectWhere("tournament_id", "=", t.getId())) {
            Game g = ge.mapRow(gameRow);
            g.setTournament(t);

            games.add(g);
        }
        return games;
    }
}
