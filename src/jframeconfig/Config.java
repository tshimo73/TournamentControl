/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jframeconfig;

import javax.swing.ImageIcon;

public class Config {

    // frame settings
    public static final String PROJECT_NAME = "Tournament Control";
    public static final int FRAME_WIDTH = 1338;
    public static final int FRAME_HEIGHT = 707;
    public static final int X_COORD = 0;
    public static final int Y_COORD = 0;

    
    
    // window icons
    private final ImageIcon ICON = new ImageIcon(getClass().getResource("/images/TC_Logo.png"));

    //get a specific image
    public static ImageIcon getImage(String imageName) {
        return new ImageIcon(Config.class.getResource("/images/" + imageName));
    }

    //get the projects icon
    public ImageIcon getIcon() {
        return ICON;
    }

    public java.awt.Font getFont() {
        return new java.awt.Font("UD Digi Kyokasho NK", 1, 24);
    }

    public void setAttributes(javax.swing.JFrame frame, String title) {
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setIconImage(getIcon().getImage());
        frame.setTitle(title);
        frame.setLocationRelativeTo(frame);
    }

    public void setAttributes(javax.swing.JFrame frame) {
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setIconImage(getIcon().getImage());
        frame.setTitle(PROJECT_NAME);
        frame.setLocationRelativeTo(frame);
    }
}
