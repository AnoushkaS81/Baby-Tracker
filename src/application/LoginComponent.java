// Contains the main content of the application.

// Specify the package we are part of.
package application;

// Import the "Functionality" parts of the application.
import application.Functionality.*;

// Import GUI modules.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

// Import module for file management.
import java.io.File;

// LoginComponent class to create the login UI panel.
public class LoginComponent extends JPanel implements ActionListener {
    // Accounts Database object, which will manage the accounts file.
    private AccountDatabase accounts;

    // Components
    private JTextField usernameField;
    private JTextField fullNameField;
    private JPasswordField passwordField;
    private JPasswordField passwordRepeatField;
    private JButton loginButton;
    private JButton switchToRegisterButton;

    // Field names that are determined by login or registration.
    private String titleText;
    private String submitText;

    // Colors for the theme
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Light blue background
    private final Color ACCENT_COLOR = new Color(70, 130, 180);      // Steel blue accent
    private final Color BUTTON_COLOR = new Color(70, 130, 180);      // Steel blue for buttons
    private final Color TEXT_COLOR = new Color(47, 79, 79);          // Dark slate gray for text

    // If we are registering a new account as opposed to logging in.
    private boolean register;

    public LoginComponent() {
        // Create the app's data directory if it does not exist.
        new File(AppStorage.getDirectory()).mkdirs();

        // Set up the database of accounts.
        this.accounts = new AccountDatabase();

        // Set background color and layout
        this.setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // Build the UI components
        this.rebuildUI();
    }

