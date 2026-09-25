
package entities;

import enums.TournamentType;
import games.Game;
import java.util.List;
import java.util.Map;
import tournaments.*;
import database.DatabaseManager;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TournamentEntity extends Entity<Tournament> {

    private final GameEntity ge = new GameEntity();

    public TournamentEntity() {
        super(Tournament.class);
    }

    /**
     * Inserts the tournament into the tblTournaments table in the database
     * @param t
     * @return 
     */
    public boolean insert(Tournament t) {
        try {
            // extract values from class
            String id = t.getId(), dir = t.getDirector(), ca = t.getChiefArbiter(),
                    dca = t.getDeputyChiefArbiter(), fed = t.getFederation(),
                    name = t.getName();
            int rounds = t.getRounds();
            TournamentType type = t.getTournamentType();
            LocalDateTime start = t.getStartDate(), end = t.getEndDate();
            boolean hE = t.hasEnded();

            String sql = String.format("INSERT INTO %s (id, name, federation,"
                    + " director, chief_arbiter, deputy_chief_arbiter, tournament_type_id,"
                    + " start_date, end_date, has_ended, rounds) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", getTable());

            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, fed);
            stmt.setString(4, dir);
            stmt.setString(5, ca);
            stmt.setString(6, dca);
            stmt.setInt(7, type.getID());
            stmt.setTimestamp(8, Timestamp.valueOf(start));
            stmt.setTimestamp(9, Timestamp.valueOf(end));
            stmt.setBoolean(10, hE);
            stmt.setInt(11, rounds);

            if (stmt.executeUpdate() > 0) {
                System.out.println("Inserted: " + name);
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Failed to insert tournament.");
            Logger.getLogger(PlayerEntity.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Gets the games played in this tournament from the database.
     * @param id - the tournaments id
     * @return the list of games played in the tournament
     */
    public List<Game> getGames(String id) {
        List<Game> games = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tblGames WHERE tournament_id = ?";
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            if (meta.getColumnCount() >= 1) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }

                    Game game = ge.mapRow(row);
                    games.add(game);

                }
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TournamentEntity.class.getName()).log(Level.SEVERE, null, ex);
        }

        return games;
    }

    /**
     * Maps the query results into the tournament object
     * @param row - row of attributes
     * @return the tournament object
     */
    @Override
    protected Tournament mapRow(Map<String, Object> row) {
        Tournament t = new Tournament(
                (String) row.get("name"),
                (String) row.get("federation"),
                (String) row.get("director"),
                (String) row.get("chief_arbiter"),
                (String) row.get("deputy_chief_arbiter"),
                TournamentType.getTypeByID(((Number) row.get("tournament_type_id")).intValue()),
                ((Timestamp) row.get("start_date")).toLocalDateTime(),
                ((Timestamp) row.get("end_date")).toLocalDateTime()
        );

        t.setId((String) row.get("id"));
        t.setRounds(((Number) row.get("rounds")).intValue());

        return t;
    }
}
