package application;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import javax.swing.border.AbstractBorder;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Records extends JFrame {
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 150;
    private static final int GAP = 20;
    
    
    private String childName;
    private String username;
    
    public Records(String childName,String username) {
        this.childName = childName;
        this.username = username;
        // Create a header panel for the child's name
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Create and style the name label
        JLabel nameLabel = new JLabel(childName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(nameLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Set up the main frame
        setTitle("Records for " + childName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Make frame full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true); // Optional - removes window decorations
        
        // Main panel with grid layout
        JPanel mainPanel = new JPanel(new GridLayout(2, 3, GAP, GAP));
        mainPanel.setBorder(new EmptyBorder(GAP, GAP, GAP, GAP));
        
        // Create record type buttons
        addRecordButton(mainPanel, "Feeding", "src/icons/feeding.png");
        addRecordButton(mainPanel, "Medication", "src/icons/medicine.png");
        addRecordButton(mainPanel, "Diaper Change", "src/icons/diaper.png");
        addRecordButton(mainPanel, "Sleep", "src/icons/sleep.png");
        addRecordButton(mainPanel, "Growth", "src/icons/healthy.png");
        
        // Add a back button
        JButton backButton = createStyledButton("Back", "src/icons/back.png");
        backButton.addActionListener(e -> TLauncher.switchTo(new UserProfile(username), backButton));
//        return backButton ;

        mainPanel.add(backButton);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void addRecordButton(JPanel panel, String text, String iconPath) {
        JButton button = createStyledButton(text, iconPath);
        button.addActionListener(e -> handleRecordButtonClick(text));
        panel.add(button);
    }
    
    private JButton createStyledButton(String text, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath);
        // Scale the icon
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImg);
        
        JButton button = new JButton(text, icon);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        
        // Add rounded borders
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15), // 15 pixels radius for rounded corners
            BorderFactory.createEmptyBorder(10, 10, 10, 10) // Internal padding
        ));
        
        return button;
    }
    
    private void handleRecordButtonClick(String recordType) {
        // Placeholder for record type specific actions
        switch (recordType) {
            case "Feeding":
                // TODO: Open feeding records view
                JOptionPane.showMessageDialog(this, "Opening Feeding Records");
                break;
            case "Medication":
                // TODO: Open medication records view
                JOptionPane.showMessageDialog(this, "Opening Medication Records");
                break;
            case "Diaper Change":
                // TODO: Open diaper change records view
                JOptionPane.showMessageDialog(this, "Opening Diaper Change Records");
                break;
            case "Sleep":
                // TODO: Open sleep records view
                JOptionPane.showMessageDialog(this, "Opening Sleep Records");
                break;
            case "Growth":
                // TODO: Open growth records view
                JOptionPane.showMessageDialog(this, "Opening Growth Records");
                break;
        }
    }
    
    private static class RoundedBorder extends AbstractBorder {
        private int radius;
        
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            
            // Draw small circles at each corner
            int circleSize = radius;
            g2.drawLine(x + circleSize, y, x + width - circleSize, y); // top
            g2.drawLine(x + circleSize, y + height - 1, x + width - circleSize, y + height - 1); // bottom
            g2.drawLine(x, y + circleSize, x, y + height - circleSize); // left
            g2.drawLine(x + width - 1, y + circleSize, x + width - 1, y + height - circleSize); // right
            
            // Draw the circles at corners
            g2.drawOval(x, y, circleSize, circleSize); // top-left
            g2.drawOval(x + width - circleSize - 1, y, circleSize, circleSize); // top-right
            g2.drawOval(x, y + height - circleSize - 1, circleSize, circleSize); // bottom-left
            g2.drawOval(x + width - circleSize - 1, y + height - circleSize - 1, circleSize, circleSize); // bottom-right
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius/2, this.radius/2, this.radius/2, this.radius/2);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
}
