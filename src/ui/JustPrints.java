/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ui;

import database.DatabaseManager;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JustPrints {
    public static void main(String[] args) {
        DatabaseManager dm = new DatabaseManager();
        
        dm.init();
        
        Connection conn = DatabaseManager.getConn();
        
        try {
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM tblGames");
            
            while(rs.next()){
                ResultSetMetaData meta = rs.getMetaData();
                
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    System.out.println(meta.getColumnName(i)+ ": " + rs.getObject(i));
                }
                System.out.println();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JustPrints.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
