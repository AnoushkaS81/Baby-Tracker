package application;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Helper_Bot extends JFrame {
    private JPanel chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton backButton;
    private final Color PRIMARY_COLOR = new Color(79, 121, 247);  // Modern Blue
    private final Color BACKGROUND_COLOR = new Color(248, 250, 252);  // Light Gray-Blue
    private final Color TEXT_COLOR = new Color(31, 41, 55);  // Dark Gray
    private final Color ACCENT_COLOR = new Color(236, 242, 255);  // Light Blue for highlights
    private final Font MAIN_FONT = new Font("Inter", Font.PLAIN, 16);
    private final Font HEADER_FONT = new Font("Inter", Font.BOLD, 26);
    private final Font INPUT_FONT = new Font("Inter", Font.PLAIN, 16);
    
    public Helper_Bot(String username) {
        setTitle("Parenting Assistant");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create main panel with modern styling
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Add header panel with back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);

        // Create back button
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(PRIMARY_COLOR);
        backButton.setBorder(null);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFocusPainted(false);

        // Create title label panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel headerLabel = new JLabel("Parenting Assistant");
        headerLabel.setFont(HEADER_FONT);
        headerLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(headerLabel);

        // Add components to header panel
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Style chat area with modern look
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(Color.WHITE);
        
        // Enhanced welcome message with better spacing and emojis
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JTextArea welcomeText = new JTextArea(
            "👋  Welcome to your Parenting Assistant!\n\n" +
            "I'm here to provide guidance and support for your parenting journey. " +
            "Feel free to ask about:\n\n" +
            "     🌱  Child Development\n" +
            "     🤗  Behavioral Management\n" +
            "     ⏰  Daily Routines\n" +
            "     💡  Parenting Tips & Strategies\n" +
            "     🎯  Age-Appropriate Activities\n\n" +
            "How can I help you today?\n\n");
        welcomeText.setFont(MAIN_FONT);
        welcomeText.setEditable(false);
        welcomeText.setLineWrap(true);
        welcomeText.setWrapStyleWord(true);
        welcomeText.setBackground(ACCENT_COLOR);
        welcomeText.setForeground(TEXT_COLOR);
        welcomePanel.add(welcomeText, BorderLayout.CENTER);
        welcomePanel.setBackground(ACCENT_COLOR);
        welcomePanel.setBorder(new RoundedBorder(15));
        chatArea.add(welcomePanel);
        
        // Modern scroll pane styling
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(226, 232, 240), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        
        // Create styled input panel
        JPanel inputPanel = new JPanel(new BorderLayout(15, 0));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Style input field with modern look
        inputField = new JTextField();
        inputField.setFont(INPUT_FONT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        // Enhanced send button styling
        sendButton = new JButton("Send →");
        sendButton.setFont(new Font("Inter", Font.BOLD, 14));
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setBorder(new RoundedBorder(12));
        sendButton.setPreferredSize(new Dimension(120, 45));
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setFocusPainted(false);
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        // Assemble all components
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add action listeners
        sendButton.addActionListener(e -> handleUserInput());
        inputField.addActionListener(e -> handleUserInput());

        // Add back button action listener
        backButton.addActionListener(e -> TLauncher.switchTo(new UserProfile(username), backButton));


        
        setVisible(true);
    }
    
    // Custom rounded border for send button
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
    }
    
    private void handleUserInput() {
        String userInput = inputField.getText().trim();
        if (!userInput.isEmpty()) {
            appendMessage("You: " + userInput);
            String response = generateResponse(userInput);
            appendMessage("Assistant: " + response);
            inputField.setText("");
        }
    }
    
    private void appendMessage(String message) {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BorderLayout());
        
        JTextArea messageText = new JTextArea();
        messageText.setFont(MAIN_FONT);
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setEditable(false);
        
        // Use consistent colors for all messages
        messageText.setBackground(ACCENT_COLOR);
        messageText.setForeground(TEXT_COLOR);
        messagePanel.setBackground(ACCENT_COLOR);
        
        if (message.startsWith("You: ")) {
            // User message styling - just change the icon/prefix
            messageText.setText(message.substring(5));
            messagePanel.add(new JLabel("👤 "), BorderLayout.WEST);
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 0, 5, 50),
                new RoundedBorder(15)
            ));
        } else if (message.startsWith("Assistant: ")) {
            // Assistant message styling - just change the icon/prefix
            messageText.setText(message.substring(11));
            messagePanel.add(new JLabel("🤖 "), BorderLayout.WEST);
            messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 50, 5, 0),
                new RoundedBorder(15)
            ));
        }
        
        messagePanel.add(messageText, BorderLayout.CENTER);
        
        // Add the message panel to the chat area
        chatArea.add(messagePanel);
        chatArea.revalidate();
        chatArea.repaint();
    }
    
    private String generateResponse(String input) {
        input = input.toLowerCase();
        
        // Show typing indicator
        showTypingIndicator();
        
        // Add thinking delay
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (input.contains("sleep") || input.contains("bedtime")) {
            return "Let me help you with sleep routines! 😊\n\n" +
                   "Here's what I recommend for better bedtime:\n\n" +
                   "1. Set a consistent bedtime\n" +
                   "2. Create a calming environment\n" +
                   "3. Read a bedtime story\n" +
                   "4. Limit screen time before bed\n" +
                   "5. Use soft lighting and quiet activities\n\n" +
                   "Remember, consistency is key for better sleep habits! 😴";
        } else if (input.contains("eat") || input.contains("food") || input.contains("meal")) {
            return "Great question about nutrition! 🍎\n\n" +
                   "Here are some healthy eating tips:\n\n" +
                   "1. Offer colorful, nutritious foods\n" +
                   "2. Make mealtimes fun and social\n" +
                   "3. Be patient with new foods\n" +
                   "4. Lead by example\n" +
                   "5. Avoid using food as reward/punishment\n\n" +
                   "Would you like specific meal planning suggestions? 🥗";
        } else if (input.contains("tantrum") || input.contains("angry") || input.contains("crying")) {
            return "I understand this can be challenging. 💝\n\n" +
                   "Here's how to manage emotional moments:\n\n" +
                   "1. Stay calm - your peace is their anchor\n" +
                   "2. Acknowledge their feelings\n" +
                   "3. Create a safe space\n" +
                   "4. Use simple, loving words\n" +
                   "5. Offer comfort and understanding\n\n" +
                   "Remember: Big emotions are normal, they need guidance to learn regulation. 🤗";
        } else if (input.contains("play") || input.contains("game") || input.contains("activity")) {
            return "Here are some fun activity ideas! 🎮\n\n" +
                   "1. Nature scavenger hunt\n" +
                   "2. DIY sensory bins\n" +
                   "3. Indoor obstacle course\n" +
                   "4. Puppet show with socks\n" +
                   "5. Baking simple recipes together\n\n" +
                   "Would you like more age-specific activities? 🧩";
        } else if (input.contains("potty") || input.contains("toilet") || input.contains("training")) {
            return "Potty training tips coming up! 🚽\n\n" +
                   "1. Look for readiness signs\n" +
                   "2. Use positive reinforcement\n" +
                   "3. Create a consistent routine\n" +
                   "4. Be patient and supportive\n" +
                   "5. Celebrate small successes\n\n" +
                   "Remember, every child develops at their own pace! 🌈";
        }
        
        return "I understand you're asking about '" + input + "'.\n\n" +
               "Could you please provide more details? I want to give you the most helpful advice possible for your specific situation. 💭\n\n" +
               "Feel free to ask about child development, behavior, routines, or any other parenting concerns!";
    }
    
    // Add this new class for modern scrollbar
    private class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(203, 213, 225);
            this.trackColor = Color.WHITE;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            return button;
        }
    }
    
    private void showTypingIndicator() {
        JPanel typingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typingPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel typingLabel = new JLabel("Assistant is typing...");
        typingLabel.setFont(new Font("Inter", Font.ITALIC, 14));
        typingLabel.setForeground(new Color(150, 150, 150));
        
        typingPanel.add(typingLabel);
        chatArea.add(typingPanel);
        chatArea.revalidate();
        chatArea.repaint();
        
        // Remove after response is generated
        Timer timer = new Timer(1500, e -> {
            chatArea.remove(typingPanel);
            chatArea.revalidate();
            chatArea.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private void saveChat(String username) {
        try {
            File dir = new File("chats");
            if (!dir.exists()) {
                dir.mkdir();
            }
            
            StringBuilder chatText = new StringBuilder();
            for (Component comp : chatArea.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel messagePanel = (JPanel) comp;
                    for (Component innerComp : messagePanel.getComponents()) {
                        if (innerComp instanceof JTextArea) {
                            chatText.append(((JTextArea) innerComp).getText()).append("\n");
                        }
                    }
                }
            }
            
            FileWriter writer = new FileWriter("chats/" + username + "_chat.txt", true);
            writer.write(chatText.toString() + "\n--- Session End: " + 
                         new java.util.Date() + " ---\n\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPreviousChat(String username) {
        try {
            File chatFile = new File("chats/" + username + "_chat.txt");
            if (chatFile.exists()) {
                Scanner reader = new Scanner(chatFile);
                StringBuilder previousChat = new StringBuilder();
                
                while (reader.hasNextLine()) {
                    previousChat.append(reader.nextLine()).append("\n");
                }
                
                // Add a "Previous Conversations" header
                JPanel historyPanel = new JPanel(new BorderLayout());
                JTextArea historyText = new JTextArea("--- Previous Conversations ---\n\n" + 
                                                     previousChat.toString() + 
                                                     "\n--- New Session ---\n\n");
                historyText.setFont(MAIN_FONT);
                historyText.setEditable(false);
                historyText.setLineWrap(true);
                historyText.setWrapStyleWord(true);
                historyPanel.add(historyText, BorderLayout.CENTER);
                
                chatArea.add(historyPanel, 0); // Add at the beginning of the chat area
                
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

}