    // Build the UI components.
    private void rebuildUI() {
        // First, remove all existing components.
        removeAll();

        // Create a main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Create a card panel with rounded corners and shadow effect
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new SoftBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)));
        cardPanel.setAlignmentX(CENTER_ALIGNMENT);

        // Set text for title label based on whether we are login or register.
        if (this.register) {
            titleText = "Create Your Account";
        } else {
            titleText = "Welcome Back";
        }

        // Add a logo/image at the top
        try {
            ImageIcon originalIcon = new ImageIcon("Images/Profile_Picture.jpg");
            Image image = originalIcon.getImage();
            Image resizedImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            JLabel logoLabel = new JLabel(resizedIcon);
            logoLabel.setAlignmentX(CENTER_ALIGNMENT);
            cardPanel.add(logoLabel);
            cardPanel.add(Box.createVerticalStrut(20));
        } catch (Exception e) {
            System.out.println("Image not found, using text instead");
        }

        // Title label with larger, styled font
        JLabel title = new JLabel(titleText);
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        cardPanel.add(title);

        // Subtitle
        JLabel subtitle = new JLabel(this.register ? "Please fill in your details" : "Please enter your credentials");
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(128, 128, 128));
        cardPanel.add(subtitle);
        cardPanel.add(Box.createVerticalStrut(30));

        // Username field
        usernameField = createStyledTextField(15);
        cardPanel.add(createFieldPanel("Username", usernameField));
        cardPanel.add(Box.createVerticalStrut(15));

        // Full name field, if we are registering
        if (this.register) {
            fullNameField = createStyledTextField(15);
            cardPanel.add(createFieldPanel("Full Name", fullNameField));
            cardPanel.add(Box.createVerticalStrut(15));
        }

        // Password field
        passwordField = createStyledPasswordField(15);
        cardPanel.add(createFieldPanel("Password", passwordField));

        // Second password field, if we are registering
        if (this.register) {
            cardPanel.add(Box.createVerticalStrut(15));
            passwordRepeatField = createStyledPasswordField(15);
            cardPanel.add(createFieldPanel("Confirm Password", passwordRepeatField));
        }

        cardPanel.add(Box.createVerticalStrut(30));

        // Login/Register button with improved styling
        if (this.register) {
            submitText = "Create Account";
        } else {
            submitText = "Sign In";
        }

        loginButton = createStyledButton(submitText);
        loginButton.addActionListener(this);
        cardPanel.add(loginButton);

        cardPanel.add(Box.createVerticalStrut(15));

        // Switch button with lighter styling
        if (this.register) {
            JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            loginPanel.setBackground(Color.WHITE);
            loginPanel.add(new JLabel("Already have an account?"));
            switchToRegisterButton = createLinkButton("Sign In");
            loginPanel.add(switchToRegisterButton);
            cardPanel.add(loginPanel);
        } else {
            JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            registerPanel.setBackground(Color.WHITE);
            registerPanel.add(new JLabel("Don't have an account?"));
            switchToRegisterButton = createLinkButton("Create Account");
            registerPanel.add(switchToRegisterButton);
            cardPanel.add(registerPanel);
        }

        // Add the card to the main panel
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(cardPanel);
        mainPanel.add(Box.createVerticalGlue());

        // Add main panel to the component
        add(mainPanel, BorderLayout.CENTER);

        // Revalidate and repaint
        revalidate();
        repaint();
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setMaximumSize(new Dimension(300, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT_COLOR),
                BorderFactory.createEmptyBorder(5, 2, 5, 2)));
        return field;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setMaximumSize(new Dimension(300, 35));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT_COLOR),
                BorderFactory.createEmptyBorder(5, 2, 5, 2)));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 40));
        button.setPreferredSize(new Dimension(300, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(BUTTON_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(BUTTON_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(BUTTON_COLOR);
            }
        });

        return button;
    }

    private JPanel createFieldPanel(String labelText, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);

        return panel;
    }

    // Action performed when login button is pressed
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            // Extract fields from text input, only if the fields exist.
            String username = "";
            if (usernameField != null) {
                username = usernameField.getText();
            }
            String fullname = "";
            if (fullNameField != null) {
                fullname = fullNameField.getText();
            }
            String password = "";
            if (passwordField != null) {
                password = passwordField.getText();
            }
            String passwordrepeat = "";
            if (passwordRepeatField != null) {
                passwordrepeat = passwordRepeatField.getText();
            }
            // Obtain account object, if any.
            Account account = this.accounts.getAccount(username);
            // Moved to a separate class (static method) for apparent clarity.
            int logincode = LoginAction.doLogin(this.register, username, fullname, password, passwordrepeat, account, this.accounts.verifyAccount(username, password));
            // Get the return code.
            if (logincode == 0) {
                // LOGIN is successful, display an appropriate message.
                JOptionPane.showMessageDialog(this, "You have successfully logged in.", "Information", JOptionPane.INFORMATION_MESSAGE);
                // Switch to main application dashboard screen.
                TLauncher.switchTo(new UserProfile(username), this);
                // Close this window.
                SwingUtilities.getWindowAncestor(this).dispose();
            } else if (logincode == 1) {
                // REGISTRATION is successful, create new account.
                this.accounts.createAccount(username, fullname, password);
                // Display appropriate message.
                JOptionPane.showMessageDialog(this, "Your account was successfully registered. You may now login.", "Information", JOptionPane.INFORMATION_MESSAGE);
                // Switch back to the login screen.
                this.switchLoginScreen();
            } else if (logincode == 2) {
                // Some registration fields are empty.
                showErrorMessage("You must fill in every field to register an account.");
            } else if (logincode == 3) {
                // Registration passwords do not match.
                showErrorMessage("The passwords do not match.");
            } else if (logincode == 4) {
                // Account already exists and cannot be registered again.
                showErrorMessage("An account with the same username already exists.");
            } else if (logincode == 5) {
                // Some login fields are empty.
                showErrorMessage("You must fill in every field to log in to your account.");
            } else if (logincode == 6) {
                // Login credentials are incorrect.
                showErrorMessage("Your username or password is incorrect.");
            }
        } else if (e.getSource() == switchToRegisterButton) {
            this.switchLoginScreen();
        }
    }

    private void showErrorMessage(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Error", true);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel errorIcon = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        errorIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(errorIcon);
        panel.add(Box.createVerticalStrut(15));

        JLabel errorMessage = new JLabel(message);
        errorMessage.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        errorMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(errorMessage);
        panel.add(Box.createVerticalStrut(20));

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.addActionListener(e -> dialog.dispose());
        panel.add(okButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void switchLoginScreen() {
        // Flip the boolean to toggle the screen.
        this.register = !this.register;
        // Rebuild/redraw all the UI components.
        rebuildUI();
    }
}