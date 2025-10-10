package userauthenticationsystem;


import java.util.regex.Pattern;
import java.util.regex.Matcher;
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
    
    // Constants
    private static final int MAX_MESSAGE_LENGTH = 100;
    private static final int MAX_RECIPIENT_LENGTH = 10;
    
    // Constructor
    public Message(String recipient, String messageText) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = generateMessageHash();
        this.sent = false;
        messageCounter++;
    }
    
    // Method 1: Check message length
    public Boolean checkMessage() {
        if (messageText == null) {
            System.out.println("Message cannot be null.");
            return false;
        }
        
        if (messageText.length() > MAX_MESSAGE_LENGTH) {
            int excessChars = messageText.length() - MAX_MESSAGE_LENGTH;
            System.out.println("Message exceeds 100 characters by " + excessChars + ", please reduce size.");
            return false;
        }
        
        System.out.println("Message ready to send.");
        return true;
    }
    
    // Method 2: Check recipient phone number format
    public Boolean checkRecipient() {
        if (recipient == null || recipient.isEmpty()) {
            System.out.println("Recipient number cannot be empty.");
            return false;
        }
        
        // Check length
        if (recipient.length() > MAX_RECIPIENT_LENGTH) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
            return false;
        }
        
        // Check if starts with international code (typically starts with +)
        if (!recipient.startsWith("+")) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
            return false;
        }
        
        // Check if the rest are digits
        String numberPart = recipient.substring(1);
        if (!numberPart.matches("\\d+")) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
            return false;
        }
        
        System.out.println("Cell phone number successfully captured.");
        return true;
    }
    
    // Method 3: Generate and return Message Hash
    public String checkMessageHash() {
        if (messageHash == null) {
            this.messageHash = generateMessageHash();
        }
        return this.messageHash;
    }
    
    // Method 4: User choice for message action
    public String setMessage(String userChoice) {
        switch (userChoice.toLowerCase()) {
            case "send":
                if (checkMessage() && checkRecipient()) {
                    this.sent = true;
                    totalMessagesSent++;
                    sentMessages.add(this);
                    return "Message successfully sent.";
                } else {
                    return "Message validation failed. Cannot send.";
                }
                
            case "discard":
                return "Press on delete message.";
                
            case "store":
                storeMessage();
                return "Message successfully stored.";
                
            default:
                return "Invalid choice. Please select send, discard, or store.";
        }
    }
    
    // Method 5: Return all sent messages
    public String printMessage() {
        StringBuilder allMessages = new StringBuilder();
        allMessages.append("All Sent Messages:\n");
        allMessages.append("==================\n");
        
        for (int i = 0; i < sentMessages.size(); i++) {
            Message msg = sentMessages.get(i);
            allMessages.append("Message ").append(i + 1).append(":\n");
            allMessages.append("  Message ID: ").append(msg.getMessageID()).append("\n");
            allMessages.append("  Message Hash: ").append(msg.getMessageHash()).append("\n");
            allMessages.append("  Recipient: ").append(msg.getRecipient()).append("\n");
            allMessages.append("  Message: ").append(msg.getMessageText()).append("\n");
            allMessages.append("------------------------\n");
        }
        
        return allMessages.toString();
    }
    
    // Method 6: Return total number of messages sent
    public int returnTotalMessages() {
        return totalMessagesSent;
    }
    
    // Method 7: Store message in JSON file
    public void storeMessage() {
        try (FileWriter writer = new FileWriter("stored_messages.json", true)) {
            String jsonMessage = String.format(
                "{\"messageID\": \"%s\", \"recipient\": \"%s\", \"message\": \"%s\", \"messageHash\": \"%s\"}%n",
                messageID, recipient, messageText.replace("\"", "\\\""), messageHash
            );
            writer.write(jsonMessage);
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }
    
    // Helper method to generate Message ID
    private String generateMessageID() {
        return String.format("%08d", messageCounter + 1);
    }
    
    // Helper method to generate Message Hash
    private String generateMessageHash() {
        if (messageText == null || messageText.isEmpty()) {
            return "00-0-null-null";
        }
        
        String[] words = messageText.split("\\s+");
        String firstTwoNumbers = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
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
    
    // Static method to clear all messages (for testing)
    public static void clearAllMessages() {
        sentMessages.clear();
        totalMessagesSent = 0;
        messageCounter = 0;
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

    void setMessage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}