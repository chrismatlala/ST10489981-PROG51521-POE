
import org.junit.jupiter.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class LoginSystemPOETest {
    private LoginSystemPOE loginSystem = new LoginSystemPOE();

    @Test
    public void testCheckUsername() {
        // Correct format
        assertTrue(loginSystem.checkUsername("kyl_1"));
        assertTrue(loginSystem.checkUsername("a_b"));
        
        // Incorrect format
        assertFalse(loginSystem.checkUsername("kyle!!!!!!!"));
        assertFalse(loginSystem.checkUsername("kyle"));
        assertFalse(loginSystem.checkUsername("ky le"));
    }

    @Test
    public void testCheckPasswordComplexity() {
        // Correct passwords
        assertTrue(loginSystem.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertTrue(loginSystem.checkPasswordComplexity("A1@bcdefg"));
        
        // Incorrect passwords
        assertFalse(loginSystem.checkPasswordComplexity("password"));  // No caps, numbers, special chars
        assertFalse(loginSystem.checkPasswordComplexity("Password"));  // No numbers or special chars
        assertFalse(loginSystem.checkPasswordComplexity("P@ssw0r"));   // Too short
        assertFalse(loginSystem.checkPasswordComplexity("password123")); // No caps or special chars
    }

    @Test
    public void testCheckCellPhoneNumber() {
        // Correct formats
        assertTrue(loginSystem.checkCellPhoneNumber("+27831234567"));
        assertTrue(loginSystem.checkCellPhoneNumber("+1234567890"));
        
        // Incorrect formats
        assertFalse(loginSystem.checkCellPhoneNumber("08966553"));     // No international code
        assertFalse(loginSystem.checkCellPhoneNumber("+123456"));      // Too short
        assertFalse(loginSystem.checkCellPhoneNumber("27831234567"));  // Missing +
        assertFalse(loginSystem.checkCellPhoneNumber("+abc1234567")); // Contains letters
    }

    @Test
    public void testLoginUser() {
        loginSystem.username = "test_1";
        loginSystem.password = "P@ssw0rd123";
        
        // Successful login
        assertTrue(loginSystem.loginUser("test_1", "P@ssw0rd123"));
        
        // Failed logins
        assertFalse(loginSystem.loginUser("wrong", "P@ssw0rd123"));
        assertFalse(loginSystem.loginUser("test_1", "wrong"));
        assertFalse(loginSystem.loginUser("wrong", "wrong"));
    }

    @Test
    public void testReturnLoginStatus() {
        loginSystem.firstName = "John";
        loginSystem.lastName = "Doe";
        
        // Successful login message
        assertEquals("Welcome John, Doe it is great to see you again.", 
                    loginSystem.returnLoginStatus(true));
        
        // Failed login message
        assertEquals("Username or password incorrect, please try again.", 
                    loginSystem.returnLoginStatus(false));
    }
}