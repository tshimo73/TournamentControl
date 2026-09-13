package ui.views.tournaments;

import database.DatabaseManager;
import entities.TournamentEntity;
import enums.Federation;
import enums.GameResult;
import filing.exporter.Exporter;
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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import ui.view.managers.ViewTournamentManager;

public class ViewTournament extends javax.swing.JFrame {

    private final Tournament t;
    private Importer importer = null;
    private final TournamentEntity te = new TournamentEntity();
    private final DefaultTableModel model = new DefaultTableModel();
    private ViewTournamentManager vtm;

    /**
     * Creates new form ViewTournaments
     *
     * @param tournament
     */
    public ViewTournament(Tournament tournament) {
        initComponents();
        this.t = tournament;
        vtm = new ViewTournamentManager(t);

        Config.setAttributes(this, tournament.getName());
        lblHeading.setText(t.getName() + "(Rounds: " + t.getRounds() + ")");

        setTournamentStats();

        // leaderboard table
        String[] fields = {"Rank", "Full Name", "FIDE ID", "Rating", "Federation",
            "Score", "Tiebreak", "Wins", "Draws", "Losses"};
        model.setColumnIdentifiers(fields);
        tblLeaderboard.setModel(model);
        getLeaderboard();

    }

    public ViewTournament(Tournament tournament, Importer im) {
        initComponents();
        this.t = tournament;
        this.importer = im;
        vtm = new ViewTournamentManager(t);

        Config.setAttributes(this, "Imported: " + tournament.getName());
        lblHeading.setText(t.getName() + "(Rounds: " + t.getRounds() + ")");

        setImportedTournamentStats();

        // leaderboard table
        String[] fields = {"Rank", "Full Name", "FIDE ID", "Rating", "Federation",
            "Score", "Tiebreak", "Wins", "Draws", "Losses"};
        model.setColumnIdentifiers(fields);
        tblLeaderboard.setModel(model);
        
        List<Player> leaderboard = (List<Player>) vtm.getImportedTournamentStats(t).get("leaderboard");
        getImportedLeaderboard(leaderboard, t.getGames());

    }

    private void setImportedTournamentStats() {
        HashMap<String, Object> stats = vtm.getImportedTournamentStats(t);

        lblAverageRating.setText(String.format("Average Rating: %.2f", (double) stats.get("aveRating")));
        lblDrawPercentage.setText(String.format("Draw Percentage: %.2f%%", (double) stats.get("drawPercentage")));
        lblNumPlayers.setText(String.format("Number of Players: %d", (int) stats.get("numPlayers")));
        lblTotalGames.setText(String.format("Total Games: %d", (int) stats.get("numGames")));
        lblNumNoDraws.setText(String.format("Number of Decisive Games: %d", (int) stats.get("decisiveGames")));
    }

    private void setTournamentStats() {

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

    /**
     * Gets the top 20 (if theres more than 20) players in the tournament
     *
     * @param topX
     */
    private void getLeaderboard() {
        vtm.getLeaderboard(model);
    }

    /**
     * Adds the leaderboard to the table
     * @param ps - the already sorted players list
     * @param gs  - all the games played
     */
    private void getImportedLeaderboard(List<Player> ps, List<Game> gs) {
        HashMap<Player, List<Integer>> wdl = vtm.getWDL(ps, gs);
        int rank = 1;
        for (Player p : ps) {
            model.addRow(new Object[]{
                rank,
                p.getFullName(),
                p.getFideID(),
                p.getRating(),
                Federation.valueOf(p.getFederation()).getCountryName(),
                p.getScore(),
                p.getTieBreak(),
                wdl.get(p).get(0), // wins
                wdl.get(p).get(1), // draws
                wdl.get(p).get(2), // losses
            });
            rank++;

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
        lblStats1 = new javax.swing.JLabel();
        btnExport = new javax.swing.JButton();

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
        btnTournamentTab.setBounds(1080, 20, 230, 40);

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

        lblStats1.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 18)); // NOI18N
        lblStats1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStats1.setText("Statistics");
        lpnlVT.add(lblStats1);
        lblStats1.setBounds(40, 88, 288, 37);

        btnExport.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 24)); // NOI18N
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        lpnlVT.setLayer(btnExport, javax.swing.JLayeredPane.PALETTE_LAYER);
        lpnlVT.add(btnExport);
        btnExport.setBounds(1080, 90, 230, 40);

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
        new ui.TournamentsPage().setVisible(true);
    }//GEN-LAST:event_btnTournamentTabActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        int res = JOptionPane.showConfirmDialog(this, "Do you want to export this tournament?", "Export Tournament Confirmation", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            System.out.println("Exporting tournament...");

            // Again, issues with the JFileChooser, so i needed help to do it manually.
            // 1. Find the parent window safely for the dialog
            java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
            java.awt.Frame parentFrame = null;
            if (parentWindow instanceof java.awt.Frame frame) {
                parentFrame = frame;
            }

            // 2. Open the native file dialog in SAVE mode
            java.awt.FileDialog fileDialog = new java.awt.FileDialog(parentFrame, "Export Tournament", java.awt.FileDialog.SAVE);

            // Set the default file name the user will see
            fileDialog.setFile(t.getName() + "_Export" + filing.exporter.Exporter.FILE_EXTENSION);
            fileDialog.setVisible(true);

            // 3. Grab the directory and filename the user chose
            String directory = fileDialog.getDirectory();
            String filename = fileDialog.getFile();

            if (directory != null && filename != null) {
                // Create the exact file path the user requested
                java.io.File saveLocation = new java.io.File(directory, filename);

                // 4. Pass the location to your updated manager
                java.io.File exportedFile = vtm.export(saveLocation);

                if (exportedFile != null) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Tournament exported successfully to:\n" + saveLocation.getAbsolutePath());
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, "Failed to export the tournament.", "Export Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.out.println("User cancelled the export.");
            }
        }
    }//GEN-LAST:event_btnExportActionPerformed

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
    private javax.swing.JButton btnExport;
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
    private javax.swing.JLabel lblStats1;
    private javax.swing.JLabel lblTotalGames;
    private javax.swing.JLayeredPane lpnlVT;
    private javax.swing.JTable tblLeaderboard;
    // End of variables declaration//GEN-END:variables
}
