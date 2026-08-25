/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ui;

import database.*;
import entities.*;
import enums.*;
import filing.*;
import games.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jframeconfig.*;
import matchmaking.*;
import players.*;
import tournaments.*;
import utils.*;

public class MainTerminal {
    public static void main(String[] args){
        DatabaseManager.init();
        
        PlayerEntity pe = new PlayerEntity();
        
        List<HashMap<String, Object>> players = pe.selectWhere(PlayerFields.FEDERATION, 
                "LIKE", "T*", PlayerFields.FEDERATION, PlayerFields.FIRST_NAME);
        
        System.out.println(players.get(5).get(PlayerFields.FEDERATION.toString()));
        
        DatabaseManager.closeConn();
    }
}
