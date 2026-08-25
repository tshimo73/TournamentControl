/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package database;

/**
 *
 * @author tshim
 */
public enum PlayerFields {
    TOURNAMENT_ID("tournament_id"),
    TITLE("title"),
    FIRST_NAME("first_name"),
    LAST_NAME("last_name"),
    FIDE_ID("fide_id"),
    RATING("rating"),
    FEDERATION("federation"),
    SCORE("score"),
    TIEBREAK("tiebreak");
    
    private final String name;
    
    PlayerFields(String name){
        this.name = name;
    }
    
    @Override
    public String toString(){
        return this.name.toLowerCase();
    }
    
}
