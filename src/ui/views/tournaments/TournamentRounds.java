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
import database.PlayerFields;
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
    private final DefaultTableModel MODEL = new DefaultTableModel();
    private MatchMaker mm;
    private int currRound = 1, maxRounds;
    private List<List<Game>> gamesForEachRound = new ArrayList<>(new ArrayList<>());

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

        // initialise table
        String[] roundFields = {"Round Number", "White Player", "Black Player", "Result", "Opening"};
        MODEL.setColumnIdentifiers(roundFields);
        tblRounds.setModel(MODEL);
        
        //to remove all the netbeans given options(item 1, 2, 3, etc)
        cBoxRound.removeAllItems();

        // generate first round
        genRound();
    }

    private void genRound() {
        if (currRound <= maxRounds) {
            gamesForEachRound.add(mm.generateRound(t));
            
            addRoundToCBox(currRound);
            showTableRound(currRound);
            
            currRound++;
            mm.setRound(currRound);
            pBarRounds.setValue(currRound - 1); // because the round was increments
            t.setPlayers(mm.getLeaderBoard()); // leaderboard (order) updated after every round
        } else {
            String s = "Tournament has already reached the maximum amount of rounds. This cannot be changed.";
            System.out.println(s);
            JOptionPane.showMessageDialog(this, s);

            HashMap<String, Object> attrs = new HashMap<>();
            attrs.put("has_ended", true);

            t = TE.update(t.getId(), attrs);
        }
    }

    private void showTableRound(int round) {
        int index = round - 1;

        if (gamesForEachRound == null || index < 0 || index >= gamesForEachRound.size()) {
            return;
        }

        List<Game> gamesForRound = gamesForEachRound.get(index);

        // Null-safe check (null checked first)
        if (gamesForRound == null || gamesForRound.isEmpty()) {
            return;
        }

        // Reset table
        MODEL.setRowCount(0);

        for (Game g : gamesForRound) {
            addGame(g);
        }
        cBoxRound.setSelectedIndex(index);
    }

    private void addGame(Game g) {
        MODEL.addRow(new Object[]{
            g.getRound(),
            getPlayerName(g.getWhite().getId()),
            getPlayerName(g.getBlack().getId()),
            g.getResult().getScore() + "(" + g.getResult().name() + ")",
            g.getOpening()
        });
    }

    private void addRoundToCBox(int i) {
        cBoxRound.addItem(i + "");
    }

    private void setRegisteredPlayers() {
        List<Player> players = new ArrayList<>();

        List<HashMap<String, Object>> ps = PE.selectWhere(PlayerFields.TOURNAMENT_ID, "=", t.getId());

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
        for (Player p : mm.getLeaderBoard()) {
            if (p.getId() == id) {
                return p.getFullName();
            }
        }

        return "Unavailable";
    }

    private void initMatchMaker() {
        mm = new MatchMaker(t.getPlayers(), currRound);
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
        lblHeading1 = new javax.swing.JLabel();
        pBarRounds = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRounds = new javax.swing.JTable();
        btnGenRound = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnHomeTab.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 24)); // NOI18N
        btnHomeTab.setText("Home");
        btnHomeTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeTabActionPerformed(evt);
            }
        });

        btnTournamentTab.setFont(new java.awt.Font("UD Digi Kyokasho NK", 1, 24)); // NOI18N
        btnTournamentTab.setText("Tournaments");
        btnTournamentTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTournamentTabActionPerformed(evt);
            }
        });

        lblHeading.setFont(new java.awt.Font("UD Digi Kyokasho NP", 1, 18)); // NOI18N
        lblHeading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        cBoxRound.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {}));
        cBoxRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cBoxRoundActionPerformed(evt);
            }
        });

        lblHeading1.setFont(new java.awt.Font("UD Digi Kyokasho NP", 1, 14)); // NOI18N
        lblHeading1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeading1.setText("Select Round.");

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

        btnGenRound.setText("Generate Next Round");
        btnGenRound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenRoundActionPerformed(evt);
            }
        });

        lpnlTR.setLayer(btnHomeTab, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpnlTR.setLayer(btnTournamentTab, javax.swing.JLayeredPane.PALETTE_LAYER);
        lpnlTR.setLayer(lblHeading, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpnlTR.setLayer(cBoxRound, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpnlTR.setLayer(lblHeading1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpnlTR.setLayer(pBarRounds, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpnlTR.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpnlTR.setLayer(btnGenRound, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout lpnlTRLayout = new javax.swing.GroupLayout(lpnlTR);
        lpnlTR.setLayout(lpnlTRLayout);
        lpnlTRLayout.setHorizontalGroup(
            lpnlTRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpnlTRLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(btnHomeTab, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(252, 252, 252)
                .addComponent(lblHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTournamentTab, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
            .addGroup(lpnlTRLayout.createSequentialGroup()
                .addGroup(lpnlTRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lpnlTRLayout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(lpnlTRLayout.createSequentialGroup()
                        .addGroup(lpnlTRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(lpnlTRLayout.createSequentialGroup()
                                .addGap(462, 462, 462)
                                .addComponent(pBarRounds, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(lpnlTRLayout.createSequentialGroup()
                                .addGap(402, 402, 402)
                                .addComponent(cBoxRound, javax.swing.GroupLayout.PREFERRED_SIZE, 567, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(lpnlTRLayout.createSequentialGroup()
                                .addGap(555, 555, 555)
                                .addComponent(lblHeading1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(72, 72, 72)
                        .addComponent(btnGenRound, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(213, Short.MAX_VALUE))
        );
        lpnlTRLayout.setVerticalGroup(
            lpnlTRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpnlTRLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(lpnlTRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHomeTab, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTournamentTab, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblHeading1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(lpnlTRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lpnlTRLayout.createSequentialGroup()
                        .addComponent(cBoxRound, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pBarRounds, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnGenRound, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(79, Short.MAX_VALUE))
        );

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

    private void btnHomeTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeTabActionPerformed
        this.dispose();
        new ui.Main().setVisible(true);
    }//GEN-LAST:event_btnHomeTabActionPerformed

    private void btnTournamentTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTournamentTabActionPerformed
        this.dispose();
        new ui.tabbedPanels.TournamentsPage().setVisible(true);
    }//GEN-LAST:event_btnTournamentTabActionPerformed

    private void cBoxRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cBoxRoundActionPerformed
        showTableRound(Integer.parseInt(cBoxRound.getSelectedItem().toString()));
    }//GEN-LAST:event_cBoxRoundActionPerformed

    private void btnGenRoundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenRoundActionPerformed
        genRound();
    }//GEN-LAST:event_btnGenRoundActionPerformed

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
    private javax.swing.JButton btnTournamentTab;
    private javax.swing.JComboBox<String> cBoxRound;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHeading;
    private javax.swing.JLabel lblHeading1;
    private javax.swing.JLayeredPane lpnlTR;
    private javax.swing.JProgressBar pBarRounds;
    private javax.swing.JTable tblRounds;
    // End of variables declaration//GEN-END:variables
}
