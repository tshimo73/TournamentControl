
package enums;

public enum GameResult {
    WHITE_WIN, DRAW, BLACK_WIN, INVALID;
    
    public static GameResult getResultFromScore(String score){
        switch(score) {
            case "1-0": return WHITE_WIN;
            case "0-1": return BLACK_WIN;
            case "0.5-0.5": return DRAW;
            default: return INVALID;
            
        }
    }
    
    public String getScore(){
        switch(this){
            case WHITE_WIN: return "1-0";
            case BLACK_WIN: return "0-1";
            case DRAW: return "0.5-0.5";
            default: return "Invalid Game Result";
        }
    }
    
}