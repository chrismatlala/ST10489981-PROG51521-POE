/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userauthenticationsystem;

import java.util.Scanner;

/**
 *
 * @author chris
 */
public class RegisterUser {

    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String cellNumber;
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

        return "Registration successful!";
    }

    private boolean checkUsernameFormat(String username) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private boolean checkPasswordComplexity(String password) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private boolean checkCellPhoneNumberFormat(String cellNumber) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
}
