package ui.views.tournaments;

import database.DatabaseManager;
import entities.TournamentEntity;
import enums.GameResult;
import filing.importer.Importer;
import games.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jframeconfig.Config;
import players.Player;
import tournaments.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class ViewTournament extends javax.swing.JFrame {

    private final Tournament t;
    private Importer importer;
    private final TournamentEntity TE = new TournamentEntity();
    private final DefaultTableModel MODEL = new DefaultTableModel();

    /**
     * Creates new form ViewTournaments
     *
     * @param tournament
     */
    public ViewTournament(Tournament tournament) {
        initComponents();
        this.t = tournament;

        Config.setAttributes(this, tournament.getName());
        lblHeading.setText(t.getName() + "(Rounds: " + t.getRounds() + ")");

        setTournamentStats();
        
        // leaderboard table
        String[] fields = {"Rank", "Full Name", "FIDE ID", "Rating", "Federation", 
                           "Score", "Tiebreak", "Wins", "Draws", "Losses"};
        MODEL.setColumnIdentifiers(fields);
        tblLeaderboard.setModel(MODEL);
        getLeaderboard();
        
    }

    public void setTournamentStats() {

        try {
            int numGames = 0, numPlayers = 0,
                    numWWins = 0, numBWins = 0, numDraws = 0;
            double drawPercentage = 0.0, aveRating = 0.0;
            Statement stmt = DatabaseManager.getConn().createStatement();

            // necesary sql queries
            String sqlGames = String.format("SELECT result, count(result) AS [NumGames]"
                    + " FROM tblGames"
                    + " WHERE tournament_id = \"%s\""
                    + " GROUP BY result", t.getId()),
                    sqlPlayers = String.format("SELECT count(id) AS [NumPlayers], ROUND(AVG(rating), 2) AS [AvgRating]"
                            + " FROM tblRegistrations"
                            + " WHERE tournament_id = \"%s\"", t.getId());

            ResultSet rsGames = stmt.executeQuery(sqlGames), rsPlayers = stmt.executeQuery(sqlPlayers);

            // the game query
            while (rsGames.next()) {
                String result = rsGames.getString("result");
                int wins = rsGames.getInt("NumGames");

                switch (result) {
                    case "1-0":
                        numWWins = wins;
                        break;

                    case "0-1":
                        numBWins = wins;
                        break;

                    case "0.5-0.5":
                        numDraws = wins;
                        break;

                }
            }
            numGames = numWWins + numBWins + numDraws;
            drawPercentage = numGames > 0 ? ((double) numDraws / (double) numGames) * 100.0 : 0.0; // had to do an if statement, got NaN error (case divided by 0)

            // players qery
            if (rsPlayers.next()) {
                numPlayers = rsPlayers.getInt("NumPlayers");
                aveRating = rsPlayers.getDouble("AvgRating");
            }

            // Set the stats
            lblAverageRating.setText(String.format("Average Rating: %.2f", aveRating));
            lblDrawPercentage.setText(String.format("Draw Percentage: %.2f%%", drawPercentage));
            lblNumPlayers.setText(String.format("Number of Players: %d", numPlayers));
            lblTotalGames.setText(String.format("Total Games: %d", numGames));
            lblNumNoDraws.setText(String.format("Number of Decisive Games: %d", numWWins + numBWins));
        } catch (SQLException ex) {
            Logger.getLogger(ViewTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setGamesFromDB() {
        if (t.getIsImported()) {
            return;
        }
        System.out.println("Seeding tournament with games from Database...");
        List<Game> gs = TE.getGames(t.getId());

        if (!gs.isEmpty()) {
            t.setGames(gs);
            System.out.printf("%d games found and added!", gs.size());
        } else {
            System.out.println("No games were found for this tournament");
        }

    }

    /**
     * Gets the top 20 (if theres more than 20) players in the tournament
     *
     * @param topX
     */
    private void getLeaderboard() {
        try {
            // used embedded queries because i had two player id fields so creating
            // the link was a hassle. embeddeds was easy
            Statement stmt = DatabaseManager.getConn().createStatement();
            String sql = String.format("SELECT TOP 20 first_name & \" \" & last_name AS [FullName], "
                    + "fide_id, rating, federation, score, tiebreak, "
                    
                    // win embedded query
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.id = tblGames.black_player_id AND result = \"%s\")"
                    + ")) AS [Wins], "
                    
                    // draw embedded
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.id = tblGames.black_player_id AND result = \"%s\")"
                    + ")) AS [Draws], "
                    
                    // loss embedded
                    + "(SELECT COUNT(id) FROM tblGames WHERE tournament_id = \"%s\" "
                    + "AND ("
                    + "(tblRegistrations.id = tblGames.white_player_id AND result = \"%s\") "
                    + "OR (tblRegistrations.id = tblGames.black_player_id AND result = \"%s\")"
                    + ")) AS [Losses] "
                    
                    // rest of the query
                    + "FROM tblRegistrations WHERE tournament_id = \"%s\" "
                    + "ORDER BY score DESC, tiebreak DESC",
                    t.getId(), GameResult.WHITE_WIN.getScore(), GameResult.BLACK_WIN.getScore(), // win vars
                    t.getId(), GameResult.DRAW.getScore(), GameResult.DRAW.getScore(), // draw vars
                    t.getId(), GameResult.BLACK_WIN.getScore(), GameResult.WHITE_WIN.getScore(), // loss vars
                    t.getId()); // for the final where clause

            ResultSet rs = stmt.executeQuery(sql);

            int rank = 1;
            while (rs.next()) {
                MODEL.addRow(new Object[]{
                    rank,
                    rs.getString("FullName"),
                    rs.getString("fide_id"),
                    rs.getDouble("rating"),
                    rs.getString("federation"),
                    rs.getDouble("score"),
                    rs.getDouble("tiebreak"),
                    rs.getInt("Wins"),
                    rs.getInt("Draws"),
                    rs.getInt("Losses")
                });
                rank++;
            }

        } catch (SQLException ex) {
            System.out.println("Failed to get leaderboard");
            Logger.getLogger(ViewTournament.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lpnlVT = new javax.swing.JLayeredPane();
        lblPOTT = new javax.swing.JLabel();
        btnHomeTab = new javax.swing.JButton();
        btnTournamentTab = new javax.swing.JButton();
        lblHeading = new javax.swing.JLabel();
        lblStats = new javax.swing.JLabel();
        lblNumPlayers = new javax.swing.JLabel();
        lblTotalGames = new javax.swing.JLabel();
        lblNumNoDraws = new javax.swing.JLabel();
        lblDrawPercentage = new javax.swing.JLabel();
        lblAverageRating = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLeaderboard = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblPOTT.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 18)); // NOI18N
        lblPOTT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPOTT.setText("Leaderboard");
        lpnlVT.add(lblPOTT);
        lblPOTT.setBounds(502, 88, 288, 37);

        btnHomeTab.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 24)); // NOI18N
        btnHomeTab.setText("Home");
        btnHomeTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeTabActionPerformed(evt);
            }
        });
        lpnlVT.setLayer(btnHomeTab, javax.swing.JLayeredPane.PALETTE_LAYER);
        lpnlVT.add(btnHomeTab);
        btnHomeTab.setBounds(54, 16, 230, 40);

        btnTournamentTab.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 24)); // NOI18N
        btnTournamentTab.setText("Tournaments");
        btnTournamentTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTournamentTabActionPerformed(evt);
            }
        });
        lpnlVT.setLayer(btnTournamentTab, javax.swing.JLayeredPane.PALETTE_LAYER);
        lpnlVT.add(btnTournamentTab);
        btnTournamentTab.setBounds(1096, 16, 230, 40);

        lblHeading.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 24)); // NOI18N
        lblHeading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lpnlVT.add(lblHeading);
        lblHeading.setBounds(296, 16, 749, 37);

        lblStats.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 18)); // NOI18N
        lblStats.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStats.setText("Statistics");
        lpnlVT.add(lblStats);
        lblStats.setBounds(40, 88, 288, 37);

        lblNumPlayers.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 14)); // NOI18N
        lblNumPlayers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNumPlayers.setText("Statistics");
        lpnlVT.add(lblNumPlayers);
        lblNumPlayers.setBounds(40, 154, 371, 37);

        lblTotalGames.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 14)); // NOI18N
        lblTotalGames.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalGames.setText("Statistics");
        lpnlVT.add(lblTotalGames);
        lblTotalGames.setBounds(40, 220, 371, 37);

        lblNumNoDraws.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 14)); // NOI18N
        lblNumNoDraws.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNumNoDraws.setText("Statistics");
        lpnlVT.add(lblNumNoDraws);
        lblNumNoDraws.setBounds(40, 286, 371, 37);

        lblDrawPercentage.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 14)); // NOI18N
        lblDrawPercentage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDrawPercentage.setText("Statistics");
        lpnlVT.add(lblDrawPercentage);
        lblDrawPercentage.setBounds(40, 352, 371, 37);

        lblAverageRating.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 14)); // NOI18N
        lblAverageRating.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAverageRating.setText("Statistics");
        lpnlVT.add(lblAverageRating);
        lblAverageRating.setBounds(40, 418, 371, 37);

        tblLeaderboard.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblLeaderboard);

        lpnlVT.add(jScrollPane1);
        jScrollPane1.setBounds(417, 154, 898, 402);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lpnlVT)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lpnlVT)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHomeTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeTabActionPerformed
        this.dispose();
        new ui.Main().setVisible(true);
    }//GEN-LAST:event_btnHomeTabActionPerformed

    private void btnTournamentTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTournamentTabActionPerformed
        this.dispose();
        new ui.tabbedPanels.TournamentsPage().setVisible(true);
    }//GEN-LAST:event_btnTournamentTabActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt, javax.swing.JButton btn) {
        if (importer.saveToDB()) {
            btn.setVisible(false);
        }
    }


    public void setImporter(Importer im) {
        importer = im;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ViewTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewTournament.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ViewTournament(new Tournament()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHomeTab;
    private javax.swing.JButton btnTournamentTab;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAverageRating;
    private javax.swing.JLabel lblDrawPercentage;
    private javax.swing.JLabel lblHeading;
    private javax.swing.JLabel lblNumNoDraws;
    private javax.swing.JLabel lblNumPlayers;
    private javax.swing.JLabel lblPOTT;
    private javax.swing.JLabel lblStats;
    private javax.swing.JLabel lblTotalGames;
    private javax.swing.JLayeredPane lpnlVT;
    private javax.swing.JTable tblLeaderboard;
    // End of variables declaration//GEN-END:variables
}
