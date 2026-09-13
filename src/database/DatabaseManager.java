/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package database;

import java.sql.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private final static String dbName = "tournamentcontrol", url = String.format("jdbc:ucanaccess://%s\\%s.accdb", System.getProperty("user.dir"), dbName);
    private static Connection conn;

    public DatabaseManager() {
    }

    /**
     * Initialises the database
     */
    public static void init() {
                
        try {

            // System.out.printf("Connecting to database with url (%s)...\n", url);
            conn = DriverManager.getConnection(url);

            if (isConnected()) {
                System.out.println("Connected to database!");
            }

            System.out.println();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void closeConn() {
        try {
            conn.close();
            System.out.println("Connection to database closed.");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConn() {
        return conn;
    }

    private static boolean isConnected() {
        try {
            return conn.isValid(0);
        } catch (SQLException ex) {
            System.out.println("Error checking if the database is connected");
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
