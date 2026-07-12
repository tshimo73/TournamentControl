/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package games;

import net.datafaker.Faker;
import players.Player;
import tournaments.Tournament;


public class GameManager {

    private static final Faker F = new Faker();
    private GameManager(){}
    
    /**
     * Generates a game with custom players
     * @param w
     * @param b
     * @param t
     * @param round
     * @return 
     */
    public static Game generateGame(Player w, Player b, Tournament t, int round){ 
        String o = F.chess().opening();
        Game game = new Game(t, round, w, b, o);
        System.out.println("Game: " + game);
        return game;
    }
    
    /**
     * Generates a game with players
     * @param t
     * @param round
     * @return 
     */
    public static Game generateGameWithPlayers(Tournament t, int round){
        Player w = Player.generatePlayer();
        Player b = Player.generatePlayer();
        return generateGame(w, b, t, round);
    }
    
}
