/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ui;

import filing.*;
import filing.exporter.Exporter;
import filing.importer.Importer;
import games.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import matchmaking.MatchMaker;
import players.*;
import tournaments.*;

public class MainForTerminal {
    public static void main(String[] args){
        try {
            File f = new File("South African Chess Championships_Export.tctrl");
            System.out.println("==================================================================");
            System.out.println("Imported Tournament");
            Importer i = new Importer(f);
            i.scanFile();
            Tournament loadedT = i.getLoadedTournament();
            List<Game> loadedGames = loadedT.getGames();
            List<Player> loadedPlayers = loadedT.getPlayers(), leaderboard = loadedT.getLeaderBoard();
            
            System.out.println(loadedT);
            System.out.println("");
            System.out.println("Games:");
            for(Game game : loadedGames) System.out.println(game);
            
            System.out.println("");
            System.out.println("Players:");
            for(Player player : loadedPlayers) System.out.println(player);
            System.out.println("");
            
            i.saveToDB();
            
            
        } catch (InvalidFileExtensionException ex) {
            System.out.println(ex.getMessage());
        }
        
        int a = 3, b = 4, c = 5;
        
        int l = (a == b) ? 4 : (b == c) ? 5 : 8;
    }
}
