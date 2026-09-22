package ui.view.managers;

import database.DatabaseManager;
import entities.GameEntity;
import entities.PlayerEntity;
import entities.TournamentEntity;
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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import players.Player;
import tournaments.Tournament;
import ui.views.tournaments.ViewTournament;

public class ViewTournamentManager {

    private Exporter ex;
    private PlayerEntity pe = new PlayerEntity();
    private GameEntity ge = new GameEntity();
    private TournamentEntity te = new TournamentEntity();
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
    
    /**
     * saves the imported tournament to the database
     * 
     * @param t the tournament to be saved
     * @return the succession or failure of the save
     */
    public boolean saveToDB(Tournament t){

        try {
            
            if (!(te.find(t.getId()) == null)) {
                JOptionPane.showMessageDialog(null, "This tournament is already saved in the database.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            te.insert(t);

            for (Player p : t.getPlayers()) {
                p.setTournamentID(t.getId());
                Player dbP = pe.findByFideID(p.getFideID());
                
                if (dbP == null || !dbP.getTournamentID().equals(t.getId())) {
                    pe.insert(p);
                }
            }

            for (Game g : t.getGames()) {
                ge.insert(g);
            }

            t.setIsImported(false);
            
            JOptionPane.showMessageDialog(null, "Tournament successfully saved to the database!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            return true;

        } catch (Exception ex) {
            Logger.getLogger(ViewTournament.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Failed to save tournament: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Sets the leaderboard table
     * @param model 
     */
    public void setLeaderboard(DefaultTableModel model) {
        try {
            // used embedded queries because i had two player id fields so creating
            // the link was a hassle. embeddeds was easy
            Statement stmt = DatabaseManager.getConn().createStatement();
            String sql = String.format("SELECT TOP 20 first_name & \" \" & last_name AS [FullName], "
                    + "fide_id, rating, federation, score, tiebreak, "
                    // win embedded query
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.fide_id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.fide_id = tblGames.black_player_id AND result = \"%s\")"
                    + ")) AS [Wins], "
                    // draw embedded
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.fide_id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.fide_id = tblGames.black_player_id AND result = \"%s\")"
                    + ")) AS [Draws], "
                    // loss embedded
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.fide_id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.fide_id = tblGames.black_player_id AND result = \"%s\")"
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

    /**
     * Gets the stats of a tournament that was manually imported
     * @param t the imported tournament
     * @return a hashmap of the stats
     */
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

    /**
     * Sorts the players according to their scores and tiebreaks
     * @param ps - the players to be sorted
     * @return the list of sorted players
     */
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

    /**
     * Gets the players of a tournament in the db
     * @return a list of the players
     */
    private List<Player> getPlayers() {
        List<Player> ps = new ArrayList<>();
        for (HashMap<String, Object> playerRow : pe.selectWhere("tournament_id", "=", t.getId())) {
            ps.add(pe.mapRow(playerRow));
        }
        return ps;
    }

    /**
     * Gets the games of a tournament in the db
     * @return a list of the games
     */
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
