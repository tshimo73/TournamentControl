package games;

import enums.GameResult;
import players.Player;
import tournaments.Tournament;

public class Game {

    protected int id;
    protected int round;
    protected Tournament tournament;
    protected Player white, black;
    protected String opening;
    protected GameResult result;
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

            setResult(GameResult.getResultFromScore(score));
            hasEnded = true;
            return this;
        } else {
            return this;
        }
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
