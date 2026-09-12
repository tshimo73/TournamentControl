
package ui.view.managers;

import entities.GameEntity;
import entities.PlayerEntity;
import filing.exporter.Exporter;
import games.Game;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import players.Player;
import tournaments.Tournament;

public class ViewTournamentManager {
    private Exporter ex;
    private PlayerEntity pe = new PlayerEntity();
    private GameEntity ge = new GameEntity();
    private Tournament t;
    
    public ViewTournamentManager(){
        
    }
    
    public File export(Tournament t, File dest){
        this.t = t;
        t.setPlayers(getPlayers());
        t.setGames(getGames());
        
        ex = new Exporter(t, dest);
        
        return ex.export();
    }
    
    private List<Player> getPlayers(){
        List<Player> ps = new ArrayList<>();
        for(HashMap<String, Object> playerRow : pe.selectWhere("tournament_id", "=", t.getId())){
            ps.add(pe.mapRow(playerRow));
        }
        return ps;
    }
    
    private List<Game> getGames(){
        List<Game> games = new ArrayList<>();
        for(HashMap<String, Object> gameRow : ge.selectWhere("tournament_id", "=", t.getId())){
            Game g = ge.mapRow(gameRow);
            g.setTournament(t);
            
            games.add(g);
        }
        return games;
    }
}
