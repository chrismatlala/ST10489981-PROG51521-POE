package userauthenticationsystem;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class UserAuthenticationSystem {
    private String userName;
    private String password;
    private String cellNumber;
    private String firstName;
    private String lastName;
    private boolean loggedIn;
    private List<Message> messages;
    private List<User> registeredUsers;
    private int messageLimit;
    private int totalMessagesSent;

    // Default Constructor
    public UserAuthenticationSystem() {
        this.firstName = "";
        this.lastName = "";
        this.cellNumber = "";
        this.userName = "";
        this.password = "";
        this.loggedIn = false;
        this.messages = new ArrayList<>();
        this.registeredUsers = new ArrayList<>();
        this.messageLimit = 0;
        this.totalMessagesSent = 0;
        
        // Add some sample users for testing
        initializeSampleUsers();
    }

    // Parameterized Constructor
    public UserAuthenticationSystem(String firstName, String lastName, String userName, String password, String cellNumber) {
        if (firstName == null || lastName == null || userName == null || password == null) {
            throw new IllegalArgumentException("Constructor arguments cannot be null.");
        }

        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.cellNumber = cellNumber;
        this.loggedIn = false;
        this.messages = new ArrayList<>();
        this.registeredUsers = new ArrayList<>();
        this.messageLimit = 0;
        this.totalMessagesSent = 0;
        
        initializeSampleUsers();
    }

    private void initializeSampleUsers() {
        // Add some sample users for testing
        registeredUsers.add(new User("Mike", "Johnson", "mike_j", "Password123!", "+27726030002"));
        registeredUsers.add(new User("Sarah", "Williams", "sarah", "SecurePass1!", "+27731234567"));
        registeredUsers.add(new User("David", "Brown", "dave_b", "MyPass123!", "+27829876543"));
        registeredUsers.add(new User("Emily", "Davis", "em_d", "Emily123!", "+27655321478"));
    }

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        boolean success = enteredUsername.equals(this.userName) && enteredPassword.equals(this.password);
        this.loggedIn = success;
        return success;
    }

    public void logout() {
        this.loggedIn = false;
        JOptionPane.showMessageDialog(null, "You have been logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setMessageLimit() {
        String input = JOptionPane.showInputDialog(null,
            "How many messages do you wish to enter?",
            "Message Setup",
            JOptionPane.QUESTION_MESSAGE);

        if (input == null) {
            // User cancelled, set default
            this.messageLimit = 5;
            return;
        }

        try {
            int limit = Integer.parseInt(input.trim());
            if (limit <= 0) {
                JOptionPane.showMessageDialog(null, 
                    "Message limit must be a positive number. Setting default to 5.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
                this.messageLimit = 5;
            } else {
                this.messageLimit = limit;
                JOptionPane.showMessageDialog(null, 
                    "Message limit set to: " + limit,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, 
                "Invalid input. Please enter a valid number. Setting default to 5.",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
            this.messageLimit = 5;
        }
    }

    private int getSentMessageCount() {
        return (int) messages.stream()
                .filter(msg -> msg.isSent())
                .count();
    }

    // Handle user registration process using JOptionPane
    public String registerNewUser() {
        // First Name
        String firstName = JOptionPane.showInputDialog(null,
            "Enter your first name:",
            "User Registration - First Name",
            JOptionPane.QUESTION_MESSAGE);
        
        if (firstName == null || firstName.trim().isEmpty()) {
            return "Registration cancelled. First name is required.";
        }
        this.firstName = firstName.trim();

        // Last Name
        String lastName = JOptionPane.showInputDialog(null,
            "Enter your last name:",
            "User Registration - Last Name",
            JOptionPane.QUESTION_MESSAGE);
        
        if (lastName == null || lastName.trim().isEmpty()) {
            return "Registration cancelled. Last name is required.";
        }
        this.lastName = lastName.trim();

        // Username
        String username = JOptionPane.showInputDialog(null,
            "Enter username (must contain _ and be ≤5 characters):",
            "User Registration - Username",
            JOptionPane.QUESTION_MESSAGE);
        
        if (username == null || username.trim().isEmpty()) {
            return "Registration cancelled. Username is required.";
        }
        
        if (!checkUsernameFormat(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        this.userName = username.trim();

        // Password
        String password = JOptionPane.showInputDialog(null,
            "Enter password (≥8 chars, with capital, number, special char):",
            "User Registration - Password",
            JOptionPane.QUESTION_MESSAGE);
        
        if (password == null || password.trim().isEmpty()) {
            return "Registration cancelled. Password is required.";
        }
        
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        this.password = password.trim();

        // Cell Phone Number
        String cellNumber = JOptionPane.showInputDialog(null,
            "Enter cell phone number (with international code, e.g., +27831234567):",
            "User Registration - Cell Number",
            JOptionPane.QUESTION_MESSAGE);
        
        if (cellNumber == null || cellNumber.trim().isEmpty()) {
            return "Registration cancelled. Cell phone number is required.";
        }
        
        if (!checkCellPhoneNumberFormat(cellNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        this.cellNumber = cellNumber.trim();

        // Add the registered user to the list
        User newUser = new User(firstName, lastName, userName, password, cellNumber);
        registeredUsers.add(newUser);

        return "Registration successful! Welcome " + firstName + " " + lastName + "!";
    }

    // Verify login credentials
    public boolean authenticateUser(String enteredUsername, String enteredPassword) {
        return enteredUsername.equals(this.userName) && enteredPassword.equals(this.password);
    }

    // Return appropriate login status message
    public String getLoginStatusMessage(boolean isSuccessful) {
        if (isSuccessful) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    public void startApplication() {
        JOptionPane.showMessageDialog(null,
            "WELCOME TO QUICKCHAT\n" +
            "===================\n\n" +
            "Your secure messaging platform",
            "QuickChat Welcome",
            JOptionPane.INFORMATION_MESSAGE);

        boolean running = true;

        while (running) {
            if (!loggedIn) {
                // Main Menu
                String[] mainMenuOptions = {"Register", "Login", "Quit"};
                int mainChoice = JOptionPane.showOptionDialog(null,
                    "=== MAIN MENU ===\n\n" +
                    "Please choose an option:",
                    "QuickChat Main Menu",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    mainMenuOptions,
                    mainMenuOptions[0]);

                switch (mainChoice) {
                    case 0: // Register
                        String registerResult = registerNewUser();
                        JOptionPane.showMessageDialog(null, registerResult, 
                            "Registration Result", 
                            registerResult.contains("successful") ? 
                                JOptionPane.INFORMATION_MESSAGE : 
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    case 1: // Login
                        String loginUsername = JOptionPane.showInputDialog(null,
                            "Enter your username:",
                            "Login",
                            JOptionPane.QUESTION_MESSAGE);
                        
                        if (loginUsername == null) {
                            break; // User cancelled
                        }
                        
                        String loginPassword = JOptionPane.showInputDialog(null,
                            "Enter your password:",
                            "Login",
                            JOptionPane.QUESTION_MESSAGE);
                        
                        if (loginPassword == null) {
                            break; // User cancelled
                        }
                        
                        boolean loginSuccess = loginUser(loginUsername.trim(), loginPassword.trim());
                        String loginMessage = getLoginStatusMessage(loginSuccess);
                        JOptionPane.showMessageDialog(null, loginMessage,
                            "Login Result",
                            loginSuccess ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                        break;
                    case 2: // Quit
                        int confirmQuit = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to quit QuickChat?",
                            "Confirm Quit",
                            JOptionPane.YES_NO_OPTION);
                        
                        if (confirmQuit == JOptionPane.YES_OPTION) {
                            JOptionPane.showMessageDialog(null,
                                "Thank you for using QuickChat. Goodbye!",
                                "Goodbye",
                                JOptionPane.INFORMATION_MESSAGE);
                            running = false;
                        }
                        break;
                    default: // Closed dialog
                        running = false;
                        break;
                }
            } else {
                // User is logged in
                if (messageLimit <= 0) {
                    setMessageLimit();
                }

                boolean inChatMenu = true;

                while (inChatMenu && loggedIn) {
                    String[] chatMenuOptions = {"Send Messages", "Show Recently Sent Messages", "Logout", "Quit"};
                    int chatChoice = JOptionPane.showOptionDialog(null,
                        "Welcome to QuickChat, " + firstName + " " + lastName + "!\n\n" +
                        "Message Limit: " + messageLimit + 
                        " | Sent: " + getSentMessageCount() + 
                        " | Remaining: " + (messageLimit - getSentMessageCount()) + "\n\n" +
                        "Please choose an option:",
                        "QuickChat Dashboard",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        chatMenuOptions,
                        chatMenuOptions[0]);

                    switch (chatChoice) {
                        case 0: // Send Messages
                            sendMessageWorkflow();
                            break;
                        case 1: // Show Recently Sent Messages
                            showRecentMessages();
                            break;
                        case 2: // Logout
                            logout();
                            inChatMenu = false;
                            break;
                        case 3: // Quit
                            int confirmQuit = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to quit QuickChat?",
                                "Confirm Quit",
                                JOptionPane.YES_NO_OPTION);
                            
                            if (confirmQuit == JOptionPane.YES_OPTION) {
                                JOptionPane.showMessageDialog(null,
                                    "Thank you for using QuickChat. Goodbye!",
                                    "Goodbye",
                                    JOptionPane.INFORMATION_MESSAGE);
                                running = false;
                                inChatMenu = false;
                            }
                            break;
                        default: // Closed dialog
                            inChatMenu = false;
                            running = false;
                            break;
                    }
                }
            }
        }
    }

    private void sendMessageWorkflow() {
        if (getSentMessageCount() >= messageLimit) {
            JOptionPane.showMessageDialog(null,
                "You have reached your message limit of " + messageLimit + " messages.",
                "Message Limit Reached",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show available users
        StringBuilder usersList = new StringBuilder();
        usersList.append("Available Users:\n\n");
        for (int i = 0; i < registeredUsers.size(); i++) {
            User user = registeredUsers.get(i);
            if (!user.getUserName().equals(this.userName)) {
                usersList.append((i + 1)).append(". ").append(user.getFullName())
                         .append(" (").append(user.getUserName()).append(")\n");
            }
        }

        String recipient = JOptionPane.showInputDialog(null,
            usersList.toString() + "\nEnter recipient username:",
            "Select Recipient",
            JOptionPane.QUESTION_MESSAGE);

        if (recipient == null || recipient.trim().isEmpty()) {
            return; // User cancelled
        }

        User recipientUser = findUserByUsername(recipient.trim());
        if (recipientUser == null) {
            JOptionPane.showMessageDialog(null,
                "Error: User '" + recipient + "' not found.",
                "User Not Found",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String messageContent = JOptionPane.showInputDialog(null,
            "Enter your message for " + recipientUser.getFullName() + " (max 250 characters):",
            "Compose Message",
            JOptionPane.QUESTION_MESSAGE);

        if (messageContent == null || messageContent.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Message cannot be empty.",
                "Empty Message",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create message object using the standalone Message class
        Message newMessage = new Message(recipientUser.getCellNumber(), messageContent.trim());

        // Check message length
        if (!newMessage.checkMessageLength()) {
            return; // Error message already shown by checkMessageLength()
        }

        // Check recipient number format
        if (!newMessage.checkRecipientFormat()) {
            return; // Error message already shown by checkRecipientFormat()
        }

        // Show message details and get user action
        String[] messageOptions = {"Send", "Discard", "Store"};
        int messageAction = JOptionPane.showOptionDialog(null,
            "Message Details:\n\n" +
            "Recipient: " + recipientUser.getFullName() + " (" + recipientUser.getCellNumber() + ")\n" +
            "Message: " + messageContent + "\n" +
            "Message Hash: " + newMessage.checkMessageHash() + "\n\n" +
            "What would you like to do with this message?",
            "Message Action",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            messageOptions,
            messageOptions[0]);

        switch (messageAction) {
            case 0: // Send
                String sendResult = newMessage.setMessage("send");
                if (sendResult.contains("successfully sent")) {
                    messages.add(newMessage);
                    totalMessagesSent++;
                    JOptionPane.showMessageDialog(null, sendResult, "Message Sent", JOptionPane.INFORMATION_MESSAGE);
                    displayMessageDetails(newMessage, recipientUser.getFullName());
                } else {
                    JOptionPane.showMessageDialog(null, sendResult, "Send Failed", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 1: // Discard
                int discardConfirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to discard this message?",
                    "Confirm Discard",
                    JOptionPane.YES_NO_OPTION);
                
                if (discardConfirm == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Message discarded.", "Discarded", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case 2: // Store
                newMessage.storeMessage();
                messages.add(newMessage);
                JOptionPane.showMessageDialog(null, "Message successfully stored.", "Stored", JOptionPane.INFORMATION_MESSAGE);
                break;
            default: // Cancelled
                JOptionPane.showMessageDialog(null, "Message action cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                break;
        }

        JOptionPane.showMessageDialog(null,
            "Total messages sent: " + totalMessagesSent + "\n" +
            "Messages remaining: " + (messageLimit - getSentMessageCount()),
            "Message Statistics",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayMessageDetails(Message message, String recipientName) {
        JOptionPane.showMessageDialog(null,
            "=== MESSAGE DETAILS ===\n\n" +
            "Message ID: " + message.getMessageID() + "\n" +
            "Message Hash: " + message.checkMessageHash() + "\n" +
            "Recipient: " + recipientName + "\n" +
            "Message: " + message.getMessageText() + "\n" +
            "Status: Sent Successfully",
            "Message Details",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRecentMessages() {
        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "No messages sent yet.",
                "Recent Messages",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder recentMessages = new StringBuilder();
        recentMessages.append("=== RECENTLY SENT MESSAGES ===\n\n");
        
        int count = 0;
        for (Message msg : messages) {
            if (msg.isSent()) {
                count++;
                recentMessages.append("Message ").append(count).append(":\n");
                recentMessages.append("  ID: ").append(msg.getMessageID()).append("\n");
                recentMessages.append("  Hash: ").append(msg.checkMessageHash()).append("\n");
                recentMessages.append("  Content: ").append(msg.getMessageText()).append("\n");
                recentMessages.append("------------------------\n");
            }
        }

        if (count == 0) {
            recentMessages.append("No sent messages found.");
        }

        JOptionPane.showMessageDialog(null,
            recentMessages.toString(),
            "Recent Messages",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private User findUserByUsername(String username) {
        for (User user : registeredUsers) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    // Check if username meets requirements
    public boolean checkUsernameFormat(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.length() <= 5 && username.contains("_");
    }

    // Check if password meets complexity requirements
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecialChar = Pattern.compile("[^A-Za-z0-9]").matcher(password).find();

        return hasUpperCase && hasDigit && hasSpecialChar;
    }

    // Check if cell phone number is correctly formatted
    public boolean checkCellPhoneNumberFormat(String cellNumber) {
        if (cellNumber == null || cellNumber.trim().isEmpty()) {
            return false;
        }
        String pattern = "^\\+\\d{1,3}\\d{7,10}$";
        return Pattern.matches(pattern, cellNumber);
    }

    // Main method to run the program
    public static void main(String[] args) {
        UserAuthenticationSystem authSystem = new UserAuthenticationSystem();
        authSystem.startApplication();
    }

    // Inner User class
    private static class User {
        private String firstName;
        private String lastName;
        private String userName;
        private String password;
        private String cellNumber;

        public User(String firstName, String lastName, String userName, String password, String cellNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.userName = userName;
            this.password = password;
            this.cellNumber = cellNumber;
        }

        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getUserName() { return userName; }
        public String getCellNumber() { return cellNumber; }
        public String getPassword() { return password; }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}