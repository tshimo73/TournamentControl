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
 * I took heavy inspiration from Springboot and Laravel for this. 
 * It was really cool and made the coding so much easier so i made my own poor implementation.
 */
abstract class Entity<T> {
    private String table;
    private final DatabaseManager dm = new DatabaseManager();
    
    public Entity(Class<T> c){
        this.table = "tbl" + c.getSimpleName() + "s";
        
        System.out.println("From " + table + "\n");
        dm.init();
    }
    
    /**
     * Find the row with the specified ID
     * @param id
     * @return 
     */
    public T find(int id){
        try {
            String sql = "SELECT * FROM " + table + " WHERE id = ?";
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);
            
            // setting the id
            stmt.setInt(1, id);
            
            ResultSet rs = stmt.executeQuery();
            
            if(rs.next()){
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, Object> row = new HashMap<>();
                
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                    System.out.println(meta.getColumnName(i)+ ": " + rs.getObject(i));
                }
                
                return mapRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        } catch(NullPointerException ex){
            System.out.println("Database Connection not found");
        }
        
        return null;
        
    }
    
    public List<T> getAll(){
        List<T> rows = new ArrayList<>();
        
        try {
            String sql = "SELECT * FROM " + table;
            
            PreparedStatement stmt = dm.getConn().prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            
            while(rs.next()){
                ResultSetMetaData meta = rs.getMetaData();
                Map<String, Object> row = new HashMap<>();
                
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                    System.out.println(meta.getColumnName(i)+ ": " + rs.getObject(i));
                }
                
                T result = mapRow(row);
                
                rows.add(result);
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
        }  catch(NullPointerException ex){
            System.out.println("Database Connection not found");
        }
        
        return rows;
    }
    
    public boolean delete(int id){
        String sql = String.format("DELETE FROM %s WHERE id = ?", table);
        
        try {
            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);
            
            stmt.setInt(1, id);
            
            if(stmt.executeUpdate() == 1){
                System.out.println("Element has been successfully deleted from the database.");
                return true;
            } else return false;
            
        } catch (SQLException ex) {
            System.out.println("Error deleting row from " + table);
            Logger.getLogger(Entity.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    protected abstract T mapRow(Map<String, Object> row);
    
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
    
}
