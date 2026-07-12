/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package players;

import enums.*;
import utils.*;
import net.datafaker.*;

public class PlayerManager {
    private static final Faker F = new Faker();
    private PlayerManager(){}
    
    public static Player generatePlayer(){
        
        int id = Random.getRandomInt(1, 99999999);
        PlayerTitle title = PlayerTitle.getRandomTitle();
        
        return new Player(id, F.name().firstName(), F.name().lastName(),
                          id + "", F.country().countryCode3().toUpperCase(),
                       title.getRating(), title);
    }
}
