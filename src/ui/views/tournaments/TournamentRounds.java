/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui.views.tournaments;

import entities.PlayerEntity;
import entities.TournamentEntity;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import matchmaking.MatchMaker;
import players.Player;
import tournaments.Tournament;
import entities.GameEntity;
import games.Game;
import java.util.HashMap;
import javax.swing.JOptionPane;
import jframeconfig.Config;

/**
 *
 * @author tshim
 */
public class TournamentRounds extends javax.swing.JFrame {

    private final TournamentEntity TE = new TournamentEntity();
    private final PlayerEntity PE = new PlayerEntity();
    private final GameEntity GE = new GameEntity();
    private Tournament t;
    private final DefaultTableModel MODEL_ROUNDS = new DefaultTableModel(),
            MODEL_LEADERBOARD = new DefaultTableModel();
    private MatchMaker mm;
    private int currRound = 1, maxRounds;
    private HashMap<Integer, List<Game>> gamesForEachRound = new HashMap<>();
    private boolean hasEnded = false;

    /**
     * Creates new form TournamentRounds
     */
    public TournamentRounds(Tournament t) {
        initComponents();
        Config.setAttributes(this, "Tournament Rounds");

        this.t = t;

        // set the players who registered into the tournament from the database
        setRegisteredPlayers();

        lblHeading.setText(t.getName());
        maxRounds = t.getRounds();

        // initialise the matchmaker
        initMatchMaker();

        //progressbar setup
        pBarRounds.setMaximum(maxRounds);
        pBarRounds.setMinimum(currRound); // which is one at this stage(defaulted)
        pBarRounds.setValue(currRound);

        // initialise tables
        String[] roundFields = {"Round Number", "White Player", "Black Player", "Result", "Opening"};
        MODEL_ROUNDS.setColumnIdentifiers(roundFields);
        tblRounds.setModel(MODEL_ROUNDS);

        String[] leaderboardFields = {"Rank", "Full Name", "Federation", "Rating",
            "Score", "Tiebreak"};
        MODEL_LEADERBOARD.setColumnIdentifiers(leaderboardFields);
        tblLeaderboard.setModel(MODEL_LEADERBOARD);

        //to remove all the netbeans given options(item 1, 2, 3, etc)
        cBoxRound.removeAllItems();

        // generate first round
        genRound();
    }

    private void genRound() {
        if (currRound <= maxRounds) {

            if (currRound == maxRounds) {

                HashMap<String, Object> attrs = new HashMap<>();
                attrs.put("has_ended", true);
                hasEnded = true;

                t = TE.update(t.getId(), attrs);
            }

            List<Game> games = mm.generateRound(t);
            gamesForEachRound.put(currRound, games);

            for (Game g : games) {
                if (!GE.insert(g)) {
                    System.out.println("failed to insert game in round: " + currRound);
                }
            }

            cBoxRound.addItem(currRound + "");
            showTableRound(currRound);
            setLeaderBoard(currRound);
            pBarRounds.setValue(currRound);
            updateRoundCompletion(currRound); // updates the percentage of the rounds completed

            currRound++; // increments round to prepare for then next one
            mm.setRound(currRound); // sets the next round of the matchmaker
        } else {
            String s = "Tournament has already reached the maximum amount of rounds. This cannot be changed.";
            System.out.println(s);
            JOptionPane.showMessageDialog(this, s);

        }
    }

    private void showTableRound(int round) {
        List<Game> gamesForRound = gamesForEachRound.get(round);

        // Null-safe check (null checked first)
        if (gamesForRound == null || gamesForRound.isEmpty()) {
            return;
        }

        // Reset table
        MODEL_ROUNDS.setRowCount(0);

        for (Game g : gamesForRound) {
            addGame(g);
        }
        cBoxRound.setSelectedIndex(round - 1);

    }

    private void updateRoundCompletion(int roundNum) {
        double percentage = (double) roundNum / (double) maxRounds * 100;
        lblRoundCompletion.setText(String.format("%.2f", percentage) + "%"); //format confused actual percentage symbol as a placeholder
    }

    private void addGame(Game g) {
        MODEL_ROUNDS.addRow(new Object[]{
            g.getRound(),
            getPlayerName(g.getWhite().getId()),
            getPlayerName(g.getBlack().getId()),
            g.getResult().getScore() + "(" + g.getResult().name() + ")",
            g.getOpening()
        });
    }

    private void setRegisteredPlayers() {
        List<Player> players = new ArrayList<>();

        List<HashMap<String, Object>> ps = PE.selectWhere("tournament_id", "=", t.getId());

        if (!ps.isEmpty()) {
            for (HashMap<String, Object> playerRow : ps) {
                players.add(PE.mapRow(playerRow));
            }
            t.setPlayers(players);
            System.out.println("Players set.");
        } else {
            System.out.println("No registered players in this tournament.");
            JOptionPane.showMessageDialog(this, "This tournament does not have enough players.");
        }

    }

    private String getPlayerName(int id) {
        for (Player p : t.getPlayers()) {
            if (p.getId() == id) {
                return p.getFullName();
            }
        }

        return "Unavailable";
    }

    private void initMatchMaker() {
        mm = new MatchMaker(t.getPlayers(), currRound);
    }

    private void setLeaderBoard(int round) {
        MODEL_LEADERBOARD.setRowCount(0);

        int rank = 1;
        for (Player p : mm.getLeaderboardForRound(round)) {
            addToLeaderboard(p, rank);
            rank++;
        }
    }

