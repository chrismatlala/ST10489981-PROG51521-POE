package userauthenticationsystem;

import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Message {
    
    // Instance variables
    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private boolean sent;
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;
    private static List<Message> sentMessages = new ArrayList<>();
    private static List<Message> storedMessages = new ArrayList<>();
    
    // Constants
    private static final int MAX_MESSAGE_LENGTH = 250;
    private static final int MAX_RECIPIENT_LENGTH = 15;
    
    // Constructor
    public Message(String recipient, String messageText) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = generateMessageHash();
        this.sent = false;
        messageCounter++;
    }
    
    // Method 1: Check message length - USING JOPTIONPANE
    public Boolean checkMessageLength() {
        if (messageText == null) {
            JOptionPane.showMessageDialog(null, "Message cannot be null.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (messageText.length() > MAX_MESSAGE_LENGTH) {
            int excessChars = messageText.length() - MAX_MESSAGE_LENGTH;
            JOptionPane.showMessageDialog(null, 
                "Message exceeds " + MAX_MESSAGE_LENGTH + " characters by " + excessChars + ", please reduce size.",
                "Message Too Long", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        JOptionPane.showMessageDialog(null, "Message ready to send.", "Success", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }
    
    // Method 2: Check recipient phone number format - USING JOPTIONPANE
    public Boolean checkRecipientFormat() {
        if (recipient == null || recipient.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Recipient number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Check length
        if (recipient.length() > MAX_RECIPIENT_LENGTH) {
            JOptionPane.showMessageDialog(null, 
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                "Invalid Number", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check if starts with international code (typically starts with +)
        if (!recipient.startsWith("+")) {
            JOptionPane.showMessageDialog(null, 
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                "Invalid Format", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check if the rest are digits
        String numberPart = recipient.substring(1);
        if (!numberPart.matches("\\d+")) {
            JOptionPane.showMessageDialog(null, 
                "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                "Invalid Characters", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        JOptionPane.showMessageDialog(null, "Cell phone number successfully captured.", "Success", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }
    
    // Method 3: Generate and return Message Hash
    public String checkMessageHash() {
        if (messageHash == null) {
            this.messageHash = generateMessageHash();
        }
        return this.messageHash;
    }
    
    // Method 4: User choice for message action - USING JOPTIONPANE
    public String sendMessageAction() {
        String[] options = {"Send", "Discard", "Store"};
        int choice = JOptionPane.showOptionDialog(null,
            "What would you like to do with this message?\n" +
            "Recipient: " + recipient + "\n" +
            "Message: " + (messageText.length() > 50 ? messageText.substring(0, 50) + "..." : messageText) + "\n" +
            "Message Hash: " + checkMessageHash(),
            "Message Action",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        switch (choice) {
            case 0: // Send
                if (checkMessageLength() && checkRecipientFormat()) {
                    this.sent = true;
                    totalMessagesSent++;
                    sentMessages.add(this);
                    JOptionPane.showMessageDialog(null, "Message successfully sent to " + recipient, "Success", JOptionPane.INFORMATION_MESSAGE);
                    return "send";
                } else {
                    JOptionPane.showMessageDialog(null, "Message validation failed. Cannot send.", "Error", JOptionPane.ERROR_MESSAGE);
                    return "validation_failed";
                }
                
            case 1: // Discard
                int discardConfirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to discard this message?",
                    "Confirm Discard",
                    JOptionPane.YES_NO_OPTION);
                
                if (discardConfirm == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Message discarded.", "Discarded", JOptionPane.INFORMATION_MESSAGE);
                    return "discard";
                } else {
                    return sendMessageAction(); // Show options again
                }
                
            case 2: // Store
                storeMessage();
                storedMessages.add(this);
                JOptionPane.showMessageDialog(null, "Message successfully stored.", "Stored", JOptionPane.INFORMATION_MESSAGE);
                return "store";
                
            default: // Closed dialog or cancel
                return "cancelled";
        }
    }
    
    // Method 5: Return all sent messages - USING JOPTIONPANE
    public void displaySentMessages() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages sent yet.", "Sent Messages", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder allMessages = new StringBuilder();
        allMessages.append("All Sent Messages:\n");
        allMessages.append("==================\n\n");
        
        for (int i = 0; i < sentMessages.size(); i++) {
            Message msg = sentMessages.get(i);
            allMessages.append("Message ").append(i + 1).append(":\n");
            allMessages.append("  Message ID: ").append(msg.getMessageID()).append("\n");
            allMessages.append("  Message Hash: ").append(msg.getMessageHash()).append("\n");
            allMessages.append("  Recipient: ").append(msg.getRecipient()).append("\n");
            allMessages.append("  Message: ").append(msg.getMessageText()).append("\n");
            allMessages.append("------------------------\n");
        }
        
        JOptionPane.showMessageDialog(null, allMessages.toString(), "Sent Messages History", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Method 6: Display total number of messages sent - USING JOPTIONPANE
    public void displayTotalMessages() {
        JOptionPane.showMessageDialog(null, 
            "Total messages sent: " + totalMessagesSent + "\n" +
            "Total messages stored: " + storedMessages.size(),
            "Message Statistics", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Method 7: Store message in JSON file
    public void storeMessage() {
        try (FileWriter writer = new FileWriter("stored_messages.json", true)) {
            String jsonMessage = String.format(
                "{\"messageID\": \"%s\", \"recipient\": \"%s\", \"message\": \"%s\", \"messageHash\": \"%s\"}%n",
                messageID, recipient, messageText.replace("\"", "\\\""), messageHash
            );
            writer.write(jsonMessage);
            writer.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error storing message: " + e.getMessage(), "Storage Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper method to generate Message ID
    private String generateMessageID() {
        return String.format("MSG%06d", messageCounter + 1);
    }
    
    // Helper method to generate Message Hash
    private String generateMessageHash() {
        if (messageText == null || messageText.isEmpty()) {
            return "00-0-null-null";
        }
        
        String[] words = messageText.split("\\s+");
        String firstTwoNumbers = messageID.length() >= 2 ? messageID.substring(3, 5) : "00";
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        return String.format("%s-token-%d-%s-%s", 
            firstTwoNumbers, messageCounter, firstWord, lastWord).toLowerCase();
    }
    
    // Getters
    public String getMessageID() {
        return messageID;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public String getMessageText() {
        return messageText;
    }
    
    public String getMessageHash() {
        return messageHash;
    }
    
    public boolean isSent() {
        return sent;
    }
    
    // Setters
    public void setRecipient(String recipient) {
        this.recipient = recipient;
        this.messageHash = generateMessageHash();
    }
    
    public void setMessageText(String messageText) {
        this.messageText = messageText;
        this.messageHash = generateMessageHash();
    }
    
    // Static method to get total messages sent
    public static int getTotalMessagesSent() {
        return totalMessagesSent;
    }
    
    // Static method to get stored messages count
    public static int getStoredMessagesCount() {
        return storedMessages.size();
    }
    
    // Static method to clear all messages (for testing)
    public static void clearAllMessages() {
        sentMessages.clear();
        storedMessages.clear();
        totalMessagesSent = 0;
        messageCounter = 0;
    }
    
    // Method to create message through dialog - USING JOPTIONPANE
    public static Message createMessageDialog() {
        String recipient = JOptionPane.showInputDialog(null, 
            "Enter recipient phone number (with international code):", 
            "Recipient", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (recipient == null || recipient.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Recipient cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String messageText = JOptionPane.showInputDialog(null, 
            "Enter your message (max " + MAX_MESSAGE_LENGTH + " characters):", 
            "Message", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (messageText == null || messageText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Message cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        return new Message(recipient.trim(), messageText.trim());
    }
    
    @Override
    public String toString() {
        return String.format(
            "Message ID: %s | Hash: %s | Recipient: %s | Message: %s | Status: %s",
            messageID, messageHash, recipient, 
            messageText.length() > 20 ? messageText.substring(0, 20) + "..." : messageText,
            sent ? "Sent" : "Stored"
        );
    }
}