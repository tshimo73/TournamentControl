/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import database.DatabaseManager;
import database.PlayerFields;
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
            int tID = p.getTournamentID();
            PlayerTitle title = p.getTitle();
            String fN = p.getFirstName(), lN = p.getLastName(), fID = p.getFideID(),
                    fed = p.getFederation();
            double r = p.getRating(), score = p.getScore(), tb = p.getTieBreak();
            String sql = String.format("INSERT INTO %s (tournament_id, first_name, last_name"
                    + ", fide_id, federation, rating, score, tiebreak, title) VALUES"
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?)", getTable());

            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);
            stmt.setInt(1, tID);
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

    // i discovered the ... parameter thing today, really cool.
    // other than the required parameters it allows for an 'infinite' amount of optional
    // parameters that get put into an array.
    public List<HashMap<String, Object>> selectWhere(PlayerFields field, String operation, Object answer, PlayerFields... fieldsToSelect) {
        // i did it this way because i'm not selecting all the rows,
        // with the hashmap i can get specific about what exactly i want to get
        // i.e. I only select id and date of birth
        // i can get them specifically from the hashmap and typecast them

        List<HashMap<String, Object>> rowsOfPlayerResults = new ArrayList<>();

        List<String> operations = new ArrayList<>();
        operations.add("=");
        operations.add(">");
        operations.add("<");
        operations.add("<>");

        // converting all the fields to LOWERCASE strings, since the enum returns them as uppercase
        String[] fts = new String[fieldsToSelect.length];

        for (int i = 0; i < fieldsToSelect.length; i++) {
            if (fieldsToSelect.length == 0 || fieldsToSelect == null) {
                break;
            }

            fts[i] = fieldsToSelect[i].toString();
        }

        // joining them fields to add to the select statement
        String fields = (fieldsToSelect == null || fieldsToSelect.length == 0)
                ? "*" : String.join(", ", fts);

        try {

            String sql = operations.contains(operation)
                    ? String.format("SELECT %s FROM %s WHERE %s %s %s",
                            fields, getTable(), field.toString(), operation, answer)
                    : (operation.equals("LIKE"))
                    ? String.format("SELECT %s FROM %s WHERE %s %s \"%s\"",
                            fields, getTable(), field.toString(), operation, answer)
                    : null; // only using this method for basic operations 
            // and LIKE. anything else will be done manually.

            if (sql == null) {
                System.out.println("No SQL statement could be configured"
                        + " in the player entity.");
                return null;
            }

            Statement stmt = DatabaseManager.getConn().createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    System.out.println(meta.getColumnName(i) + ": " + rs.getObject(i));
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }

                rowsOfPlayerResults.add(row);
            }
        } catch (SQLException ex) {
            System.out.println("Failed to get player results");
            Logger.getLogger(PlayerEntity.class.getName()).log(Level.SEVERE, null, ex);
        }

        return rowsOfPlayerResults;
    }

    /**
     * Finds player by their FIDE ID. Returns the first--which should be the 
     * only--player
     * @param fID
     * @return 
     */
    public Player findByFideID(String fID) {
        // Words cannot express how happy I am with this method
        HashMap<String, Object> playerRow = selectWhere(PlayerFields.FIDE_ID, "=", fID).getFirst();
        
        return mapRow(playerRow);
    }

    @Override
    protected Player mapRow(Map<String, Object> row) {
        Player p = new Player();

        // 'Number' used because the numbers in the DB were casted as Double
        // Couldn't find an Integer data type on access so i stuck with this
        p.setId(((Number) row.get("id")).intValue());
        p.setTournamentID(((Number) row.get("tournament_id")).intValue());
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