    private void addToLeaderboard(Player p, int rank) {
        MODEL_LEADERBOARD.addRow(new Object[]{
            rank,
            p.getFullName(),
            p.getFederation(),
            p.getRating(),
            p.getScore(),
            p.getTieBreak()
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lpnlTR = new javax.swing.JLayeredPane();
        btnHomeTab = new javax.swing.JButton();
        btnTournamentTab = new javax.swing.JButton();
        lblHeading = new javax.swing.JLabel();
        cBoxRound = new javax.swing.JComboBox<>();
        lblRoundHeading = new javax.swing.JLabel();
        pBarRounds = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRounds = new javax.swing.JTable();
        btnGenRound = new javax.swing.JButton();
        btnSeeFinal = new javax.swing.JButton();
        lblRoundCompletion = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblLeaderboard = new javax.swing.JTable();
        lblRoundCompletion1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnHomeTab.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 24)); // NOI18N
        btnHomeTab.setText("Home");
        btnHomeTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeTabActionPerformed(evt);
            }
        });
        lpnlTR.add(btnHomeTab);
        btnHomeTab.setBounds(31, 29, 230, 40);

        btnTournamentTab.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 24)); // NOI18N
        btnTournamentTab.setText("Tournaments");
        btnTournamentTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTournamentTabActionPerformed(evt);
            }
        });
        lpnlTR.setLayer(btnTournamentTab, javax.swing.JLayeredPane.PALETTE_LAYER);
        lpnlTR.add(btnTournamentTab);
        btnTournamentTab.setBounds(1080, 30, 230, 40);

        lblHeading.setFont(new java.awt.Font("UD Digi Kyokasho NP", 1, 18)); // NOI18N
        lblHeading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lpnlTR.add(lblHeading);
        lblHeading.setBounds(470, 30, 429, 40);

        cBoxRound.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));
        cBoxRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cBoxRoundActionPerformed(evt);
            }
        });
        lpnlTR.add(cBoxRound);
        cBoxRound.setBounds(490, 110, 360, 22);

        lblRoundHeading.setFont(new java.awt.Font("UD Digi Kyokasho NP", 1, 14)); // NOI18N
        lblRoundHeading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoundHeading.setText("Select Round.");
        lpnlTR.add(lblRoundHeading);
        lblRoundHeading.setBounds(550, 80, 239, 30);
        lpnlTR.add(pBarRounds);
        pBarRounds.setBounds(31, 159, 510, 26);

        tblRounds.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblRounds);

        lpnlTR.add(jScrollPane1);
        jScrollPane1.setBounds(31, 191, 610, 402);

        btnGenRound.setText("Generate Next Round");
        btnGenRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenRoundActionPerformed(evt);
            }
        });
        lpnlTR.add(btnGenRound);
        btnGenRound.setBounds(1080, 110, 210, 31);

        btnSeeFinal.setText("See Final Stats");
        btnSeeFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeeFinalActionPerformed(evt);
            }
        });
        lpnlTR.add(btnSeeFinal);
        btnSeeFinal.setBounds(1080, 150, 210, 30);

        lblRoundCompletion.setFont(new java.awt.Font("UD Digi Kyokasho NP", 1, 18)); // NOI18N
        lblRoundCompletion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoundCompletion.setText("0%");
        lpnlTR.add(lblRoundCompletion);
        lblRoundCompletion.setBounds(540, 160, 101, 26);

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
        jScrollPane2.setViewportView(tblLeaderboard);

        lpnlTR.add(jScrollPane2);
        jScrollPane2.setBounds(680, 190, 622, 402);

        lblRoundCompletion1.setFont(new java.awt.Font("UD Digi Kyokasho NP", 1, 18)); // NOI18N
        lblRoundCompletion1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblRoundCompletion1.setText("Round Leaderboard");
        lpnlTR.add(lblRoundCompletion1);
        lblRoundCompletion1.setBounds(680, 160, 200, 26);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lpnlTR)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lpnlTR)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSeeFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeeFinalActionPerformed
        if (!hasEnded) {
            JOptionPane.showMessageDialog(null, "Tournament has not ended. Please Simulate all the rounds.");
            return;
        }

        t.setPlayers(mm.getLeaderboardForRound(currRound)); // already sorted leaderboard

        this.dispose();
        new ViewTournament(t).setVisible(true);
    }//GEN-LAST:event_btnSeeFinalActionPerformed

    private void btnGenRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenRoundActionPerformed
        genRound();
    }//GEN-LAST:event_btnGenRoundActionPerformed

    private void cBoxRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cBoxRoundActionPerformed
        showTableRound(Integer.parseInt(cBoxRound.getSelectedItem().toString()));
        setLeaderBoard(Integer.parseInt(cBoxRound.getSelectedItem().toString()));
    }//GEN-LAST:event_cBoxRoundActionPerformed

    private void btnTournamentTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTournamentTabActionPerformed
        this.dispose();
        new ui.TournamentsPage().setVisible(true);
    }//GEN-LAST:event_btnTournamentTabActionPerformed

    private void btnHomeTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeTabActionPerformed
        this.dispose();
        new ui.Main().setVisible(true);
    }//GEN-LAST:event_btnHomeTabActionPerformed

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
            java.util.logging.Logger.getLogger(TournamentRounds.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TournamentRounds.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TournamentRounds.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TournamentRounds.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TournamentRounds(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenRound;
    private javax.swing.JButton btnHomeTab;
    private javax.swing.JButton btnSeeFinal;
    private javax.swing.JButton btnTournamentTab;
    private javax.swing.JComboBox<String> cBoxRound;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHeading;
    private javax.swing.JLabel lblRoundCompletion;
    private javax.swing.JLabel lblRoundCompletion1;
    private javax.swing.JLabel lblRoundHeading;
    private javax.swing.JLayeredPane lpnlTR;
    private javax.swing.JProgressBar pBarRounds;
    private javax.swing.JTable tblLeaderboard;
    private javax.swing.JTable tblRounds;
    // End of variables declaration//GEN-END:variables
}
