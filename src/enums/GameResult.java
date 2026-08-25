
package enums;

public enum GameResult {
    WHITE_WIN("1-0"),
    DRAW("0.5-0.5"),
    BLACK_WIN("0-1"),
    INVALID("Invalid Game Result");

    private final String score;

    GameResult(String score) {
        this.score = score;
    }

    public String getScore() {
        return this.score;
    }

    public static GameResult getResultFromScore(String score) {
        switch (score.trim()) {
            case "1-0": 
                return WHITE_WIN;
            case "0-1": 
                return BLACK_WIN;
            case "0.5-0.5":
                return DRAW;
            default: 
                return INVALID;
        }
    }

    @Override
    public String toString() {
        return this.score;
    }
}