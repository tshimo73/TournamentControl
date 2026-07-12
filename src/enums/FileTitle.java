
package enums;
/*
    I just learned that it's not good to hard code every single value, makes
    it much harder to change later. So this is to make things easier for myself
    incase i want to make changes in the future.
*/
public enum FileTitle {
    TOURNAMENT, PLAYER, GAME, END;
    
    @Override
    public String toString(){
        switch(this){
            case TOURNAMENT: return "TOURNAMENT";
              
            case PLAYER: return "PLAYER";
                
            case GAME: return "GAME";
            
            case END: return "END";
                
            default: return "";
        }
    }
}
