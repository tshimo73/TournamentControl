
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
        p.setTournamentID(null);
        return p;
    }
    
}
