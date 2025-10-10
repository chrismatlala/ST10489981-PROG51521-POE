package userauthenticationsystem;


import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.io.*;
import java.lang.reflect.Type;

public class Message {
    // Instance variables
    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String status; // "sent", "stored", "discarded"
    private String timestamp;
    
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;
    private static List<Message> sentMessages = new ArrayList<>();
    private static List<Message> storedMessages = new ArrayList<>();
    private static final String STORED_MESSAGES_FILE = "stored_messages.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    // Constants
    private static final int MAX_MESSAGE_LENGTH = 100;
    private static final int MAX_RECIPIENT_LENGTH = 10;
    
    // Constructors
    public Message(String recipient, String messageText) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = generateMessageHash();
        this.status = "created";
        this.timestamp = java.time.LocalDateTime.now().toString();
        messageCounter++;
    }
    
    // JSON-specific constructor
    public Message(String messageID, String recipient, String messageText, String messageHash, String status, String timestamp) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = messageHash;
        this.status = status;
        this.timestamp = timestamp;
    }
    
    // Method to display single message using JOptionPane
    public void displayMessage() {
        String messageDetails = formatMessageForDisplay();
        JOptionPane.showMessageDialog(
            null,
            messageDetails,
            "Message Details - ID: " + this.messageID,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Method to display all sent messages using JOptionPane
    public static void displayAllSentMessages() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(
                null,
                "No messages have been sent yet.",
                "Sent Messages",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        StringBuilder allMessages = new StringBuilder();
        allMessages.append("All Sent Messages:\n");
        allMessages.append("==================\n\n");
        
        for (int i = 0; i < sentMessages.size(); i++) {
            Message msg = sentMessages.get(i);
            allMessages.append("Message ").append(i + 1).append(":\n");
            allMessages.append(msg.formatMessageForDisplay());
            allMessages.append("\n------------------------\n\n");
        }
        
        // Add total count
        allMessages.append("Total Messages Sent: ").append(totalMessagesSent);
        
        JOptionPane.showMessageDialog(
            null,
            allMessages.toString(),
            "All Sent Messages",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Method to display stored messages from JSON using JOptionPane
    public static void displayStoredMessages() {
        try {
            List<Message> storedMessages = loadStoredMessages();
            
            if (storedMessages.isEmpty()) {
                JOptionPane.showMessageDialog(
                    null,
                    "No stored messages found.",
                    "Stored Messages",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Stored Messages (from JSON):\n");
            sb.append("============================\n\n");
            
            for (int i = 0; i < storedMessages.size(); i++) {
                Message msg = storedMessages.get(i);
                sb.append("Message ").append(i + 1).append(":\n");
                sb.append(msg.formatMessageForDisplay());
                sb.append("\n------------------------\n\n");
            }
            
            sb.append("Total Stored Messages: ").append(storedMessages.size());
            
            JOptionPane.showMessageDialog(
                null,
                sb.toString(),
                "Stored Messages",
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error loading stored messages: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    // Helper method to format message in the required order
    private String formatMessageForDisplay() {
        return String.format(
            "MessageID:    %s\n" +
            "Message Hash: %s\n" +
            "Recipient:    %s\n" +
            "Message:      %s\n" +
            "Status:       %s\n" +
            "Timestamp:    %s",
            this.messageID,
            this.messageHash,
            this.recipient,
            this.messageText,
            this.status,
            this.timestamp
        );
    }
    
    // Method to display message summary (for quick overview)
    public static void displayMessageSummary() {
        String summary = String.format(
            "Message Summary:\n" +
            "===============\n\n" +
            "Total Messages Sent:   %d\n" +
            "Total Messages Stored: %d\n" +
            "Current Message Count: %d",
            totalMessagesSent,
            getStoredMessagesCount(),
            messageCounter
        );
        
        JOptionPane.showMessageDialog(
            null,
            summary,
            "Message Summary",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Method to show message action results using JOptionPane
    public void showActionResult(String action, String result) {
        String actionMessage = String.format(
            "Action: %s\n\n" +
            "MessageID: %s\n" +
            "Result: %s\n\n" +
            "Current Status: %s",
            action.toUpperCase(),
            this.messageID,
            result,
            this.status
        );
        
        JOptionPane.showMessageDialog(
            null,
            actionMessage,
            "Message Action - " + action,
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    // Existing JSON methods (unchanged)
    public void storeMessage() {
        try {
            List<Message> allStoredMessages = loadStoredMessages();
            this.status = "stored";
            this.timestamp = java.time.LocalDateTime.now().toString();
            allStoredMessages.add(this);
            storedMessages.add(this);
            saveMessagesToJson(allStoredMessages);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error storing message: " + e.getMessage(),
                "Storage Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    public static List<Message> loadStoredMessages() throws IOException {
        File file = new File(STORED_MESSAGES_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (FileReader reader = new FileReader(file)) {
            Type messageListType = new TypeToken<List<Message>>(){
                private Type getType() {
                    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                }
            }.getType();
            List<Message> messages = gson.fromJson(reader, messageListType);
            return messages != null ? messages : new ArrayList<>();
        }
    }
    
    private static void saveMessagesToJson(List<Message> messages) throws IOException {
        try (FileWriter writer = new FileWriter(STORED_MESSAGES_FILE)) {
            gson.toJson(messages, writer);
        }
    }
    
    // Enhanced setMessage method with JOptionPane feedback
    public String setMessage(String userChoice) {
        String result;
        
        switch (userChoice.toLowerCase()) {
            case "send":
                if (checkMessage() && checkRecipient()) {
                    this.status = "sent";
                    this.timestamp = java.time.LocalDateTime.now().toString();
                    totalMessagesSent++;
                    sentMessages.add(this);
                    result = "Message successfully sent.";
                } else {
                    result = "Message validation failed. Cannot send.";
                }
                break;
                
            case "discard":
                this.status = "discarded";
                result = "Press on delete message.";
                break;
                
            case "store":
                storeMessage();
                result = "Message successfully stored.";
                break;
                
            default:
                result = "Invalid choice. Please select send, discard, or store.";
                break;
        }
        
        // Show action result in dialog
        showActionResult(userChoice, result);
        return result;
    }
    
    // Rest of the existing methods remain the same
    public Boolean checkMessage() {
        if (messageText == null) return false;
        return messageText.length() <= MAX_MESSAGE_LENGTH;
    }
    
    public Boolean checkRecipient() {
        if (recipient == null || recipient.isEmpty()) return false;
        if (recipient.length() > MAX_RECIPIENT_LENGTH) return false;
        if (!recipient.startsWith("+")) return false;
        String numberPart = recipient.substring(1);
        return numberPart.matches("\\d+");
    }
    
    public String checkMessageHash() {
        if (messageHash == null) {
            this.messageHash = generateMessageHash();
        }
        return this.messageHash;
    }
    
    public String printMessage() {
        StringBuilder allMessages = new StringBuilder();
        allMessages.append("All Sent Messages:\n");
        allMessages.append("==================\n");
        
        for (int i = 0; i < sentMessages.size(); i++) {
            Message msg = sentMessages.get(i);
            allMessages.append("Message ").append(i + 1).append(":\n");
            allMessages.append(msg.formatMessageForDisplay());
            allMessages.append("\n------------------------\n");
        }
        
        return allMessages.toString();
    }
    
    public int returnTotalMessages() {
        return totalMessagesSent;
    }
    
    private String generateMessageID() {
        return String.format("%08d", messageCounter + 1);
    }
    
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
    
    // Getters and Setters
    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
        this.messageHash = generateMessageHash();
    }
    
    public void setMessageText(String messageText) {
        this.messageText = messageText;
        this.messageHash = generateMessageHash();
    }
    
    public static int getTotalMessagesSent() { return totalMessagesSent; }
    
    public static void clearAllMessages() {
        sentMessages.clear();
        storedMessages.clear();
        totalMessagesSent = 0;
        messageCounter = 0;
    }
    
    public static void clearStoredMessages() throws IOException {
        saveMessagesToJson(new ArrayList<>());
        storedMessages.clear();
    }
    
    public static int getStoredMessagesCount() {
        try {
            return loadStoredMessages().size();
        } catch (IOException e) {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return formatMessageForDisplay();
    }

    private static class Gson {

        public Gson() {
        }

        private void toJson(List<Message> messages, FileWriter writer) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private List<Message> fromJson(FileReader reader, Type messageListType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class TypeToken<T> {

        public TypeToken() {
        }
    }
}