package application;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import application.Functionality.*;

public class Settings extends JFrame {
    // Password is not included here, because we use astericks as a placeholder.
    private String username;
    private String name;
    private String email;
    private String phone;
    private JPanel mainPanel;
    // These are used to ensure everything is uniformly formatted and looks nice.
    private static final int LABEL_WIDTH = 134;
    private static final int FIELD_HEIGHT = 40;
    private static final int HORIZONTAL_GAP = 20;
    private static final Font HEADER_FONT = new Font("Segoe UI Semibold", Font.BOLD, 36);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    // The main constructor
    public Settings(String username) {
        this.username = username;
        // Collect the user information before initialising anything else in this screen.
        getUserInfo();
        // Set up the boilerplate of the frame.
        setupFrame();
        // Set up all the components on the screen.
        initializeComponents();
    }

    private void getUserInfo() {
        try {
            // Set up an account database (which will read data from the disk), and find the account.
            // This is needed because Settings is separate to UserProfile.
            AccountDatabase accountDB = new AccountDatabase();
            Account userAccount = accountDB.getAccount(username);

            // Ensure the account object actually exists, then get the information.
            if (userAccount != null) {
                this.username = userAccount.getUsername();
                this.name = userAccount.getFullname();
                this.email = userAccount.getEmail();
                this.phone = userAccount.getPhone();
            } else {
                // Display a message that the user was not found.
                // In reality, this should NEVER happen, because Settings is called by UserProfile.
                JOptionPane.showMessageDialog(this,
                        "User not found",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (RuntimeException e) {
            // Deal with the problem if AccountDatabase throws RuntimeException from a disk error.
            // Print the stack trace for debugging purposes, but know that it's likely unneeded.
            e.printStackTrace();
            // Display the dialog.
            JOptionPane.showMessageDialog(this,
                    "Error loading user data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Settings");

        // Set the size to 80% of screen size, for the most ideal fit.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // This would be floats by default, so cast to int, since you can't have half a pixel!
        setSize((int) (screenSize.width * 0.8), (int) (screenSize.height * 0.8));
        // Ensure it is centered on the screen.
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(new JScrollPane(mainPanel));
    }

    private void initializeComponents() {
        // Add the header.
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = createBackButton();
        headerPanel.add(backButton);
        mainPanel.add(headerPanel);

        // Add the title.
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Add the user information fields. Only username is not editable.
        addField("Username", username, false);
        addField("Name", name, true);
        addField("Email", email, true);
        addField("Phone", phone, true);
        // Password is NEVER displayed, so show a placeholder of astericks.
        // We use eight characters for this placeholder, REGARDLESS OF THE ACTUAL LENGTH!
        addField("Password", "********", true);

        mainPanel.add(Box.createVerticalStrut(20));

        // Add the Children Management section.
        JLabel childrenLabel = new JLabel("Children Management");
        childrenLabel.setFont(HEADER_FONT);
        childrenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(childrenLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Add the list of all children asscociated with this account.
        // We will initialise a ChildDatabase object, which will read the existing data from the disk.
        // This is because the Settings page can't share the same db object from UserProfile.
        ChildDatabase childDB = new ChildDatabase(username);
        for (Child child : childDB.getAllChildren()) {
            addChildField(child.getName());
        }

        mainPanel.add(Box.createVerticalStrut(40));

        // Add the delete account button.
        JButton deleteAccountButton = createDeleteAccountButton();
        JPanel deletePanel = new JPanel();
        deletePanel.add(deleteAccountButton);
        mainPanel.add(deletePanel);
    }

    private JButton createBackButton() {
        JButton backButton = new JButton();

        ImageIcon backIcon = new ImageIcon("Images/back.png");
        Image img = backIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        backButton.setIcon(new ImageIcon(img));

        backButton.setPreferredSize(new Dimension(40,40));
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setBorder(null);
        // Ensure that the program switches back to the UserProfile screen if back is pressed.
        backButton.addActionListener(e -> TLauncher.switchTo(new UserProfile(username), backButton));
        return backButton;
    }

    private JButton createDeleteAccountButton() {
        JButton deleteButton = new JButton("Delete Account");
        deleteButton.setFont(REGULAR_FONT);
        deleteButton.setBackground(new Color(220, 53, 69)); // Red color
        deleteButton.setForeground(Color.red);
        // Ensure a confirmation box is shown.
        deleteButton.addActionListener(e -> confirmDeleteAccount());
        return deleteButton;
    }

    // Method to create the fields that will display details of the user account.
    private void addField(String labelText, String value, boolean editable) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, HORIZONTAL_GAP, 10));

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(REGULAR_FONT);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, FIELD_HEIGHT));

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(REGULAR_FONT);
        valueLabel.setPreferredSize(new Dimension(LABEL_WIDTH * 2, FIELD_HEIGHT));

        fieldPanel.add(label);
        fieldPanel.add(valueLabel);

        // If it is "editable", then an edit button is added to allow the user to update the field.
        if (editable) {
            JButton editButton = new JButton();

            ImageIcon editIcon = new ImageIcon("Images/compose.png");
            Image img = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            editButton.setIcon(new ImageIcon(img));

            editButton.setPreferredSize(new Dimension(40,40));
            editButton.setContentAreaFilled(false);
            editButton.setFocusPainted(false);
            editButton.setBorder(null);
            editButton.addActionListener(e -> updateField(labelText, valueLabel));
            fieldPanel.add(editButton);
        }

        mainPanel.add(fieldPanel);
    }

