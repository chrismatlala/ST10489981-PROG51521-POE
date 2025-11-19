package quickchatapp;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Login {
    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;
    
    // Constructor
    public Login() {
        this.username = "";
        this.password = "";
        this.cellPhoneNumber = "";
        this.firstName = "";
        this.lastName = "";
    }
    
    public Login(String firstName, String lastName, String username, String password) {
        if (firstName == null || lastName == null || username == null || password == null) {
            throw new IllegalArgumentException("Constructor arguments cannot be null.");
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }
    
    // First name must not be null, empty, or contain spaces
    public boolean checkfirstName() {
        if (firstName == null) {
            return false;
        }
        if (firstName.isEmpty()) {
            return false;
        }
        if (firstName.contains(" ")) {
            return false;
        }
        return true;
    }

    // Last name must not be null, empty, or contain spaces
    public boolean checklastName() {
        if (lastName == null) {
            return false;
        }
        if (lastName.isEmpty()) {
            return false;
        }
        if (lastName.contains(" ")) {
            return false;
        }
        return true;
    }
    
    // Check if username meets requirements
    public boolean checkUserName() {
        if (username == null) return false;
        return username.contains("_") && username.length() <= 5;
    }
    
    // Check if password meets complexity requirements
    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecialChar = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }
        }
        
        return hasCapital && hasNumber && hasSpecialChar;
    }
    
    // Check if cell phone number is correctly formatted
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) return false;
        
        // Regular expression to match South African cell phone numbers with international code
        String regex = "^\\+27[0-9]{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cellPhoneNumber);
        
        return matcher.matches();
    }
    
    // Register a new user
    public String registerUser(String firstName, String lastName, String username, 
                              String password, String cellPhoneNumber) {
        // Prevent null or empty inputs
        if (firstName == null || firstName.trim().isEmpty()) {
            return "First name cannot be empty.";
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            return "Last name cannot be empty.";
        }
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (cellPhoneNumber == null || cellPhoneNumber.trim().isEmpty()) {
            return "Cell phone number cannot be empty.";
        }

        // Set fields
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        
        boolean isUsernameValid = checkUserName();
        boolean isPasswordValid = checkPasswordComplexity();
        boolean isCellPhoneValid = checkCellPhoneNumber();
        
        if (!isUsernameValid) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        
        if (!isPasswordValid) {
            return "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        
        if (!isCellPhoneValid) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }
        
        return "User registered successfully.";
    }
    
    // Login user
    public boolean loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return this.username.equals(username) && this.password.equals(password);
    }
    
    // Return login status message
    public String returnLoginStatus(String username, String password) {
        if (loginUser(username, password)) {
            return "Welcome " + firstName + "," + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
    
    // Getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }
    
    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}