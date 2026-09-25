/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package games;

import enums.GameResult;
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
        return game;
    }
    
    public static Game generateResult(Game g){
        boolean hasEnded = g.hasEnded();
        Player white = g.getWhite(), black = g.getBlack();
        if (!hasEnded) {
            String score;
            double res = Math.random(), whiteCurrentPoints = white.getScore(),
                    blackCurrentPoints = black.getScore(), wTB = white.getTieBreak(),
                    bTB = black.getTieBreak();

            if (res > 0.55) { // 45% chance to win-- white players first
                score = "1-0"; // white wins
                white.setScore(whiteCurrentPoints + 1);
                white.setTieBreak(wTB + black.getRating());
            } else if (res >= 0.35) {
                score = "0.5-0.5"; // they game has been drawn
                black.setScore(blackCurrentPoints + 0.5);
                white.setScore(whiteCurrentPoints + 0.5);

                black.setTieBreak(bTB + white.getRating() * 0.5);
                white.setTieBreak(wTB + black.getRating() * 0.5);
            } else {
                score = "0-1"; // black wins
                black.setScore(blackCurrentPoints + 1);
                black.setTieBreak(bTB + white.getRating());
            }

            g.setResult(GameResult.getResultFromScore(score));
            hasEnded = true;
            return g;
        } else {
            return g;
        }
    }
}
