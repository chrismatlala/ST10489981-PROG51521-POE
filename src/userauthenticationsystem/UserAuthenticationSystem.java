package userauthenticationsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;

public class UserAuthenticationSystem {
    private String username;
    private String password;
    private String cellNumber;
    private String firstName;
    private String lastName;
    private boolean isLoggedIn = false;

    // === GUI Entry Point ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserAuthenticationSystem app = new UserAuthenticationSystem();
            app.showMainMenu();
        });
    }

    // === GUI: Main Menu ===
    private void showMainMenu() {
        JFrame frame = new JFrame("QuickChat Messaging Apllication");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to QuickChat.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton sendBtn = new JButton("Send Messages");
        JButton recentBtn = new JButton("Show Recently Sent Messages");
        JButton storedBtn = new JButton("View Stored Messages");
        JButton quitBtn = new JButton("Quit");

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.add(sendBtn);
        buttonPanel.add(recentBtn);
        buttonPanel.add(storedBtn);
        buttonPanel.add(quitBtn);

        frame.add(welcomeLabel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // === Event Listeners ===
        sendBtn.addActionListener(e -> sendMessagesGUI());
        recentBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Coming Soon."));
        storedBtn.addActionListener(e -> viewStoredMessagesGUI());
        quitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Thank you for using QuickChat. Goodbye!");
            frame.dispose();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // === GUI: Send Messages ===
    private void sendMessagesGUI() {
        JFrame sendFrame = new JFrame("Send Message");
        sendFrame.setSize(400, 400);
        sendFrame.setLayout(new GridLayout(7, 1, 10, 10));

        JTextField recipientField = new JTextField();
        JTextArea messageArea = new JTextArea();
        JButton sendBtn = new JButton("Send Message");
        JButton storeBtn = new JButton("Store Message");
        JButton discardBtn = new JButton("Discard Message");

        sendFrame.add(new JLabel("Recipient Cell Number (with +code):"));
        sendFrame.add(recipientField);
        sendFrame.add(new JLabel("Message (max 250 characters):"));
        sendFrame.add(new JScrollPane(messageArea));
        sendFrame.add(sendBtn);
        sendFrame.add(storeBtn);
        sendFrame.add(discardBtn);

        sendFrame.setLocationRelativeTo(null);
        sendFrame.setVisible(true);

        // Message object
        Message message = new Message();

        // === Action Listeners ===
        sendBtn.addActionListener(e -> {
            String recipient = recipientField.getText().trim();
            String text = messageArea.getText().trim();

            if (message.checkRecipientCell(recipient) == 0) {
                JOptionPane.showMessageDialog(sendFrame, "Invalid phone number. Must include international code.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (text.length() > 250) {
                JOptionPane.showMessageDialog(sendFrame, "Message exceeds 250 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            message.setRecipient(recipient);
            message.setMessage(text);
            String result = message.sentMessage(1); // Send
            JOptionPane.showMessageDialog(sendFrame, result + "\n\n" + message.printMessages(), "Message Sent", JOptionPane.INFORMATION_MESSAGE);
        });

        storeBtn.addActionListener(e -> {
            String recipient = recipientField.getText().trim();
            String text = messageArea.getText().trim();

            if (message.checkRecipientCell(recipient) == 0 || text.isEmpty()) {
                JOptionPane.showMessageDialog(sendFrame, "Please fill in all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            message.setRecipient(recipient);
            message.setMessage(text);
            String result = message.sentMessage(3); // Store
            JOptionPane.showMessageDialog(sendFrame, result, "Stored", JOptionPane.INFORMATION_MESSAGE);
        });

        discardBtn.addActionListener(e -> {
            recipientField.setText("");
            messageArea.setText("");
            JOptionPane.showMessageDialog(sendFrame, "Message discarded.");
        });
    }

    // === GUI: View Stored Messages ===
    private void viewStoredMessagesGUI() {
        JFrame viewFrame = new JFrame("Stored Messages");
        viewFrame.setSize(400, 300);

        JTextArea messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setText(JSONHandler.getAllMessages());

        viewFrame.add(new JScrollPane(messagesArea));
        viewFrame.setLocationRelativeTo(null);
        viewFrame.setVisible(true);
    }

    // === Validation Methods ===
    public boolean checkUsername(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    public boolean checkPasswordComplexity(String password) {
        if (password.length() < 8) return false;
        if (!Pattern.compile("[A-Z]").matcher(password).find()) return false;
        if (!Pattern.compile("[0-9]").matcher(password).find()) return false;
        return Pattern.compile("[^A-Za-z0-9]").matcher(password).find();
    }

    public boolean checkCellPhoneNumber(String cellNumber) {
        String pattern = "^\\+\\d{1,3}\\d{7,10}$";
        return Pattern.matches(pattern, cellNumber);
    }

    // === Login ===
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return enteredUsername.equals(this.username) && enteredPassword.equals(this.password);
    }

    public String returnLoginStatus(boolean isSuccessful) {
        if (isSuccessful) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}