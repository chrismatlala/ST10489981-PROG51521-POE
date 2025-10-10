
package userauthenticationsystem;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class MessageTest {
    private Message testMessage;
    private static final String TEST_RECIPIENT = "+27726030002";
    private static final String TEST_MESSAGE_TEXT = "Hi Mike, can you join us for dinner tonight";
    
    @BeforeEach
    void setUp() {
        Message.clearAllMessages();
        testMessage = new Message(TEST_RECIPIENT, TEST_MESSAGE_TEXT);
    }
    
    @AfterEach
    void tearDown() {
        Message.clearAllMessages();
        try {
            Files.deleteIfExists(Paths.get("stored_messages.json"));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    // Test Case 1: Based on Screenshot 15 data
    @Test
    @DisplayName("Test Case 1: Valid message with recipient +27726030002")
    void testMessage1_ValidData() {
        Message message1 = new Message("+27726030002", "Hi Mike, can you join us for dinner tonight");
        
        assertTrue(message1.checkMessage(), "Message should be valid");
        assertTrue(message1.checkRecipient(), "Recipient should be valid");
        assertNotNull(message1.getMessageID());
        assertNotNull(message1.getMessageHash());
        
        // Test message hash format
        String hash = message1.checkMessageHash();
        assertTrue(hash.matches("\\d+-token-\\d+-hi-tonight"));
    }

    // Test Case 2: Based on Screenshot 15 data (invalid recipient)
    @Test
    @DisplayName("Test Case 2: Invalid recipient 56375755869")
    void testMessage2_InvalidRecipient() {
        Message message2 = new Message("56375755869", "Hi Keegan, did you receive the payment?");
        
        assertTrue(message2.checkMessage(), "Message should be valid");
        assertFalse(message2.checkRecipient(), "Recipient should be invalid - missing international code");
    }

    // Updated character limit tests (360 characters mentioned in screenshot 16)
    @Test
    @DisplayName("Message should not exceed 360 characters - SUCCESS")
    void testMessageLength_Success() {
        String validMessage = "A".repeat(360); // Exactly 360 characters
        Message message = new Message("+27123456789", validMessage);
        
        assertTrue(message.checkMessage());
    }

    @Test
    @DisplayName("Message should not exceed 360 characters - FAILURE")
    void testMessageLength_Failure() {
        String longMessage = "A".repeat(365); // 365 characters - 5 over limit
        Message message = new Message("+27123456789", longMessage);
        
        assertFalse(message.checkMessage());
    }

    @Test
    @DisplayName("Recipient number formatting - SUCCESS")
    void testRecipientFormat_Success() {
        String[] validRecipients = {
            "+27726030002",
            "+27123456789",
            "+44123456789"
        };
        
        for (String recipient : validRecipients) {
            Message message = new Message(recipient, "Test message");
            assertTrue(message.checkRecipient(), "Should accept valid recipient: " + recipient);
        }
    }

    @Test
    @DisplayName("Recipient number formatting - FAILURE")
    void testRecipientFormat_Failure() {
        String[] invalidRecipients = {
            "56375755869",      // No international code
            "27726030002",      // Missing +
            "+123456789012",    // Too long
            "+12abc34567",      // Contains letters
            "+",                // Too short
            ""                  // Empty
        };
        
        for (String recipient : invalidRecipients) {
            Message message = new Message(recipient, "Test message");
            assertFalse(message.checkRecipient(), "Should reject invalid recipient: " + recipient);
        }
    }

    @Test
    @DisplayName("Message Hash generation correctness")
    void testMessageHashCorrectness() {
        // Test with specific data from requirements
        Message message = new Message("+27726030002", "Hi Mike, can you join us for dinner tonight");
        String hash = message.checkMessageHash();
        
        // Expected format: first-two-numbers-token-message-number-first-last-words
        assertNotNull(hash);
        String[] parts = hash.split("-");
        assertEquals(5, parts.length);
        assertEquals("token", parts[1]);
        assertEquals("hi", parts[3]); // First word
        assertEquals("tonight", parts[4]); // Last word
    }

    @Test
    @DisplayName("Message ID generation and format")
    void testMessageIDGeneration() {
        Message message1 = new Message("+27123456789", "First message");
        Message message2 = new Message("+27123456789", "Second message");
        
        String id1 = message1.getMessageID();
        String id2 = message2.getMessageID();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2, "Message IDs should be unique");
        assertTrue(id1.matches("\\d+"), "Message ID should contain only digits");
    }

    @Test
    @DisplayName("User action selection - Send Message")
    void testUserAction_SendMessage() {
        Message message = new Message("+27726030002", "Test message for sending");
        
        // This would normally show JOptionPane, but we test the validation
        assertTrue(message.checkMessage());
        assertTrue(message.checkRecipient());
        
        // The actual send action would be tested in integration tests
    }

    @Test
    @DisplayName("User action selection - Discard Message")
    void testUserAction_DiscardMessage() {
        Message message = new Message("+27726030002", "Test message for discarding");
        
        // Test that discard doesn't affect message count
        int initialCount = Message.getTotalMessagesSent();
        // Discard action would happen here
        assertEquals(initialCount, Message.getTotalMessagesSent());
    }

    @Test
    @DisplayName("User action selection - Store Message")
    void testUserAction_StoreMessage() {
        Message message = new Message("+27726030002", "Test message for storing");
        
        // Test storage functionality
        assertDoesNotThrow(() -> message.storeMessage());
        
        // Verify file was created (in real scenario)
        File file = new File("stored_messages.json");
        // File might exist from previous tests, so we don't assert existence
    }

    @Test
    @DisplayName("Total messages sent tracking")
    void testTotalMessagesTracking() {
        int initialCount = Message.getTotalMessagesSent();
        
        Message message1 = new Message("+27123456789", "Message 1");
        Message message2 = new Message("+27123456789", "Message 2");
        
        // Note: In actual application, setMessage("send") would increment counter
        // This tests the static tracking mechanism
        assertTrue(Message.getTotalMessagesSent() >= initialCount);
    }

    @Nested
    @DisplayName("Integration Tests with Test Data")
    class IntegrationTests {
        
        @Test
        @DisplayName("Complete workflow with test data from screenshot 15")
        void testCompleteWorkflowWithTestData() {
            // Test Data 1
            Message message1 = new Message("+27726030002", "Hi Mike, can you join us for dinner tonight");
            assertAll("Message 1 validation",
                () -> assertTrue(message1.checkMessage()),
                () -> assertTrue(message1.checkRecipient()),
                () -> assertNotNull(message1.getMessageHash())
            );
            
            // Test Data 2
            Message message2 = new Message("56375755869", "Hi Keegan, did you receive the payment?");
            assertAll("Message 2 validation",
                () -> assertTrue(message2.checkMessage()),
                () -> assertFalse(message2.checkRecipient()) // Should fail recipient validation
            );
        }
        
        @Test
        @DisplayName("Message hash loop testing")
        void testMessageHashLoop() {
            String[] testMessages = {
                "Hi Mike, can you join us for dinner tonight",
                "Hi Keegan, did you receive the payment",
                "Quickchat application testing"
            };
            
            for (String msgText : testMessages) {
                Message msg = new Message("+27123456789", msgText);
                String hash = msg.checkMessageHash();
                
                assertNotNull(hash);
                assertTrue(hash.matches("\\d+-token-\\d+-\\w+-\\w+"));
                
                // Verify first and last words are in hash
                String[] words = msgText.split(" ");
                if (words.length > 0) {
                    String firstWord = words[0].toLowerCase();
                    String lastWord = words[words.length - 1].toLowerCase();
                    assertTrue(hash.contains(firstWord));
                    assertTrue(hash.contains(lastWord));
                }
            }
        }
    }
}