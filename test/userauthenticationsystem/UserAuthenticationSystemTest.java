package userauthenticationsystem;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import userauthenticationsystem.UserAuthenticationSystem;

public class UserAuthenticationSystemTest {
    private UserAuthenticationSystem authSystem = new UserAuthenticationSystem();

    @Test
    public void CheckUsernameFormat() {
        // Correct format of the username
        assertTrue(authSystem.validateUsernameFormat("kyl_1"));
        assertTrue(authSystem.validateUsernameFormat("a_b"));
        
        // Incorrect format of the username
        assertFalse(authSystem.validateUsernameFormat("kyle!!!!!!!"));
        assertFalse(authSystem.validateUsernameFormat("kyle"));
        assertFalse(authSystem.validateUsernameFormat("ky le"));
    }

    @Test
    public void CheckPasswordComplexity() {
        // Correct passwords
        assertTrue(authSystem.validatePasswordComplexity("Ch&&sec@ke99!"));
        assertTrue(authSystem.validatePasswordComplexity("A1@bcdefg"));
        
        // Incorrect passwords
        assertFalse(authSystem.validatePasswordComplexity("password"));  // No caps, numbers, special chars
        assertFalse(authSystem.validatePasswordComplexity("Password"));  // No numbers or special chars
        assertFalse(authSystem.validatePasswordComplexity("P@ssw0r"));   // Too short
        assertFalse(authSystem.validatePasswordComplexity("password123")); // No caps or special chars
    }

    @Test
    public void checkPhoneNumberFormat() {
        // Correct formats of the phone number
        assertTrue(authSystem.validatePhoneNumberFormat("+27831234567"));
        assertTrue(authSystem.validatePhoneNumberFormat("+1234567890"));
        
        // Incorrect formats of the phone number
        assertFalse(authSystem.validatePhoneNumberFormat("08966553"));     // No international code
        assertFalse(authSystem.validatePhoneNumberFormat("+123456"));      // Too short
        assertFalse(authSystem.validatePhoneNumberFormat("27831234567"));  // Missing +
        assertFalse(authSystem.validatePhoneNumberFormat("+abc1234567"));  // Contains letters
    }

    @Test
    public void testAuthenticateUser() {
        // Set up test data using reflection since fields are private
        setPrivateField(authSystem, "userName", "test_1");
        setPrivateField(authSystem, "password", "P@ssw0rd123");
        
        // Successful login
        assertTrue(authSystem.authenticateUser("test_1", "P@ssw0rd123"));
        
        // Failed logins
        assertFalse(authSystem.authenticateUser("wrong", "P@ssw0rd123"));
        assertFalse(authSystem.authenticateUser("test_1", "wrong"));
        assertFalse(authSystem.authenticateUser("wrong", "wrong"));
    }

    @Test
    public void testGetLoginStatusMessage() {
        setPrivateField(authSystem, "firstName", "Kyle");
        setPrivateField(authSystem, "lastName", "");
        
        // Successful login message
        assertEquals("Welcome Kyle, it is great to see you again.", 
                    authSystem.getLoginStatusMessage(true));
        
        // Failed login message
        assertEquals("Username or password incorrect, please try again.", 
                    authSystem.getLoginStatusMessage(false));
    }

    @Test
    public void testRegisterNewUser() {
        // Simulate user input
        String simulatedInput = "Kyle\ntest_1\nP@ssw0rd123!\n+27831234567\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        
        String result = authSystem.registerNewUser(testScanner);
        
        assertEquals("Registration successful!", result);
        
        // Verify the fields were set correctly
        assertEquals("test_1", getPrivateField(authSystem, "userName"));
        assertEquals("P@ssw0rd123!", getPrivateField(authSystem, "password"));
        assertEquals("+27831234567", getPrivateField(authSystem, "cellNumber"));
        assertEquals("John", getPrivateField(authSystem, "firstName"));
        assertEquals("", getPrivateField(authSystem, "lastName"));
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("Error setting field: " + fieldName, e);
        }
    }

    // Helper method to get private fields using reflection
    private Object getPrivateField(Object object, String fieldName) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException("Error getting field: " + fieldName, e);
        }
    }
}