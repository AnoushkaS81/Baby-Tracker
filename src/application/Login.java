// Sets up the application window.
// Main application code will come from "ApplicationComponent" class.

// Specify the package we are part of.
package application;

// Import GUI modules.
import javax.swing.*;
import java.awt.*;

// Set up the class.
public class Login {
    public Login() {
        try {
            // Set the look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main application window with a more appealing title
        JFrame window = new JFrame("Baby Tracker");

        // Set icon if available
        try {
            window.setIconImage(new ImageIcon("Images/baby.png").getImage());
        } catch (Exception e) {
            // If icon not found, continue without it
        }

        // Set window size to be more reasonable than maximized
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setSize(screenSize); // This will set the JFrame size to the screen size
        window.setLocationRelativeTo(null);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create a gradient background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Create a gradient from light blue to white
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(230, 240, 255),
                        0, getHeight(), new Color(255, 255, 255)
                );

                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Add the login component to the background panel
        LoginComponent component = new LoginComponent();
        backgroundPanel.add(component, BorderLayout.CENTER);

        // Add the background panel to the window
        window.add(backgroundPanel);

        // Set default close operation and make the window visible
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }
}