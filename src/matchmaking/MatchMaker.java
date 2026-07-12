/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package matchmaking;

import games.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import players.*;
import tournaments.Tournament;
import utils.*;

import java.util.Comparator;

public class MatchMaker {
    int round = 1;
    protected List<Player> players; // players
    private final Map<Player, List<Player>> previousOpponents; // key: player, value: their opponents
    private final Map<Player, Integer> colourBalance; // +1 white, -1 black
    private final Map<Player, Boolean> hasRecievedBye; // has the player recieved a BYE?

    public MatchMaker(List<Player> ps, int round) {
        this.players = ps;
        this.round = round;
        this.previousOpponents = new HashMap<>();
        this.colourBalance = new HashMap<>();
        this.hasRecievedBye = new HashMap<>();

        // initialise maps for each player
        for (Player p : players) {
            previousOpponents.put(p, new ArrayList<>());
            colourBalance.put(p, 0);
            hasRecievedBye.put(p, false);
        }
    }
    
    

    public List<Game> generateRound(Tournament t) {
        List<List<Player>> scoreGroups = buildScoreGroups();
        List<Game> roundGames = new ArrayList<>();
        List<Player> leftover = new ArrayList<>();
        
        for(List<Player> group : scoreGroups){
            // take previous group left over
            group.addAll(0, leftover);
            leftover.clear();
            
            //if group isnt even, pull lowest player to the next group
            if(group.size() % 2 != 0){
                leftover.add(group.remove(group.size() - 1));
            }
            
            roundGames.addAll(pairGroup(group, t));
        }
        
        // whoever is left gets a bye
        if(!leftover.isEmpty()) {
            assignBye(leftover.get(0));
        }
         System.out.println(roundGames);
        return roundGames;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    private List<List<Player>> buildScoreGroups() {
        // sort by score decending, rating desc as tiebreaker
        List<Player> sorted = players;

        sorted.sort((a, b) -> {
            if (b.getScore() != a.getScore()) {
                return Double.compare(b.getScore(), a.getScore());
            }
            return Double.compare(a.getRating(), b.getRating());
        });

        //group players with the same score together
        List<List<Player>> groups = new ArrayList<>();
        List<Player> current = new ArrayList<>();

        for (Player p : sorted) {
            if (!current.isEmpty() && p.getScore() != current.get(0).getScore()) {
                groups.add(current);
                current = new ArrayList<>();
            }
            current.add(p);
        }
        if (!current.isEmpty()) {
            groups.add(current); // add last group
        }
        return groups;

    }

    private List<Game> pairGroup(List<Player> group, Tournament t) {
        List<Game> games = new ArrayList<>();
        List<Player> unpaired = new ArrayList<>(group);

        // fold pairing
        int mid = unpaired.size() / 2;

        List<Player> top = new ArrayList<>(unpaired.subList(0, mid));
        List<Player> bottom = new ArrayList<>(unpaired.subList(mid, unpaired.size()));

        for (int i = 0; i < top.size(); i++) {
            Player a = top.get(i);
            Player b = bottom.get(i);

            // rematch check -- if they played, swap b with the player next in line
            if (hasPlayed(a, b)) {
                boolean resolved = false;

                for (int j = i + 1; j < bottom.size(); j++) {
                    if (!hasPlayed(a, bottom.get(j))) {
                        // then swap players
                        Player temp = bottom.get(i);
                        bottom.set(i, bottom.get(j));
                        bottom.set(j, temp);
                        b = bottom.get(i);
                        resolved = true;
                        break;
                    }
                }

                if (!resolved) {
                    //cant resolve in this group, 
                    continue;
                }
            }

            previousOpponents.get(a).add(b);
            previousOpponents.get(b).add(a);
            
            // assign colours and make game
            
            Game game = assignColours(a, b, t);
            game.setRound(round);
            games.add(game.generateResult());
            
        }
        return games;
    }

    private boolean hasPlayed(Player a, Player b) {
        return previousOpponents.get(a).contains(b);
    }

    private void assignBye(Player p) {
        if(!hasRecievedBye.get(p)){
            p.setScore(p.getScore() + 0.5);
            hasRecievedBye.put(p, true);
        }
    }   

    private Game assignColours(Player a, Player b, Tournament t) {
        int balanceA = colourBalance.get(a);
        int balanceB = colourBalance.get(b);
        
        Player white, black;
        
        if(balanceA < balanceB){
            // a has played more black, then give them white
            white = a;
            black = b;
        } else if (balanceA > balanceB){
            // b has played more black, give them white
            white = b;
            black = a;
        } else {
            // equal balance
            white = a.getRating() >= b.getRating() ? a : b;
            black = white == a ? b : a;
        }
        
        colourBalance.put(white, colourBalance.get(white) + 1);
        colourBalance.put(black, colourBalance.get(black) - 1);
        
        return GameManager.generateGame(white, black, t, round); 
    }
}
