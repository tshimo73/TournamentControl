
package database;

import java.sql.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {

    private final static String DB_NAME = "tournamentcontrol", URL = String.format("jdbc:ucanaccess://%s\\%s.accdb", System.getProperty("user.dir"), DB_NAME);
    private static Connection conn;

    public DatabaseManager() {}

    /**
     * Initialises the database
     */
    public static void init() {
                
        try {

            // System.out.printf("Connecting to database with url (%s)...\n", url);
            conn = DriverManager.getConnection(URL);

            if (isConnected()) {
                System.out.println("Connected to database!");
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (SQLException ex) {
            System.out.println("Failed to connect to the database.");
            ex.printStackTrace();
        }

    }

    /**
     * Close the database connection
     */
    public static void closeConn() {
        try {
            conn.close();
            System.out.println("Connection to database closed.");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get the database connection
     * @return Connection conn
     */
    public static Connection getConn() {
        return conn;
    }

    /**
     * Returns the database connection status (connected / disconnected)
     * @return database status
     */
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
