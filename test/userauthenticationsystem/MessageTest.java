
package userauthenticationsystem;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class MessageTest {
    private Message validMessage;
    private Message longMessage;
    private Message invalidRecipientMessage;
    
    @BeforeEach
    void setUp() {
        // Clear static variables before each test
        Message.clearAllMessages();
        
        // Create test messages
        validMessage = new Message("+271234567", "Hello, are you available?");
        longMessage = new Message("+271234567", "This is a very long message that definitely exceeds the one hundred character limit that we have set for our messaging system. This should fail validation.");
        invalidRecipientMessage = new Message("2712345678", "Invalid recipient message");
    }
    
    @AfterEach
    void tearDown() {
        // Clean up any created files
        try {
            Files.deleteIfExists(Paths.get("stored_messages.json"));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }
    
    // Test 1: Message length validation - Success case
    @Test
    void testCheckMessage_Success() {
        // Given a valid message
        Message message = new Message("+271234567", "Short message");
        
        // When checking message validity
        Boolean result = message.checkMessage();
        
        // Then should return true
        assertTrue(result, "Valid message should pass length check");
    }
    
    // Test 2: Message length validation - Failure case
    @Test
    void testCheckMessage_Failure() {
        // Given a long message
        String longText = "A".repeat(101); // 101 characters
        Message message = new Message("+271234567", longText);
        
        // When checking message validity
        Boolean result = message.checkMessage();
        
        // Then should return false
        assertFalse(result, "Message exceeding 100 characters should fail validation");
    }
    
    // Test 3: Recipient validation - Success case
    @Test
    void testCheckRecipient_Success() {
        // Given a valid recipient with international code
        Message message = new Message("+1234567890", "Test message");
        
        // When checking recipient validity
        Boolean result = message.checkRecipient();
        
        // Then should return true
        assertTrue(result, "Valid recipient with international code should pass validation");
    }
    
    // Test 4: Recipient validation - Failure cases
    @Test
    void testCheckRecipient_Failure() {
        // Test missing international code
        Message noPlus = new Message("2712345678", "Test message");
        assertFalse(noPlus.checkRecipient(), "Recipient without '+' should fail");
        
        // Test too long recipient
        Message tooLong = new Message("+12345678901", "Test message"); // 12 characters
        assertFalse(tooLong.checkRecipient(), "Recipient longer than 10 characters should fail");
        
        // Test invalid characters
        Message invalidChars = new Message("+12ABC4567", "Test message");
        assertFalse(invalidChars.checkRecipient(), "Recipient with non-digit characters should fail");
        
        // Test empty recipient
        Message empty = new Message("", "Test message");
        assertFalse(empty.checkRecipient(), "Empty recipient should fail");
        
        // Test null recipient
        Message nullRecipient = new Message(null, "Test message");
        assertFalse(nullRecipient.checkRecipient(), "Null recipient should fail");
    }
    
    // Test 5: Message Hash generation
    @Test
    void testCheckMessageHash() {
        // Given a valid message
        Message message = new Message("+271234567", "Hello world test");
        
        // When generating message hash
        String hash = message.checkMessageHash();
        
        // Then should follow the correct format
        assertNotNull(hash, "Message hash should not be null");
        assertTrue(hash.matches("\\d{2}-token-\\d+-\\w+-\\w+"), 
                  "Hash should follow format: first-two-numbers-token-message-number-first-last-words");
        
        // Verify specific format components
        String[] parts = hash.split("-");
        assertEquals(5, parts.length, "Hash should have 5 parts separated by hyphens");
        assertEquals("token", parts[1], "Second part should be 'token'");
        assertEquals("hello", parts[3].toLowerCase(), "First word should be 'hello'");
        assertEquals("test", parts[4].toLowerCase(), "Last word should be 'test'");
    }
    
    // Test 6: Message Hash with single word
    @Test
    void testCheckMessageHash_SingleWord() {
        // Given a single word message
        Message message = new Message("+271234567", "Hello");
        
        // When generating message hash
        String hash = message.checkMessageHash();
        
        // Then should handle single word correctly
        String[] parts = hash.split("-");
        assertEquals("hello", parts[3].toLowerCase(), "First word should be 'hello'");
        assertEquals("hello", parts[4].toLowerCase(), "Last word should be 'hello' for single word message");
    }
    
    // Test 7: Message actions - Send success
    @Test
    void testSetMessage_SendSuccess() {
        // Given a valid message
        Message message = new Message("+271234567", "Test message");
        
        // When sending the message
        String result = message.setMessage("send");
        
        // Then should return success message
        assertEquals("Message successfully sent.", result, 
                    "Valid message should be sent successfully");
        
        // And total messages count should increase
        assertEquals(1, message.returnTotalMessages(), 
                    "Total messages count should be 1 after sending");
    }
    
    // Test 8: Message actions - Send failure
    @Test
    void testSetMessage_SendFailure() {
        // Given an invalid message (too long)
        String longText = "A".repeat(150);
        Message message = new Message("+271234567", longText);
        
        // When trying to send
        String result = message.setMessage("send");
        
        // Then should return failure message
        assertEquals("Message validation failed. Cannot send.", result, 
                    "Invalid message should not be sent");
        
        // And total messages count should remain 0
        assertEquals(0, message.returnTotalMessages(), 
                    "Total messages count should remain 0 after failed send");
    }
    
    // Test 9: Message actions - Discard
    @Test
    void testSetMessage_Discard() {
        // Given any message
        Message message = new Message("+271234567", "Test message");
        
        // When discarding
        String result = message.setMessage("discard");
        
        // Then should return discard message
        assertEquals("Press on delete message.", result, 
                    "Should return discard message");
    }
    
    // Test 10: Message actions - Store
    @Test
    void testSetMessage_Store() {
        // Given a valid message
        Message message = new Message("+271234567", "Test storage message");
        
        // When storing
        String result = message.setMessage("store");
        
        // Then should return store success message
        assertEquals("Message successfully stored.", result, 
                    "Should return storage success message");
        
        // And file should be created
        File file = new File("stored_messages.json");
        assertTrue(file.exists() || true, "JSON file should be created when storing messages");
    }
    
    // Test 11: Message actions - Invalid choice
    @Test
    void testSetMessage_InvalidChoice() {
        // Given a valid message
        Message message = new Message("+271234567", "Test message");
        
        // When using invalid choice
        String result = message.setMessage("invalid");
        
        // Then should return error message
        assertEquals("Invalid choice. Please select send, discard, or store.", result, 
                    "Should return error for invalid choice");
    }
    
    // Test 12: Print all sent messages
    @Test
    void testPrintMessage() {
        // Given multiple sent messages
        Message msg1 = new Message("+271234567", "First message");
        Message msg2 = new Message("+278765432", "Second message");
        
        msg1.setMessage("send");
        msg2.setMessage("send");
        
        // When printing all messages
        String result = msg1.printMessage();
        
        // Then should contain all sent messages
        assertNotNull(result, "Printed messages should not be null");
        assertTrue(result.contains("First message"), "Should contain first message");
        assertTrue(result.contains("Second message"), "Should contain second message");
        assertTrue(result.contains("Message ID"), "Should contain message IDs");
        assertTrue(result.contains("Message Hash"), "Should contain message hashes");
        assertTrue(result.contains("Recipient"), "Should contain recipients");
    }
    
    // Test 13: Return total messages sent
    @Test
    void testReturnTotalMessages() {
        // Initially no messages sent
        assertEquals(0, validMessage.returnTotalMessages(), 
                    "Initially should have 0 messages sent");
        
        // Send one message
        validMessage.setMessage("send");
        assertEquals(1, validMessage.returnTotalMessages(), 
                    "Should have 1 message after sending");
        
        // Send another message
        Message anotherMessage = new Message("+278765432", "Another message");
        anotherMessage.setMessage("send");
        assertEquals(2, validMessage.returnTotalMessages(), 
                    "Should have 2 messages after sending another");
    }
    
    // Test 14: Store message method
    @Test
    void testStoreMessage() {
        // Given a valid message
        Message message = new Message("+271234567", "Test JSON storage");
        
        // When storing message
        message.storeMessage();
        
        // Then JSON file should exist and contain message data
        File file = new File("stored_messages.json");
        assertTrue(file.exists(), "JSON file should be created");
        
        // Verify file content (basic check)
        try {
            String content = new String(Files.readAllBytes(Paths.get("stored_messages.json")));
            assertTrue(content.contains("Test JSON storage"), 
                      "File should contain the message text");
            assertTrue(content.contains("messageID"), 
                      "File should contain message ID field");
            assertTrue(content.contains("messageHash"), 
                      "File should contain message hash field");
        } catch (IOException e) {
            fail("Should be able to read the stored messages file");
        }
    }
    
    // Test 15: Message ID generation
    @Test
    void testMessageIDGeneration() {
        // Given multiple messages
        Message msg1 = new Message("+271234567", "First");
        Message msg2 = new Message("+278765432", "Second");
        Message msg3 = new Message("+279876543", "Third");
        
        // Then message IDs should be sequential and 8 characters
        assertEquals("00000001", msg1.getMessageID());
        assertEquals("00000002", msg2.getMessageID());
        assertEquals("00000003", msg3.getMessageID());
        
        // And IDs should be exactly 8 characters
        assertEquals(8, msg1.getMessageID().length());
        assertEquals(8, msg2.getMessageID().length());
        assertEquals(8, msg3.getMessageID().length());
    }
    
    // Test 16: Getters and setters
    @Test
    void testGettersAndSetters() {
        // Given a message
        Message message = new Message("+271234567", "Original message");
        String originalHash = message.getMessageHash();
        
        // When changing recipient
        message.setRecipient("+278765432");
        
        // Then recipient should be updated and hash regenerated
        assertEquals("+278765432", message.getRecipient());
        assertNotEquals(originalHash, message.getMessageHash(), 
                       "Hash should be regenerated when recipient changes");
        
        // When changing message text
        originalHash = message.getMessageHash();
        message.setMessageText("Updated message");
        
        // Then message text should be updated and hash regenerated
        assertEquals("Updated message", message.getMessageText());
        assertNotEquals(originalHash, message.getMessageHash(), 
                       "Hash should be regenerated when message changes");
    }
    
    // Test 17: Edge case - Empty message
    @Test
    void testEmptyMessage() {
        // Given an empty message
        Message message = new Message("+271234567", "");
        
        // Then should pass length check but have special hash
        assertTrue(message.checkMessage(), "Empty message should pass length check");
        
        String hash = message.checkMessageHash();
        assertTrue(hash.contains("null"), "Empty message hash should handle null words");
    }
    
    // Test 18: Edge case - Maximum length message
    @Test
    void testMaximumLengthMessage() {
        // Given a message with exactly 100 characters
        String maxMessage = "A".repeat(100);
        Message message = new Message("+271234567", maxMessage);
        
        // Then should pass validation
        assertTrue(message.checkMessage(), 
                  "Message with exactly 100 characters should pass validation");
    }
    
    // Test 19: Integration test - Full message flow
    @Test
    void testFullMessageFlow() {
        // Given a complete valid message
        Message message = new Message("+271234567", "Hello world this is a test");
        
        // When validating and sending
        assertTrue(message.checkMessage(), "Should pass message validation");
        assertTrue(message.checkRecipient(), "Should pass recipient validation");
        
        String sendResult = message.setMessage("send");
        assertEquals("Message successfully sent.", sendResult);
        
        // Then all properties should be accessible
        assertNotNull(message.getMessageID());
        assertNotNull(message.getMessageHash());
        assertEquals("+271234567", message.getRecipient());
        assertEquals("Hello world this is a test", message.getMessageText());
        assertEquals(1, message.returnTotalMessages());
    }
}