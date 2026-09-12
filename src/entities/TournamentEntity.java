/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import games.Game;
import java.util.List;
import java.util.Map;
import tournaments.*;
import database.DatabaseManager;
import enums.GameResult;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TournamentEntity extends Entity<Tournament> {

    private final PlayerEntity pe = new PlayerEntity();

    public TournamentEntity() {
        super(Tournament.class);
    }

    public Tournament find(String id) {
        try {
            String sql = "SELECT * FROM " + getTable() + " WHERE id = ?";
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);

            // setting the id
            stmt.setString(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                    System.out.println(meta.getColumnName(i) + ": " + rs.getObject(i));
                }

                return mapRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.out.println("Database Connection not found");
        }

        return null;

    }
    
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
            stmt.setTimestamp(8, java.sql.Timestamp.valueOf(start));
            stmt.setTimestamp(9, java.sql.Timestamp.valueOf(end));
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
                        System.out.println(meta.getColumnName(i) + ": " + rs.getObject(i));
                    }

                    Game game = mapGame(row);
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

    @Override
    protected Tournament mapRow(Map<String, Object> row) {
        Tournament t = new Tournament(
                (String) row.get("name"),
                (String) row.get("federation"),
                (String) row.get("director"),
                (String) row.get("chief_arbiter"),
                (String) row.get("deputy_chief_arbiter"),
                TournamentType.getTypeByID(((Number) row.get("tournament_type_id")).intValue()),
                ((java.sql.Timestamp) row.get("start_date")).toLocalDateTime(),
                ((java.sql.Timestamp) row.get("end_date")).toLocalDateTime()
        );

        t.setId((String) row.get("id"));
        t.setRounds(((Number) row.get("rounds")).intValue());

        return t;
    }

    private Game mapGame(Map<String, Object> row) {
        Game game = new Game();

        game.setBlack(pe.find((int) row.get("black_player_id")));
        game.setWhite(pe.find((int) row.get("white_player_id")));

        String result = String.join("-", (String) row.get("white_result"), (String) row.get("black_result"));
        game.setResult(GameResult.getResultFromScore(result));
        game.setRound((int) row.get("round_number"));

        return game;

    }

     public Tournament update(String id, Map<String, Object> attrs) {
        try {
            // really happy with how this turned out
            //had to use stringbuilder to append the attributes, normal strings
            // didnt want to work in the lambda cause they 'had to be final'
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("UPDATE %s SET ", getTable()));

            attrs.forEach((key, value) -> {
                if (value instanceof String) {
                    sql.append(String.format("%s = \"%s\", ", key, value));
                } else {
                    sql.append(String.format("%s = %s, ", key, value));
                }
            });

            sql.deleteCharAt(sql.length() - 2); // to get rid of the comma and space at the end.
            sql.append(" WHERE id = ?");

            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql.toString());
            stmt.setString(1, id);

            if (stmt.executeUpdate() > 0) {
                Statement s = DatabaseManager.getConn().createStatement();
                ResultSet rs = s.executeQuery(String.format("SELECT *"
                        + " FROM %s WHERE id = \"%s\"", getTable(), id));

                if (rs.next()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    Map<String, Object> row = new HashMap<>();

                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }

                    return mapRow(row);
                } else return null;

            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }
}
