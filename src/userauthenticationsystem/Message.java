package userauthenticationsystem;


import java.util.*;
import java.util.regex.Pattern;

class Message {
    private String messageId;
    private String messageContent;
    private String recipient;
    private boolean isSent;
    private boolean isStored;
    private Date timestamp;
    private static int messageCounter = 1;

    public Message(String messageContent, String recipient) {
        this.messageId = generateMessageId();
        this.messageContent = messageContent;
        this.recipient = recipient;
        this.isSent = false;
        this.isStored = false;
        this.timestamp = new Date();
    }

    private String generateMessageId() {
        String id = String.format("%05d", messageCounter++);
        return id;
    }

    public boolean checkMessageLength() {
        return messageContent.length() <= 250;
    }

    public boolean checkRecipientFormat() {
        if (recipient == null || recipient.trim().isEmpty()) {
            return false;
        }
        String pattern = "^\\+\\d{1,3}\\d{7,10}$";
        return Pattern.matches(pattern, recipient);
    }

    public String checkMessageHash() {
        String[] words = messageContent.split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        
        String hash = messageId.substring(0, 2) + ":" + 
                     String.format("%02d", messageCounter) + ":" +
                     firstWord.toUpperCase() + ":" + 
                     lastWord.toUpperCase();
        return hash;
    }

    public String sendMessageAction(Scanner inputScanner) {
        System.out.println("\nChoose an option:");
        System.out.println("1. Send Message");
        System.out.println("2. Discard Message");
        System.out.println("3. Store Message to send later");
        System.out.print("Select option (1-3): ");

        String choice = inputScanner.nextLine().trim();
        switch (choice) {
            case "1":
                return "send";
            case "2":
                return "discard";
            case "3":
                return "store";
            default:
                System.out.println("Invalid option. Defaulting to store.");
                return "store";
        }
    }

    public String returnTotalMessages() {
        return "Total messages: " + messageCounter;
    }

    public void storeMessage() {
        this.isStored = true;
        // In a real application, this would save to a JSON file
        System.out.println("Message stored (JSON functionality would be implemented here)");
    }

    public void markAsSent() {
        this.isSent = true;
    }

    public boolean isSent() {
        return isSent;
    }

    // Getters
    public String getMessageId() { return messageId; }
    public String getMessageContent() { return messageContent; }
    public String getRecipient() { return recipient; }
    public Date getTimestamp() { return timestamp; }
}
    

