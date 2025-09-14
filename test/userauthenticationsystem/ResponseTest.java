/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package userauthenticationsystem;

import userauthenticationsystem.Response;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 *
 * @author chris
 */
public class ResponseTest {
    Response response = new Response();
    
    public ResponseTest() {
    }

    @Test
    public void testReturnResponse() {
        String expected = "I see you there";
        String actual = response.returnResponse();
        
        assertEquals(expected,actual);
    }

    @Test
    public void testCalculateNumResponses() {
        int expected = 50;
        int actual = response.calculateNumResponses(10,40);
        
        assertEquals(expected,actual);
    }
    
}
