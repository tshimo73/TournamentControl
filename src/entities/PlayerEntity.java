/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package entities;

import java.util.Map;
import players.*;
import enums.PlayerTitle;

public class PlayerEntity extends Entity<Player> {
    
    public PlayerEntity() {
        super(Player.class, "tblRegistrations");
    }
    
    @Override
    protected Player mapRow(Map<String, Object> row){
        Player p = new Player();
        
        p.setId((int) row.get("id"));
        p.setFirstName((String) row.get("first_name"));
        p.setLastName((String) row.get("last_name"));
        p.setFideID((String) row.get("fide_id"));
        p.setFederation((String) row.get("federation"));
        p.setRating(((Number) row.get("rating")).doubleValue());
        
        String title = (String) row.get("title");
        p.setTitle(title.equals("N/A") ? PlayerTitle.NoTitle : PlayerTitle.valueOf(title));
        
        return p;
    }
}
