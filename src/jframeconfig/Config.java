package jframeconfig;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * GUI CONFIGURATIONS Configures the GUI frames so i don't have to put in the
 * same exact lines on every single frame.
 *
 * Made so my GUI classes can look cleaner
 *
 * @author tshim
 */
public class Config {

    /**
     * Done by Gemini, and a dependancy (FlatLaf)
     * to at least beautify the UI a little bit.
     */
    // Runs once when the first UI loads Config
    static {
        // 1. Install Base Dark Theme
        FlatDarkLaf.setup();

        // 2. Global Typography
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
        UIManager.put("defaultFont", baseFont);

        // 3. Accent & Focus Glow (Emerald / Chess Green)
        Color chessGreen = new Color(0x2E, 0xCC, 0x71);
        UIManager.put("Component.accentColor", chessGreen);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.innerFocusWidth", 0);

        // 4. Buttons (Pill/Rounded, Modern Spacing)
        UIManager.put("Button.arc", 12);
        UIManager.put("Button.font", boldFont);
        UIManager.put("Button.margin", new Insets(8, 18, 8, 18));

        // 5. Text Fields, Password Boxes, & Spinners
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("TextField.margin", new Insets(6, 10, 6, 10));

        // 6. Combo Boxes (Dropdowns)
        UIManager.put("ComboBox.arc", 10);
        UIManager.put("ComboBox.padding", new Insets(4, 8, 4, 8));

        // 7. Tables (Instant styling for all tournament & player tables)
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
        UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 1));
        UIManager.put("Table.alternateRowColor", new Color(0x2B, 0x2D, 0x3A));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TableHeader.separatorColor", new Color(0x3E, 0x44, 0x51));

        // 8. Scrollbars (Slim and modern)
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);

        // 9. Popups & Dialogs (JOptionPane styling)
        UIManager.put("OptionPane.messageFont", baseFont);
        UIManager.put("OptionPane.buttonFont", boldFont);

        // 10. Native Dark Window Title Bars (Windows 10/11)
        UIManager.put("TitlePane.useWindowDecorations", true);

        // --- RICH BACKGROUND PALETTE (Midnight Navy / Slate Chess Theme) ---
        Color appBackground = new Color(0x1B, 0x1E, 0x2B); // Deep navy canvas (replaces pure black)
        Color cardSurface = new Color(0x25, 0x29, 0x3A); // Elevated panels & containers
        Color inputSurface = new Color(0x1E, 0x21, 0x30); // Input background
        Color accentEmerald = new Color(0x2E, 0xCC, 0x71); // Chess green accent
        Color accentHover = new Color(0x38, 0xB0, 0x00); // Hover/Highlight green
        Color subtleBorder = new Color(0x36, 0x3B, 0x52); // Subtle separator lines

// Window & Panel Backgrounds everywhere
        UIManager.put("Panel.background", appBackground);
        UIManager.put("Viewport.background", appBackground);
        UIManager.put("ScrollPane.background", appBackground);

// Input & Dropdown Field Backgrounds
        UIManager.put("TextField.background", inputSurface);
        UIManager.put("ComboBox.background", inputSurface);
        UIManager.put("FormattedTextField.background", inputSurface);
        UIManager.put("PasswordField.background", inputSurface);

// Accent & Border Highlights
        UIManager.put("Component.accentColor", accentEmerald);
        UIManager.put("Component.borderColor", subtleBorder);
        UIManager.put("Component.focusedBorderColor", accentEmerald);

// Table Styling (Zebra contrast)
        UIManager.put("Table.background", cardSurface);
        UIManager.put("Table.alternateRowColor", new Color(0x20, 0x24, 0x33));
        UIManager.put("Table.gridColor", new Color(0x2D, 0x32, 0x46));
        UIManager.put("TableHeader.background", inputSurface);
    }

    // everything else (below) was odone by me
    
    // frame settings
    private static final String PROJECT_NAME = "Tournament Control";
    private static final int FRAME_WIDTH = 1338, FRAME_HEIGHT = 707,
            X_COORD = 0, Y_COORD = 0;

    // Tournament Control's Logo
    private static final ImageIcon ICON = new ImageIcon(Config.class.getResource("/images/TC_Logo.png"));

    // Date Time Format
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
     * Sets the frame attributes Made to reduce repeated lines across GUIs
     *
     * @param frame
     * @param title
     */
    public static void setAttributes(javax.swing.JFrame frame, String title) {
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setIconImage(ICON.getImage());
        frame.setTitle(title);
        frame.setLocationRelativeTo(frame);

        frame.getContentPane().setBackground(new Color(0x1B, 0x1E, 0x2B)); // help from gemini
    }

    public static void setAttributes(javax.swing.JFrame frame) {
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setIconImage(ICON.getImage());
        frame.setTitle(PROJECT_NAME);
        frame.setLocationRelativeTo(frame);

        frame.getContentPane().setBackground(new Color(0x1B, 0x1E, 0x2B)); // help from gemini
    }
}
