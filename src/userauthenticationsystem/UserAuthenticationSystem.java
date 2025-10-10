package userauthenticationsystem;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

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
    } // Fixed: Added missing closing brace

    public boolean loginUser(String enteredUsername, String enteredPassword) {
        boolean success = enteredUsername.equals(this.userName) && enteredPassword.equals(this.password);
        this.loggedIn = success;
        return success;
    }

    public void logout() {
        this.loggedIn = false;
        System.out.println("You have been logged out successfully.");
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setMessageLimit(Scanner inputScanner) {
        System.out.println("\n=== Message Setup ===");
        System.out.print("How many messages do you wish to enter? ");

        try {
            int limit = Integer.parseInt(inputScanner.nextLine().trim());
            if (limit <= 0) {
                System.out.println("Message limit must be a positive number. Setting default to 5.");
                this.messageLimit = 5;
            } else {
                this.messageLimit = limit;
                System.out.println("Message limit set to: " + limit);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number. Setting default to 5.");
            this.messageLimit = 5;
        }
    }

    private int getSentMessageCount() {
        return (int) messages.stream()
                .filter(msg -> msg.isSent())
                .count();
    }

    // Handle user registration process
    public String registerNewUser(Scanner inputScanner) {
        System.out.print("Enter your first name: ");
        this.firstName = inputScanner.nextLine();

        System.out.print("Enter your last name: ");
        this.lastName = inputScanner.nextLine();

        System.out.print("Enter username (must contain _ and be ≤5 characters): ");
        String username = inputScanner.nextLine();
        if (!checkUsernameFormat(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        this.userName = username;

        System.out.print("Enter password (≥8 chars, with capital, number, special char): ");
        String password = inputScanner.nextLine();
        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        this.password = password;

        System.out.print("Enter cell phone number (with international code, e.g., +27831234567): ");
        String cellNumber = inputScanner.nextLine();
        if (!checkCellPhoneNumberFormat(cellNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        this.cellNumber = cellNumber;

        // Add the registered user to the list
        User newUser = new User(firstName, lastName, userName, password, cellNumber);
        registeredUsers.add(newUser);

        return "Registration successful!";
    }

    // Verify login credentials
    public boolean authenticateUser(String enteredUsername, String enteredPassword) {
        return enteredUsername.equals(this.userName) && enteredPassword.equals(this.password);
    }

    // Return appropriate login status message
    public String getLoginStatusMessage(boolean isSuccessful) {
        if (isSuccessful) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    public void startApplication(Scanner inputScanner) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           WELCOME TO QUICKCHAT");
        System.out.println("=".repeat(50));

        boolean running = true;

        while (running) {
            if (!loggedIn) {
                System.out.println("\n=== Main Menu ===");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Quit");
                System.out.print("Choose an option: ");

                String choice = inputScanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        String registerResult = registerNewUser(inputScanner);
                        System.out.println(registerResult);
                        break;
                    case "2":
                        System.out.print("Enter username: ");
                        String loginUsername = inputScanner.nextLine();
                        System.out.print("Enter password: ");
                        String loginPassword = inputScanner.nextLine();

                        boolean loginSuccess = loginUser(loginUsername, loginPassword);
                        System.out.println(getLoginStatusMessage(loginSuccess));
                        break;
                    case "3":
                        System.out.println("\nThank you for using QuickChat. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } else {
                if (messageLimit <= 0) {
                    setMessageLimit(inputScanner);
                }

                boolean inChatMenu = true;

                while (inChatMenu && loggedIn) {
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("    Welcome to QuickChat, " + firstName + " " + lastName + "!");
                    System.out.println("Message Limit: " + messageLimit + " | Sent: " + getSentMessageCount() + " | Remaining: " + (messageLimit - getSentMessageCount()));
                    System.out.println("=".repeat(50));
                    System.out.println("1. Send Messages");
                    System.out.println("2. Show Recently Sent Messages");
                    System.out.println("3. Quit");
                    System.out.print("Choose an option: ");

                    String choice = inputScanner.nextLine().trim();

                    switch (choice) {
                        case "1":
                            sendMessageWorkflow(inputScanner);
                            break;
                        case "2":
                            showRecentMessages();
                            break;
                        case "3":
                            System.out.println("\nThank you for using QuickChat. Goodbye!");
                            running = false;
                            inChatMenu = false;
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                    }
                }
            }
        }
    }

    private void sendMessageWorkflow(Scanner inputScanner) {
        if (getSentMessageCount() >= messageLimit) {
            System.out.println("You have reached your message limit of " + messageLimit + " messages.");
            return;
        }

        System.out.println("\nAvailable users:");
        for (int i = 0; i < registeredUsers.size(); i++) {
            User user = registeredUsers.get(i);
            if (!user.getUserName().equals(this.userName)) {
                System.out.printf("%d. %s (%s)%n", i + 1, user.getFullName(), user.getUserName());
            }
        }

        System.out.print("Enter recipient username: ");
        String recipient = inputScanner.nextLine().trim();

        User recipientUser = findUserByUsername(recipient);
        if (recipientUser == null) {
            System.out.println("Error: User '" + recipient + "' not found.");
            return;
        }

        System.out.print("Enter your message (max 250 characters): ");
        String messageContent = inputScanner.nextLine().trim();

        // Create message object
        Message newMessage = new Message(this.userName, recipientUser.getUserName(), messageContent);

        // Check message length
        if (messageContent.length() > 250) {
            System.out.println("Please enter a message of less than 250 characters.");
            return;
        }

        // Check recipient number format
        if (!checkCellPhoneNumberFormat(recipientUser.getCellNumber())) {
            System.out.println("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
            return;
        }

        // Display message hash
        System.out.println("Message Hash: " + newMessage.checkMessageHash());

        // Ask for action
        System.out.print("Do you want to (send/discard/store) this message? ");
        String action = inputScanner.nextLine().trim().toLowerCase();

        switch (action) {
            case "send":
                newMessage.markAsSent();
                messages.add(newMessage);
                totalMessagesSent++;
                System.out.println("Message successfully sent.");
                displayMessageDetails(newMessage);
                break;
            case "discard":
                System.out.println("Message discarded.");
                break;
            case "store":
                messages.add(newMessage);
                System.out.println("Message successfully stored.");
                break;
            default:
                System.out.println("Invalid action. Message discarded.");
        }

        System.out.println("Total messages sent: " + totalMessagesSent);
    }

    private void displayMessageDetails(Message message) {
        System.out.println("\n=== Message Details ===");
        System.out.println("Sender: " + message.getSender());
        System.out.println("Recipient: " + message.getReceiver());
        System.out.println("Message: " + message.getContent());
        System.out.println("Timestamp: " + message.getTimestamp());
    }

    private void showRecentMessages() {
        System.out.println("\n=== Recently Sent Messages ===");
        if (messages.isEmpty()) {
            System.out.println("No messages sent yet.");
        } else {
            for (Message msg : messages) {
                System.out.println(msg);
            }
        }
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
        Scanner inputScanner = new Scanner(System.in);
        authSystem.startApplication(inputScanner);
        inputScanner.close();
    }

    // Inner Message class
    private static class Message {
        private String sender;
        private String receiver;
        private String content;
        private String timestamp;
        private boolean sent;

        public Message(String sender, String receiver, String content) {
            this.sender = sender;
            this.receiver = receiver;
            this.content = content;
            this.timestamp = java.time.LocalDateTime.now().toString();
            this.sent = false;
        }

        public String getSender() { return sender; }
        public String getReceiver() { return receiver; }
        public String getContent() { return content; }
        public String getTimestamp() { return timestamp; }
        public boolean isSent() { return sent; }

        public void markAsSent() { this.sent = true; }

        public String checkMessageHash() {
            return Integer.toHexString((sender + receiver + content + timestamp).hashCode());
        }

        @Override
        public String toString() {
            return String.format("[%s] From: %s → %s: %s",
                    timestamp, sender, receiver, content);
        }
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

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}