/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import games.Game;
import java.util.List;
import java.util.Map;
import tournaments.*;
import database.DatabaseManager;
import enums.GameResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TournamentEntity extends Entity<Tournament> {

    private final DatabaseManager dm = new DatabaseManager();
    private final PlayerEntity pe = new PlayerEntity();

    public TournamentEntity() {
        super(Tournament.class);
    }

    public List<Game> getGames(int id) {
        List<Game> games = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tblGames WHERE tournament_id = ?";
            PreparedStatement stmt = dm.getConn().prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            if(meta.getColumnCount() >= 1){
                while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                    System.out.println(meta.getColumnName(i) + ": " + rs.getObject(i));
                }
                
                Game game = mapGame(row);
                games.add(game);

            }
            } else {
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TournamentEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return games;
    }

    @Override
    protected Tournament mapRow(Map<String, Object> row) {
        Tournament t = new Tournament(
                (String) row.get("name"),
                (String) row.get("federation"),
                (String) row.get("director"),
                (String) row.get("chief_arbiter"),
                (String) row.get("deputy_chief_arbiter"),
                TournamentType.getTypeByID(((Number) row.get("tournament_type_id")).intValue()),
                ((java.sql.Timestamp) row.get("start_date")).toLocalDateTime(),
                ((java.sql.Timestamp) row.get("end_date")).toLocalDateTime()
        );
        
        t.setId((int) row.get("id"));
        
        return t;
    }

    private Game mapGame(Map<String, Object> row) {
        Game game = new Game();

        game.setBlack(pe.find((int) row.get("black_player_id")));
        game.setWhite(pe.find((int) row.get("white_player_id")));

        String result = String.join("-", (String) row.get("white_result"), (String) row.get("black_result"));
        game.setResult(GameResult.getResultFromScore(result));
        game.setRound((int) row.get("round_number"));

        return game;

    }

}
