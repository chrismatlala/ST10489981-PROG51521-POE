package userauthenticationsystem;

import java.util.Scanner;
import java.util.regex.Pattern;

public class UserAuthenticationSystem {
    private String userName;
    private String password;
    private String cellNumber;
    private String firstName;
    private String lastName;


    // Constructor
    public UserAuthenticationSystem(String firstName, String lastName, String userName, String password, String cellNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.cellNumber = cellNumber;
    }

    // Main method to run the program
    public static void main(String[] args) {
        UserAuthenticationSystem authSystem = new UserAuthenticationSystem();  // uses default constructor
        Scanner inputScanner = new Scanner(System.in);

        System.out.println("=== User Registration ===");
        String registrationResult = authSystem.registerNewUser(inputScanner);
        System.out.println(registrationResult);

        if (registrationResult.toLowerCase().contains("successful")) {
            System.out.println("\n=== User Login ===");
            System.out.print("Enter username: ");
            String enteredUsername = inputScanner.nextLine();
            System.out.print("Enter password: ");
            String enteredPassword = inputScanner.nextLine();

            boolean loginSuccess = authSystem.authenticateUser(enteredUsername, enteredPassword);
            System.out.println(authSystem.getLoginStatusMessage(loginSuccess));
        }

        inputScanner.close();
    }

    // Check if username meets requirements
    public boolean validateUsernameFormat(String username) {
        return username.length() <= 5 && username.contains("_");
    }

    // Check if password meets complexity requirements
    public boolean validatePasswordComplexity(String password) {
        if (password.length() < 8) return false;
        if (!Pattern.compile("[A-Z]").matcher(password).find()) return false;
        if (!Pattern.compile("[0-9]").matcher(password).find()) return false;
        return Pattern.compile("[^A-Za-z0-9]").matcher(password).find();
    }

    // Check if cell phone number is correctly formatted
    public boolean validatePhoneNumberFormat(String cellNumber) {
        String pattern = "^\\+\\d{1,3}\\d{7,10}$";
        return Pattern.matches(pattern, cellNumber);
    }

    // Handle user registration process
    public String registerNewUser(Scanner inputScanner) {
        System.out.print("Enter your first name: ");
        this.firstName = inputScanner.nextLine();

        System.out.print("Enter your last name: ");
        this.lastName = inputScanner.nextLine();

        System.out.print("Enter username (must contain _ and be ≤5 characters): ");
        String username = inputScanner.nextLine();
        if (!validateUsernameFormat(username)) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        this.userName = username;

        System.out.print("Enter password (≥8 chars, with capital, number, special char): ");
        String password = inputScanner.nextLine();
        if (!validatePasswordComplexity(password)) {
            return "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        this.password = password;

        System.out.print("Enter cell phone number (with international code, e.g., +27831234567): ");
        String cellNumber = inputScanner.nextLine();
        if (!validatePhoneNumberFormat(cellNumber)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        this.cellNumber = cellNumber;

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
}
