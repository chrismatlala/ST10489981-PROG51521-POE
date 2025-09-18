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
public class CellPhoneNumber {
    public boolean checkCellPhoneNumberFormat(String cellNumber) {
        String pattern = "^\\+\\d{1,3}\\d{7,10}$";
        return Pattern.matches(pattern, cellNumber);
}
}
