package application;

import application.Functionality.ChildDatabase;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserProfile extends JFrame {
    private String username;
    private ChildDatabase children;
    private JPanel childrenPanel; // Panel holding the grid of children buttons
    private JButton addChildButton; // The "Add Child" button

    // Added CardLayout and cardPanel for switching between views
    private CardLayout cardLayout;
    private JPanel cardPanel;


    // Constants for grid cell size and grid configuration
    private static final int CELL_WIDTH = 150;
    private static final int CELL_HEIGHT = 150;
    private static final int COLUMNS = 6;
    private static final int GAP = 10;  // gap between cells

    public UserProfile(String username) {
        this.username = username;
        this.children = new ChildDatabase(username);

        // Set up the main frame
        setTitle("User Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout(10, 10)); // main layout with gaps

        // Initialize CardLayout and JPanel for switching views
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // Use the cardPanel to manage view switching

        // Create the UserProfile panel and ChildProfile panel
        JPanel userProfilePanel = createUserProfilePanel();
        cardPanel.add(userProfilePanel, "UserProfile");

        // Initially, the cardPanel contains only the UserProfile view
        add(cardPanel, BorderLayout.CENTER);

        // Set up other necessary UI components (e.g., event listeners, actions)
        // For example, ensure you update the children grid in case there are existing records in the database.
        updateChildrenGrid();

        // Make the frame visible now all setup is done
        setVisible(true);
    }

    private JPanel createUserProfilePanel() {
        JPanel userProfilePanel = new JPanel();
        userProfilePanel.setLayout(new BorderLayout(10, 10));

        // ===== Top Panel =====
        // Contains username info (top left) and Settings/Logout buttons (top right)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left side: Username panel
        JLabel usernameLabel = new JLabel("Username: " + username);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(usernameLabel, BorderLayout.WEST);

        // Right side: Buttons panel
        JPanel topButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        // Create settings button with icon
        ImageIcon settingsIcon = new ImageIcon("Images/setting.png");
        // Scale the icon to a reasonable size (e.g., 40x40 pixels)
        Image scaledImage = settingsIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);

        // Create a new ImageIcon with the scaled image

        JButton settingsButton = new JButton( new ImageIcon(scaledImage));
        settingsButton.setBorder(null);
        settingsButton.setFocusPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setPreferredSize(new Dimension(60,60));
        
        JButton logoutButton = new JButton("Logout");

        // Add basic actions for Settings and Logout
        settingsButton.addActionListener(e -> {
            // Ensure all data in the ChildDatabase is synced to the disk
            this.children.sync();
            // Switch to the settings screen (TLauncher discards this automatically)
            TLauncher.switchTo(new Settings(this.username), settingsButton);
        });
        logoutButton.addActionListener(e -> {
            // Ensure all data is synced due to redundancy
            this.children.sync();
            // Switch back to the login screen and discard this one
            new Login();
            this.dispose();
        });
        topButtonsPanel.add(settingsButton);
        topButtonsPanel.add(logoutButton);
        topPanel.add(topButtonsPanel, BorderLayout.EAST);

        userProfilePanel.add(topPanel, BorderLayout.NORTH);

        // ===== Center Panel =====
        // Contains the title "Your children" and the grid of children
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));



        // Title label in the center (horizontally centered)
        JLabel titleLabel = new JLabel("Your children");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        // Children grid panel: GridLayout with 6 columns, variable rows, with gaps
        childrenPanel = new JPanel(new GridLayout(0, COLUMNS, GAP, GAP));
        childrenPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create the "Add Child" button and set its preferred size
//        addChildButton = new JButton("Add Child");

        // Create add child button with icon
        ImageIcon addChildIcon = new ImageIcon("Images/add.png");
        // Scale the icon to a reasonable size
        Image addChildScaledImage = addChildIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        
        // Create a new ImageIcon with the scaled image
        addChildButton = new JButton("ADD CHILD",new ImageIcon(addChildScaledImage));

        addChildButton.setBorder(null);
        addChildButton.setFocusPainted(false);
        addChildButton.setContentAreaFilled(false);
        addChildButton.setBorderPainted(false);
        addChildButton.setFont(new Font("Arial", Font.PLAIN, 16));
        addChildButton.setPreferredSize(new Dimension(CELL_WIDTH, CELL_HEIGHT));
        addChildButton.addActionListener(e -> promptAndAddChild());

        // Add the initial "Add Child" button to the grid
        childrenPanel.add(addChildButton);

        // Wrap the childrenPanel in a scroll pane in case there are many rows
        JScrollPane gridScrollPane = new JScrollPane(childrenPanel);
        // Set a preferred size for the scroll pane; its height will be based on our grid's preferred size
        gridScrollPane.setPreferredSize(new Dimension((CELL_WIDTH + GAP) * COLUMNS, CELL_HEIGHT + GAP * 2));
        centerPanel.add(gridScrollPane);

        userProfilePanel.add(centerPanel, BorderLayout.CENTER);

        // ===== Bottom Panel =====
        // Add a bottom panel with the HelperBot button on the right
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel rightAlignedPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        
        // Create HelperBot button with icon
        ImageIcon helperBotIcon = new ImageIcon("Images/assistant.png");
        // Scale the icon to a reasonable size
        Image helperBotScaledImage = helperBotIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        
        JButton helperBotButton = new JButton( new ImageIcon(helperBotScaledImage));
        helperBotButton.setBorder(null);
        helperBotButton.setFocusPainted(false);
        helperBotButton.setContentAreaFilled(false);
        helperBotButton.setPreferredSize(new Dimension(80,80));

