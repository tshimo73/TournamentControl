
package filing.exporter;

import games.Game;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import players.Player;
import tournaments.Tournament;

/*
    File Layout Planning (for reference)

    .tctrl (Tournament Control)

    Example 1:

    (id, name, director, chief arbiter, deputy chief arbiter, tournament type id, time control id, location, num rounds, start date, end date)
    TOURNAMENT#1#SOUTH AFRICAN CHESS CHAMPIONSHIPS#RSA#Ghale Mogale#JAMES BOHELAVARD#DANILE DUMILE#2#3#UCT CAMPUS 1#5#2026-07-29T08:00:00#2026-07-30T18:30:00

    (id, first name, last name, fideid, rating, federation)
    PLAYER#1#NM#Keegan#Kornei#198382742#2143#RSA

    (id, tournament id, round, white player id, black player id, result, opening)
    GAME#4#1#1#1#6#1-0#Sicilian Defense

 */

/*
    NOTE: THIS WILL ONLY BE SET UP TO ACCEPT 1 TOURNAMENT PER FILE!!!!! To spare myself the difficulty
*/

/**
 * The Exporter module. Exports all tournament data for the user to save.
 * @author tshim
 */
public class Exporter {
    public static final String FILE_EXTENSION = ".tctrl";
    
    private final List<Game> games;
    private final List<Player> players;
    private final Tournament t;
    private final File file;
    
    
    public Exporter(Tournament tm, File dest){
        t = tm;
        games = tm.getGames();
        players = tm.getPlayers();
        file = dest;
    }
    
    public File export(){
        PrintWriter writer = null;
        
        try {
            writer = new PrintWriter(new FileWriter(file));
            
            // print details
            writer.println(t);
            
            for (Player p : players) {
                writer.println(p);
            }
            
            for (Game g : games) {
                writer.println(g);
            }
            
            writer.print("END");
        } catch (IOException ex) {
            Logger.getLogger(Exporter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            if(writer != null) writer.close();
        }
        
        return file;
    }
}
