package games;

import enums.GameResult;
import players.Player;
import tournaments.Tournament;

public class Game {

    private int id, round;
    private Tournament tournament;
    private Player white, black;
    private String opening;
    private GameResult result;
    private boolean hasEnded = false;

    public Game(Tournament t, int r, Player w, Player b, String o) {
        this.tournament = t;
        this.round = r;
        this.white = w;
        this.black = b;
        this.opening = o;
    }

    public Game() {
    }

    /**
     * Randomly generates the game's results
     *
     * @return the game
     */
    public Game generateResult() {
        return GameManager.generateResult(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GAME").append("#");
        sb.append(id).append("#");
        sb.append(round).append("#");
        sb.append(tournament.getId()).append("#");
        sb.append(white.getFideID()).append("#");
        sb.append(black.getFideID()).append("#");
        sb.append(result).append("#");
        sb.append(opening);
        return sb.toString();
    }

    public boolean hasEnded() {
        return hasEnded;
    }

    public boolean isDraw() {
        return result.equals(GameResult.DRAW);
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Player getWhite() {
        return white;
    }

    public void setWhite(Player white) {
        this.white = white;
    }

    public Player getBlack() {
        return black;
    }

    public void setBlack(Player black) {
        this.black = black;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
        hasEnded = true;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

}
