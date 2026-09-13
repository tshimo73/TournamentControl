/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package entities;

import database.DatabaseManager;
import enums.GameResult;
import java.util.Map;
import games.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameEntity extends Entity<Game>{
    private final PlayerEntity pe = new PlayerEntity();

    public GameEntity() {
        super(Game.class);
    }

    
    public boolean insert(Game g) {
        try {
            // extract values from class
            String tID = g.getTournament().getId(), result = g.getResult().getScore(),
                    opening = g.getOpening(), wFID = g.getWhite().getFideID(),
                    bFID = g.getBlack().getFideID();
            int roundNum = g.getRound();
            
            String sql = String.format("INSERT INTO %s (tournament_id, round_number,"
                    + " white_player_id, black_player_id, result, opening) "
                    + "VALUES (?, ?, ?, ?, ?, ?)", getTable());

            PreparedStatement stmt = DatabaseManager.getConn().prepareStatement(sql);
            stmt.setString(1, tID);
            stmt.setInt(2, roundNum);
            stmt.setString(3, wFID);
            stmt.setString(4, bFID);
            stmt.setString(5, result);
            stmt.setString(6, opening);

            if (stmt.executeUpdate() > 0) {
                System.out.println(String.format("Inserted game: W(%s) vs B(%s)", g.getWhite().getFullName(), g.getBlack().getFullName()));
                return true;
            } else {
                System.out.println("game not inserted");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Failed to insert game.");
            Logger.getLogger(PlayerEntity.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    } 
    
    @Override
    public Game mapRow(Map<String, Object> row){
        Game game = new Game();
        
        game.setBlack(pe.findByFideID((String) row.get("black_player_id")));
        game.setWhite(pe.findByFideID((String) row.get("white_player_id")));
        
        String result = (String) row.get("result");
        game.setResult(GameResult.getResultFromScore(result));
        game.setRound((int) row.get("round_number"));
        
        return game;
        
    }
    
    
    
}
