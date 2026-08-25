/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jframeconfig;

import javax.swing.ImageIcon;

/**
 * GUI CONFIGURATIONS
 * Configures the GUI frames so i don't have to put in the same exact lines on 
 * every single frame.
 * 
 * Made so my GUI classes can look cleaner
 * @author tshim
 */
public class Config {

    // frame settings
    public static final String PROJECT_NAME = "Tournament Control";
    public static final int FRAME_WIDTH = 1338, FRAME_HEIGHT = 707,
                             X_COORD = 0, Y_COORD = 0;

    // Tournament Control's Logo
    public static final ImageIcon ICON = new ImageIcon(Config.class.getResource("/images/TC_Logo.png"));

    //get a specific image
    public static ImageIcon getImage(String imageName) {
        return new ImageIcon(Config.class.getResource("/images/" + imageName));
    }

    //get the projects icon
    public static ImageIcon getIcon() {
        return ICON;
    }

    public static java.awt.Font getFont() {
        // a font a liked
        // snipped from the Netbeans generated code, after i found it in
        // the font drop down.
        return new java.awt.Font("UD Digi Kyokasho NK", 1, 24);
    }

    /**
     * Sets the frame attributes
     * Made to reduce repeated lines across GUIs
     * @param frame
     * @param title 
     */
    public static void setAttributes(javax.swing.JFrame frame, String title) {
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setIconImage(ICON.getImage());
        frame.setTitle(title);
        frame.setLocationRelativeTo(frame);
    }

    public static void setAttributes(javax.swing.JFrame frame) {
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setIconImage(ICON.getImage());
        frame.setTitle(PROJECT_NAME);
        frame.setLocationRelativeTo(frame);
    }
}
