package matchmaking;

import games.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import players.*;
import tournaments.Tournament;
import entities.PlayerEntity;

/**
 * The MatchMaker class handles all the matchmaking and pairings of the
 * tournament
 *
 * @author tshim
 */
public class MatchMaker {

    private int round;
    protected List<Player> players;
    private final Map<Player, List<Player>> previousOpponents;
    private final Map<Player, Integer> colourBalance;
    private final Map<Player, Boolean> hasRecievedBye;
    private final PlayerEntity PE = new PlayerEntity();
    private HashMap<Integer, List<Player>> leaderboardPerRound = new HashMap<>();
    private List<Game> games = new ArrayList<>();

    public MatchMaker(List<Player> ps, int round) {
        this.players = ps;
        this.round = round;
        this.previousOpponents = new HashMap<>();
        this.colourBalance = new HashMap<>();
        this.hasRecievedBye = new HashMap<>();

        for (Player p : players) {
            previousOpponents.put(p, new ArrayList<>());
            colourBalance.put(p, 0);
            hasRecievedBye.put(p, false);
        }
    }

    /**
     * Generates a Tournament round.
     *
     * @param t the tournament
     *
     * @return the games generated for that round
     */
    public List<Game> generateRound(Tournament t) {
        List<Game> roundGames = new ArrayList<>();
        List<Player> availablePlayers = new ArrayList<>(players);

        // 1. A BYE only ever happens if the total player count is ODD!
        // At most ONE player per round can receive a BYE.
        if (availablePlayers.size() % 2 != 0) {
            Player byeCandidate = selectByePlayer(availablePlayers);
            if (byeCandidate != null) {
                assignBye(byeCandidate);
                availablePlayers.remove(byeCandidate);
            }
        }

        // 2. Build score groups ONLY from the remaining even pool of players
        List<List<Player>> scoreGroups = buildScoreGroups(availablePlayers);
        List<Player> leftover = new ArrayList<>();

        for (List<Player> group : scoreGroups) {
            // Carry down unpaired players from upper score bracket
            group.addAll(0, leftover);
            leftover.clear();

            // Pair players within this group and collect anyone who could not be paired
            List<Player> unpartnered = new ArrayList<>();
            roundGames.addAll(pairGroup(group, t, unpartnered));

            // Any leftovers cascade down to the next score bracket
            leftover.addAll(unpartnered);
        }

        // 3. Fallback: If strict group matching left an unresolved pair, force-pair them
        while (leftover.size() >= 2) {
            Player p1 = leftover.remove(0);
            Player p2 = leftover.remove(0);
            roundGames.add(createGame(p1, p2, t));
        }

        // my code
        updateScoresAndTieBreaks();
        leaderboardPerRound.put(round, getLeaderBoardSnapshot());
        games.addAll(roundGames);

        return roundGames;
    }

    /**
     * Selects the lowest-ranked player who hasn't had a BYE yet.
     */
    private Player selectByePlayer(List<Player> candidates) {
        // Sort ascending (lowest score first)
        List<Player> sorted = new ArrayList<>(candidates);
        sorted.sort((p1, p2) -> Double.compare(p1.getScore(), p2.getScore()));

        for (Player p : sorted) {
            if (!hasRecievedBye.getOrDefault(p, false)) {
                return p;
            }
        }
        // If everyone somehow had a bye, pick the lowest ranked player
        return sorted.get(0);
    }

    /**
     * Gives the unpaired player a BYE
     *
     * @param p
     */
    private void assignBye(Player p) {
        // In chess, a BYE gives 1.0 point (win by forfeit/no pairing)
        p.setScore(p.getScore() + 1.0);
        hasRecievedBye.put(p, true);
        System.out.println("Player " + p.getFullName() + " received a BYE for Round " + round);
    }

    private List<Game> pairGroup(List<Player> group, Tournament t, List<Player> unpartnered) {
        List<Game> games = new ArrayList<>();
        List<Player> pool = new ArrayList<>(group);

        while (pool.size() >= 2) {
            Player a = pool.remove(0);
            Player bestMatch = null;
            int bestMatchIndex = -1;

            // Find first player 'a' hasn't played against yet
            for (int j = 0; j < pool.size(); j++) {
                Player candidate = pool.get(j);
                if (!hasPlayed(a, candidate)) {
                    bestMatch = candidate;
                    bestMatchIndex = j;
                    break;
                }
            }

            // If no unique opponent found in group, pair with the next available
            if (bestMatch == null) {
                // If this is the only pair left, accept rematch or mark for cascade
                if (pool.size() == 1) {
                    bestMatch = pool.get(0);
                    bestMatchIndex = 0;
                } else {
                    // Send player 'a' to cascade down to the next bracket
                    unpartnered.add(a);
                    continue;
                }
            }

            pool.remove(bestMatchIndex);
            games.add(createGame(a, bestMatch, t));
        }

        // Remaining odd player gets passed down
        unpartnered.addAll(pool);
        return games;
    }

