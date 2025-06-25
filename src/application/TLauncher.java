package application;
import javax.swing.*;
import java.awt.*;

public class TLauncher {
    // Ensure objects of this class cannot be created (static methods only).
    private TLauncher() {}

    // Method to switch to a new window.
    public static void switchTo(JFrame newFrame, Component currentComponent) {
        // Dispose of the current window
        SwingUtilities.getWindowAncestor(currentComponent).dispose();

        ImageIcon icon = new ImageIcon("Images/baby.png");
        newFrame.setIconImage(icon.getImage());

        // Initialize the new frame
        newFrame.setTitle("Baby Tracker");
        newFrame.setVisible(true);
        newFrame.setLocationRelativeTo(null);

        newFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
