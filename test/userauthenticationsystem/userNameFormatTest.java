/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package userauthenticationsystem;

import userauthenticationsystem.userNameFormat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 *
 * @author chris
 */
public class userNameFormatTest {
    userNameFormat username = new userNameFormat();
    
    public userNameFormatTest() {
    }

    @Test
    public void testcheckUsernameFormat() {
        
        boolean actual = username.checkuserNameFormat();
        
    }
    
}
