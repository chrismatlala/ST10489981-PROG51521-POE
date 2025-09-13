/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package loginsystempoe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 *
 * @author chris
 */
public class MessageTest {
    Message message = new Message();
    
    public MessageTest() {
    }

    @Test
    public void testGetMessage() {
        String expected = "I have arrived";
        String actual = message.returnMessage;
        
        assertEquals(expected,actual);
    }
    
}
