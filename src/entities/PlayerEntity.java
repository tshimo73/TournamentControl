/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import database.DatabaseManager;
import java.sql.*;
import java.util.Map;
import players.*;
import enums.PlayerTitle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerEntity extends Entity<Player> {

    public PlayerEntity() {
        super(Player.class, "tblRegistrations");
    }

    public boolean insert(Player p) {
        try {
            // extract values from class
            String tID = p.getTournamentID();
            PlayerTitle title = p.getTitle();
            String fN = p.getFirstName(), lN = p.getLastName(), fID = p.getFideID(),
                    fed = p.getFederation();
            double r = p.getRating(), score = p.getScore(), tb = p.getTieBreak();
            String sql = String.format("INSERT INTO %s (tournament_id, first_name, last_name"
                    + ", fide_id, federation, rating, score, tiebreak, title) VALUES"
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?)", getTable());

            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);
            stmt.setString(1, tID);
            stmt.setString(2, fN);
            stmt.setString(3, lN);
            stmt.setString(4, fID);
            stmt.setString(5, fed);
            stmt.setDouble(6, r);
            stmt.setDouble(7, score);
            stmt.setDouble(8, tb);
            stmt.setString(9, title.toString());

            if (stmt.executeUpdate() > 0) {
                System.out.println("Inserted: " + p.getFullName());
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Failed to insert user.");
            Logger.getLogger(PlayerEntity.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Finds player by their FIDE ID. Returns the first--which should be the
     * only--player
     *
     * @param fID
     * @return
     */
    public Player findByFideID(String fID) {
        // Words cannot express how happy I am with this method (the select where)
        List<HashMap<String, Object>> playerRows = selectWhere("fide_id", "=", fID);

        if (playerRows == null || playerRows.isEmpty()) {
            return null;
        }

        return mapRow(playerRows.getFirst());
    }

    @Override
    public Player mapRow(Map<String, Object> row) {
        Player p = new Player();

        // 'Number' used because the numbers in the DB were casted as Double
        // Couldn't find an Integer data type on access so i stuck with this
        p.setId(((Number) row.get("id")).intValue());
        p.setTournamentID((String) row.get("tournament_id"));
        p.setFirstName((String) row.get("first_name"));
        p.setLastName((String) row.get("last_name"));
        p.setFideID((String) row.get("fide_id"));
        p.setFederation((String) row.get("federation"));
        p.setRating(((Number) row.get("rating")).doubleValue());
        p.setScore(((Number) row.get("score")).doubleValue());
        p.setTieBreak(((Number) row.get("tiebreak")).doubleValue());

        String title = (String) row.get("title");
        p.setTitle(title.equals("N/A") ? PlayerTitle.NoTitle : PlayerTitle.valueOf(title));

        return p;
    }

}
