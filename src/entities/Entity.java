/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * I took heavy inspiration from Springboot and Laravel for this. It was really
 * cool and made the coding so much easier so i made my own poor implementation.
 */
abstract class Entity<T> {

    private String table;

    public Entity(Class<T> c) {
        this.table = "tbl" + c.getSimpleName() + "s";

        System.out.println("From " + table + "\n");
    }

    public Entity(Class<T> c, String table) {
        this.table = table;

        System.out.println("From " + table + "\n");
    }

    /**
     * Find the row with the specified ID
     *
     * @param id
     * @return
     */
    public T find(int id) {
        try {
            String sql = "SELECT * FROM " + table + " WHERE id = ?";
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);

            // setting the id
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
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

    public List<T> getAll() {
        List<T> rows = new ArrayList<>();

        try {
            String sql = "SELECT * FROM " + table;

            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, Object> row = new HashMap<>();

                //AI helped me with this loop
                // i didn't know the methods in the ResultSetMetaData class
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }

                T result = mapRow(row);

                rows.add(result);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.out.println("Database Connection not found");
        }

        return rows;
    }

    public boolean delete(int id) {
        String sql = String.format("DELETE FROM %s WHERE id = ?", table);

        try {
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);

            stmt.setInt(1, id);

            if (stmt.executeUpdate() == 1) {
                System.out.println("Element has been successfully deleted from the database.");
                return true;
            } else {
                return false;
            }

        } catch (SQLException ex) {
            System.out.println("Error deleting row from " + table);
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * A method i made to update records in a table with ease
     *
     * @param id
     * @param attrs
     * @return
     */
    public T update(int id, Map<String, Object> attrs) {
        try {
            // really happy with how this turned out
            //had to use stringbuilder to append the attributes, normal strings
            // didnt want to work in the lambda cause they 'had to be final'
            StringBuilder sql = new StringBuilder();
            sql.append(String.format("UPDATE %s SET ", table));

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
            stmt.setInt(1, id);

            if (stmt.executeUpdate() > 0) {
                Statement s = DatabaseManager.getConn().createStatement();
                ResultSet rs = s.executeQuery(String.format("SELECT *"
                        + " FROM %s WHERE id = %d", table, id));

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
    
    // i discovered the ... parameter thing today, really cool.
    // other than the required parameters it allows for an 'infinite' amount of optional
    // parameters that get put into an array.
    public List<HashMap<String, Object>> selectWhere(String field, String operation, Object answer, String... fieldsToSelect) {
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

        
        // joining them fields to add to the select statement
        String fields = (fieldsToSelect.length == 0)
                ? "*" : String.join(", ", fieldsToSelect);

        try {
            boolean isAllowedOp = operations.contains(operation) || "LIKE".equalsIgnoreCase(operation);
            if (!isAllowedOp) {
                System.out.println("No SQL statement could be made");
                return null;
            }

            String sql = String.format("SELECT %s FROM %s WHERE %s %s ?",
                    fields, getTable(), field, operation);
            System.out.println(sql);

            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);
            
            // i was unsure of the answers data typeS
            stmt.setObject(1, answer);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<>();
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
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

    protected abstract T mapRow(Map<String, Object> row);

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

}
