
package players;

import enums.*;
import utils.*;
import net.datafaker.*;

public class PlayerManager {
    private static final Faker F = new Faker();
    private PlayerManager(){} // Private so the class cannot be instantiated
    
    public static Player generatePlayer(){
        
        int id = Random.getRandomInt(1, 99999999);
        PlayerTitle title = PlayerTitle.getRandomTitle();
        
        Player p = new Player(id, F.name().firstName(), F.name().lastName(),
                          id + "", F.country().countryCode3().toUpperCase(),
                       title.getRating(), title);
        p.setTournamentID(0); // a 'default' value to be changed agian for each generated player
        
        return p;
    }
    
    /**
     * Saves the player to the database under their respective tournament
     * @param p
     * @return 
     */
    public static boolean saveToDB(Player p){
        
        if(p.getTournamentID() == -1){
            System.out.println("Player with FIDE ID: " + p.getFideID() + " is not "
                    + "registered in a tournament.");
            return false;
        } else {
            
            return true;
        }
    }
}
