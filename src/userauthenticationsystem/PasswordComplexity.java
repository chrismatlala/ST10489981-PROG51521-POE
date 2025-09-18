/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package userauthenticationsystem;

import java.util.regex.Pattern;

/**
 *
 * @author chris
 */
public class PasswordComplexity {
    
    
    
    public boolean checkPasswordComplexity(){
        CharSequence password = null;
        if (password.length() < 8) return false;
        if (!Pattern.compile("[A-Z]").matcher(password).find()) return false;
        if (!Pattern.compile("[0-9]").matcher(password).find()) return false;
        return Pattern.compile("[^A-Za-z0-9]").matcher(password).find();
        
        
    }
    
}