    // Method to perform the update of the field when requested by the user.
    private void updateField(String fieldName, JLabel label) {
        String currentValue = label.getText();
        String newValue;

        if (fieldName.equals("Password")) {
            // Special handling for password change, since it needs extra validation.
            newValue = handlePasswordChange();
            // The validation failed or the operation was cancelled by the user.
            // TODO: If validation fails, restart the prompt. Only abort if cancelled.
            if (newValue == null) return;
        } else if (fieldName.equals("Phone")) {
            // Special handling for phone number, since it needs its own validation.
            newValue = handlePhoneChange();
            // The validation failed or the operation was cancelled by the user.
            // TODO: If validation fails, restart the prompt. Only abort if cancelled.
            if (newValue == null) return;
        } else {
            // Normal edit, with no extra validation needed.
            newValue = JOptionPane.showInputDialog(this,
                    "Enter new " + fieldName + ":",
                    "Update " + fieldName,
                    JOptionPane.PLAIN_MESSAGE);
        }
        // Only continue if the new value was not set to empty.
        if (newValue != null && !newValue.trim().isEmpty()) {
            // If it's an email input, then ensure it's valid.
            if (fieldName.equals("Email") && !isValidEmail(newValue)) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid email address",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                AccountDatabase accountDB = new AccountDatabase();

                switch (fieldName) {
                    case "Name":
                        accountDB.updateAccount(username, newValue);
                        this.name = newValue;
                        break;
                    case "Password":
                        accountDB.updatePassword(username, newValue);
                        break;
                    case "Phone":
                        accountDB.updatePhone(username, newValue);
                        this.phone = newValue;
                        break;
                    case "Email":
                        accountDB.updateEmail(username, newValue);
                        this.email = newValue;
                        break;
                    default:
                        JOptionPane.showMessageDialog(this,
                                "Updating " + fieldName + " is not yet supported",
                                "Not Implemented",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                }

                // If the password was changed, ensure asterisks are still displayed.
                if (fieldName.equals("Password")) {
                    label.setText("********");
                } else {
                    label.setText(newValue);
                }

                JOptionPane.showMessageDialog(this,
                        fieldName + " updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating " + fieldName + ": " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String handlePasswordChange() {
        // Ask for current password
        JPasswordField currentPassField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(this, currentPassField,
                "Enter current password:",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return null;

        // Verify current password
        String currentPass = new String(currentPassField.getPassword());
        AccountDatabase accountDB = new AccountDatabase();
        if (!accountDB.verifyAccount(username, currentPass)) {
            JOptionPane.showMessageDialog(this,
                    "Current password is incorrect",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // Ask for new password
        JPasswordField newPassField = new JPasswordField();
        result = JOptionPane.showConfirmDialog(this, newPassField,
                "Enter new password:",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return null;

        // Confirm new password
        JPasswordField confirmPassField = new JPasswordField();
        result = JOptionPane.showConfirmDialog(this, confirmPassField,
                "Confirm new password:",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) return null;

        // Verify passwords match
        String newPass = new String(newPassField.getPassword());
        String confirmPass = new String(confirmPassField.getPassword());

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this,
                    "New passwords do not match",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // The password will be returned in plain text, but it will be hashed later.
        return newPass;
    }

    private String handlePhoneChange() {
        // Create a panel with country code and phone number fields
        JPanel phonePanel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        JLabel countryCodeLabel = new JLabel("Country Code:");
        JTextField countryCodeField = new JTextField("+1", 5);
        
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        JTextField phoneNumberField = new JTextField(10);
        
        phonePanel.add(countryCodeLabel);
        phonePanel.add(countryCodeField);
        phonePanel.add(phoneNumberLabel);
        phonePanel.add(phoneNumberField);
        
        int result = JOptionPane.showConfirmDialog(this, phonePanel,
                "Enter Phone Number with Country Code",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        
        if (result != JOptionPane.OK_OPTION) return null;
        
        String countryCode = countryCodeField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        
        // Validate country code format
        if (!countryCode.startsWith("+")) {
            countryCode = "+" + countryCode;
        }
        
        // Validate phone number
        if (!isValidPhone(phoneNumber)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid phone number (e.g., 555-123-4567)",
                    "Invalid Phone Number",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        return countryCode + " " + phoneNumber;
    }

    private boolean isValidEmail(String email) {
        // This is a regular expression we are using to verify the email is in an acceptable format.
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isValidPhone(String phone) {
        // Ensure the phone matches one of the allowed formats, as described in the inline comments.
        return phone.matches("\\d{3}-\\d{3}-\\d{4}") ||  // 555-123-4567
               phone.matches("\\d{8,}") ||               // 8+ digits (55512345 or more)
               phone.matches("\\d{3,4}-\\d{4,}") ||      // 555-1234 or more
               phone.matches("\\(\\d{3}\\) \\d{3}-\\d{4}") || // (555) 123-4567
               phone.matches("\\d{3,4} \\d{4,}");        // 555 1234 or more
    }

    private void confirmDeleteAccount() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?\nThis action cannot be undone.",
                "Confirm Delete Account",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            try {
                AccountDatabase accountDB = new AccountDatabase();
                accountDB.deleteAccount(username);

                JOptionPane.showMessageDialog(this,
                        "Account deleted successfully",
                        "Account Deleted",
                        JOptionPane.INFORMATION_MESSAGE);

                // Return to the login screen now the account has been deleted.
                new Login();
                // Get rid of this screen.
                this.dispose();
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting account: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addChildField(String childName) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER, HORIZONTAL_GAP, 10));

        // Child name label
        JLabel label = new JLabel("Child:");
        label.setFont(REGULAR_FONT);
        label.setPreferredSize(new Dimension(LABEL_WIDTH, FIELD_HEIGHT));

        // Child name value
        JLabel valueLabel = new JLabel(childName);
        valueLabel.setFont(REGULAR_FONT);
        valueLabel.setPreferredSize(new Dimension(LABEL_WIDTH * 2, FIELD_HEIGHT));

        fieldPanel.add(label);
        fieldPanel.add(valueLabel);

        // Edit button
        JButton editButton = new JButton();
        ImageIcon editIcon = new ImageIcon("Images/compose.png");
        Image img = editIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        editButton.setIcon(new ImageIcon(img));
        editButton.setPreferredSize(new Dimension(40,40));
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setBorder(null);
        editButton.addActionListener(e -> updateChildName(childName, valueLabel));

        // Delete button
        JButton deleteButton = new JButton();
        ImageIcon deleteIcon = new ImageIcon("Images/bin.png");
        Image deleteImg = deleteIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        deleteButton.setIcon(new ImageIcon(deleteImg));
        deleteButton.setPreferredSize(new Dimension(40,40));
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(null);
        deleteButton.addActionListener(e -> deleteChild(childName));

        fieldPanel.add(editButton);
        fieldPanel.add(deleteButton);

        mainPanel.add(fieldPanel);
    }

    private void updateChildName(String oldName, JLabel nameLabel) {
        String newName = JOptionPane.showInputDialog(this,
                "Enter new name for " + oldName + ":",
                "Rename Child",
                JOptionPane.PLAIN_MESSAGE);

        if (newName != null && !newName.trim().isEmpty()) {
            try {
                ChildDatabase childDB = new ChildDatabase(username);
                // Check if new name already exists
                if (childDB.getChild(newName) != null) {
                    JOptionPane.showMessageDialog(this,
                            "A child with this name already exists!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Delete old and add new
                // TODO: This will not preserve child data! Ideally copy data over too!
                childDB.deleteChild(oldName);
                childDB.addChild(newName.trim());
                nameLabel.setText(newName.trim());

                JOptionPane.showMessageDialog(this,
                        "Child renamed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error renaming child: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteChild(String childName) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + childName + "?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ChildDatabase childDB = new ChildDatabase(username);
                childDB.deleteChild(childName);

                // Refresh all components on the entire settings page, to display new values.
                mainPanel.removeAll();
                initializeComponents();
                mainPanel.revalidate();
                mainPanel.repaint();

                JOptionPane.showMessageDialog(this,
                        "Child deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting child: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
