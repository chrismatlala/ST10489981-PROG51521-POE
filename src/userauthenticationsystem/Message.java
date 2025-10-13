package userauthenticationsystem;

import java.util.Random;

public class Message {
    private String messageID;
    private int messageCount;
    private String recipient;
    private String message;
    private String messageHash;
    private String status; // "sent", "stored", "discarded"
    private static int totalMessagesSent = 0;
    private static int messageCounter = 0;

    public Message() {
        this.messageID = generateMessageID();
        this.messageCount = ++messageCounter;
        this.status = "pending";
    }

    // Generate random 10-digit message ID
    private String generateMessageID() {
        Random rand = new Random();
        long id = 1000000000L + (long)(rand.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }

    public boolean checkMessageID() {
        return this.messageID.length() == 10;
    }

    public int checkRecipientCell(String recipient) {
        // Check if number starts with international code and has proper length
        if (recipient.startsWith("+") && recipient.length() <= 13 && recipient.length() >= 11) {
            String numberPart = recipient.substring(1);
            if (numberPart.matches("\\d+")) {
                return 1; // Success
            }
        }
        return 0; // Failure
    }

    public String createMessageHash() {
        String firstTwo = messageID.substring(0, 2);
        
        // Extract first and last words from message
        String[] words = message.split(" ");
        String firstWord = words.length > 0 ? words[0].toUpperCase() : "";
        String lastWord = words.length > 1 ? words[words.length - 1].toUpperCase() : firstWord;
        
        return firstTwo + ":" + messageCount + ":" + firstWord + lastWord;
    }

    public String sentMessage(int choice) {
        switch (choice) {
            case 1: // Send Message
                totalMessagesSent++;
                this.status = "sent";
                // Store in JSON
                JSONHandler.storeMessage(this);
                return "Message successfully sent.";
            case 2: // Disregard Message
                this.status = "discarded";
                return "Press 0 to delete message.";
            case 3: // Store Message
                this.status = "stored";
                // Store in JSON
                JSONHandler.storeMessage(this);
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    public String printMessages() {
        return "MessageID: " + messageID + 
               "\nMessage Hash: " + messageHash + 
               "\nRecipient: " + recipient + 
               "\nMessage: " + message +
               "\nStatus: " + status;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // Getters and Setters
    public String getMessageID() { return messageID; }
    public int getMessageCount() { return messageCount; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getMessage() { return message; }
    public void setMessage(String message) { 
        this.message = message;
        this.messageHash = createMessageHash();
    }
    public String getMessageHash() { return messageHash; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    AbstractStringBuilder checkMessageCount() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private static class AbstractStringBuilder {

        public AbstractStringBuilder() {
        }
    }
}