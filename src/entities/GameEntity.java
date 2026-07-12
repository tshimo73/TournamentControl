/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package entities;

import enums.GameResult;
import java.util.Map;
import games.*;


public class GameEntity extends Entity<Game>{
    private final PlayerEntity pe = new PlayerEntity();

    public GameEntity() {
        super(Game.class);
    }

    
    @Override
    protected Game mapRow(Map<String, Object> row){
        Game game = new Game();
        
        game.setBlack(pe.find((int) row.get("black_player_id")));
        game.setWhite(pe.find((int) row.get("white_player_id")));
        
        String result = (String) row.get("result");
        game.setResult(GameResult.getResultFromScore(result));
        game.setRound((int) row.get("round_number"));
        
        return game;
        
    }
    
    
    
}