    private Game createGame(Player a, Player b, Tournament t) {
        previousOpponents.get(a).add(b);
        previousOpponents.get(b).add(a);

        Game game = assignColours(a, b, t);
        game.setRound(round);
        return game.generateResult();
    }

    /**
     * Have the players played against each other
     *
     * @param a - player 1
     * @param b - player 2
     * @return yes/no
     */
    private boolean hasPlayed(Player a, Player b) {
        return previousOpponents.get(a) != null && previousOpponents.get(a).contains(b);
    }

    /**
     * Assigns the players colours
     *
     * @param a - player 1
     * @param b - player 2
     * @param t - the tournament the game is played in
     * @return the game
     */
    private Game assignColours(Player a, Player b, Tournament t) {
        int balanceA = colourBalance.getOrDefault(a, 0);
        int balanceB = colourBalance.getOrDefault(b, 0);

        Player white, black;

        if (balanceA < balanceB) {
            white = a;
            black = b;
        } else if (balanceA > balanceB) {
            white = b;
            black = a;
        } else {
            white = a.getRating() >= b.getRating() ? a : b;
            black = (white == a) ? b : a;
        }

        colourBalance.put(white, colourBalance.get(white) + 1);
        colourBalance.put(black, colourBalance.get(black) - 1);

        return GameManager.generateGame(white, black, t, round);
    }

    /**
     * Groups players based on their score
     *
     * @param playerPool - the players
     * @return list of players in their score groups
     */
    private List<List<Player>> buildScoreGroups(List<Player> playerPool) {
        Player[] sorted = playerPool.toArray(new Player[0]);

        for (int i = 0; i < sorted.length - 1; i++) {
            for (int j = i + 1; j < sorted.length; j++) {
                if (sorted[i].getScore() == sorted[j].getScore()) {
                    if (sorted[i].getTieBreak() < sorted[j].getTieBreak()) {
                        Player temp = sorted[i];
                        sorted[i] = sorted[j];
                        sorted[j] = temp;
                    }
                } else if (sorted[i].getScore() < sorted[j].getScore()) {
                    Player temp = sorted[i];
                    sorted[i] = sorted[j];
                    sorted[j] = temp;
                }
            }
        }

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
            groups.add(current);
        }
        return groups;
    }

    /**
     * Gets the leaderboard
     * @return 
     */
    private List<Player> getLeaderBoard() {
        Player[] leaderboard = players.toArray(new Player[0]);
        for (int i = 0; i < leaderboard.length - 1; i++) {
            for (int j = i + 1; j < leaderboard.length; j++) {
                if (leaderboard[i].getScore() == leaderboard[j].getScore()) {
                    if (leaderboard[i].getTieBreak() < leaderboard[j].getTieBreak()) {
                        Player temp = leaderboard[i];
                        leaderboard[i] = leaderboard[j];
                        leaderboard[j] = temp;
                    }
                } else if (leaderboard[i].getScore() < leaderboard[j].getScore()) {
                    Player temp = leaderboard[i];
                    leaderboard[i] = leaderboard[j];
                    leaderboard[j] = temp;
                }
            }
        }
        return List.of(leaderboard);
    }

    /**
     * Gets the leaderboard of a specific round
     * @param round - the round played
     * @return  - the leaderboard
     */
    public List<Player> getLeaderboardForRound(int round) {
        return leaderboardPerRound.get(round);
    }

    /*
    Had an issue when getting the leaderboards of certain rounds - the scores 
    and tiebreaks only reflected the latest rounds. 
    It turns out that java updates those same player objects with the updates rounds
    since its in memory, so i had to create a 'snapshot' of the leaderboards
     */
    
    /**
     * Returns a snapshot of the leaderboard
     * @return 
     */
    private List<Player> getLeaderBoardSnapshot() {
        List<Player> snap = new ArrayList<>();

        for (Player p : getLeaderBoard()) {
            snap.add(new Player(p));
        }

        return snap;
    }

    /**
     * Calculates Buchholz tiebreak scores (sum of opponents' scores) and
     * persists changes using PlayerEntity's existing update method.
     */
    private void updateScoresAndTieBreaks() {
        for (Player p : players) {
            double buchholz = 0.0;
            List<Player> opponents = previousOpponents.getOrDefault(p, new ArrayList<>());

            for (Player opponent : opponents) {
                buchholz += opponent.getScore();
            }

            p.setTieBreak(buchholz);

            // Use the existing Entity update method with PlayerFields enum keys
            HashMap<String, Object> attrs = new HashMap<>();
            attrs.put("score", p.getScore());
            attrs.put("tiebreak", p.getTieBreak());

            PE.update(p.getId(), attrs);
        }

        System.out.println("Updated scores and tiebreaks");
    }

    /**
     * Returns all the games simulated in the tournament (Mainly for the
     * Exporter class)
     *
     * @return
     */
    public List<Game> getGames() {
        return games;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