// Open the Helper Bot
        helperBotButton.addActionListener(e -> {
            TLauncher.switchTo(new Helper_Bot(this.username), helperBotButton);
        });
        
        rightAlignedPanel.add(helperBotButton);
        bottomPanel.add(rightAlignedPanel, BorderLayout.EAST);
        userProfilePanel.add(bottomPanel, BorderLayout.SOUTH);

        // Update child grid immediately, in case the database loaded existing entries from the disk.
        this.updateChildrenGrid();

        return userProfilePanel;
    }

    // Creates a prompt for the user to add a new child by typing the username.
    private void promptAndAddChild() {
        String childName = JOptionPane.showInputDialog(this, "Enter child's name:");
        // trim() is used to strip off any spaces at the start or end of the string.
        if (childName != null && !childName.trim().isEmpty()) {
            // Add the new child to the database (sync is done automatically).
            this.children.addChild(childName.trim());
            // Refresh the grid so the newly added child is listed.
            updateChildrenGrid();
        }
    }

    /**
     * Rebuilds the children grid. Ensures the "Add Child" button is always the last cell.
     */
    private void updateChildrenGrid() {
        // Remove all components from the grid
        childrenPanel.removeAll();

        // Add a button for each child with fixed preferred size
        for (int i = 0; i < this.children.getAllChildren().size(); i++) {
            String name = this.children.getAllChildren().get(i).getName();

            // Create child button with icon
        ImageIcon ChildIcon = new ImageIcon("Images/baby.png");
           // Scale the icon to a reasonable size
        Image ChildScaledImage = ChildIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);

            JButton childButton = new JButton(name,new ImageIcon(ChildScaledImage));
            childButton.setBorder(null);
            childButton.setContentAreaFilled(false);
            childButton.setFocusPainted(false);
            childButton.setFont(new Font("Arial", Font.PLAIN, 16));
            childButton.setPreferredSize(new Dimension(CELL_WIDTH, CELL_HEIGHT));
            // Placeholder - showing a simple popup with the child's name.
            // TODO: Make it switch to the screen/frame/panel/etc. for that specific child.
            // TODO: See if the child screen can be incorporated into this one, similar to old UserProfile.
            // TODO: This will make it easier for us to maintain the ChildDatabase object too.
            childButton.addActionListener(e -> openChildProfile(name));
            childrenPanel.add(childButton);
        }

        // Add the "Add Child" button as the last cell
        childrenPanel.add(addChildButton);

        // Calculate the number of rows required for the grid
        int totalCells = this.children.getAllChildren().size() + 1; // plus one for the addChildButton
        int rows = (int) Math.ceil(totalCells / (double) COLUMNS);
        // Set the preferred size of the childrenPanel accordingly
        int preferredHeight = rows * CELL_HEIGHT + (rows - 1) * GAP;
        int preferredWidth = COLUMNS * CELL_WIDTH + (COLUMNS - 1) * GAP;
        childrenPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        // Refresh the grid layout
        childrenPanel.revalidate();
        childrenPanel.repaint();
    }
    private void openChildProfile(String childName) {
        String panelName = "ChildProfile_" + childName;

        // Check if this child's profile is already added
        for (Component comp : cardPanel.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(panelName)) {
                cardLayout.show(cardPanel, panelName);
                return;
            }
        }

        // Create a new ChildProfile panel
        ChildProfile childProfilePanel = new ChildProfile(this.children, childName, cardPanel, cardLayout);
        childProfilePanel.setName(panelName); // Set panel name for future reference

        // Add it to the cardPanel and switch to it
        cardPanel.add(childProfilePanel, panelName);
        cardLayout.show(cardPanel, panelName);
    }

}