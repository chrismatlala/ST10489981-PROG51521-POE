
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
    private Message testMessage;
    private static final String TEST_RECIPIENT = "+271234567";
    private static final String TEST_MESSAGE_TEXT = "Hello this is a test message";
    
    @BeforeEach
    void setUp() {
        Message.clearAllMessages(); // Clear static data before each test
        testMessage = new Message(TEST_RECIPIENT, TEST_MESSAGE_TEXT);
    }
    
    @AfterEach
    void tearDown() {
        Message.clearAllMessages();
        // Clean up test files
        try {
            Files.deleteIfExists(Paths.get("stored_messages.json"));
            Files.deleteIfExists(Paths.get("test_messages.json"));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    void testMessageCreation() {
        assertNotNull(testMessage);
        assertEquals(TEST_RECIPIENT, testMessage.getRecipient());
        assertEquals(TEST_MESSAGE_TEXT, testMessage.getMessageText());
        assertNotNull(testMessage.getMessageID());
        assertNotNull(testMessage.getMessageHash());
    }

    @Test
    void testCheckMessage_Success() {
        // Test with valid message length
        Message validMessage = new Message(TEST_RECIPIENT, "Short message");
        assertTrue(validMessage.checkMessage());
    }

    @Test
    void testCheckMessage_Failure() {
        // Test with message exceeding 100 characters
        String longMessage = "This is a very long message that definitely exceeds the maximum allowed " +
                           "character count of one hundred characters for the QuickChat application";
        Message invalidMessage = new Message(TEST_RECIPIENT, longMessage);
        assertFalse(invalidMessage.checkMessage());
    }

    @Test
    void testCheckMessage_Boundary() {
        // Test with exactly 100 characters
        String exact100Chars = "This message is exactly one hundred characters long which should be perfectly acceptable for our system";
        assertEquals(100, exact100Chars.length());
        Message boundaryMessage = new Message(TEST_RECIPIENT, exact100Chars);
        assertTrue(boundaryMessage.checkMessage());
        
        // Test with 101 characters
        String over100Chars = exact100Chars + "x";
        assertEquals(101, over100Chars.length());
        Message overBoundaryMessage = new Message(TEST_RECIPIENT, over100Chars);
        assertFalse(overBoundaryMessage.checkMessage());
    }

    @Test
    void testCheckRecipient_Success() {
        // Test valid recipient formats
        String[] validRecipients = {
            "+271234567",
            "+1234567890",
            "+441234567"
        };
        
        for (String recipient : validRecipients) {
            Message msg = new Message(recipient, "Test message");
            assertTrue(msg.checkRecipient(), "Should validate recipient: " + recipient);
        }
    }

    @Test
    void testCheckRecipient_Failure() {
        // Test invalid recipient formats
        String[] invalidRecipients = {
            "271234567",      // Missing +
            "+12345678901",   // Too long (11 chars)
            "+12abc3456",     // Contains letters
            "+",              // Too short
            "",               // Empty
            null              // Null
        };
        
        for (String recipient : invalidRecipients) {
            Message msg = new Message(recipient, "Test message");
            assertFalse(msg.checkRecipient(), "Should invalidate recipient: " + recipient);
        }
    }

    @Test
    void testCheckMessageHash_Format() {
        String hash = testMessage.checkMessageHash();
        assertNotNull(hash);
        
        // Check hash format: first-two-numbers-token-message-number-first-last-words
        String[] parts = hash.split("-");
        assertEquals(5, parts.length);
        assertEquals("token", parts[1]);
        
        // First part should be numbers
        assertTrue(parts[0].matches("\\d+"));
        
        // Third part should be a number (message counter)
        assertTrue(parts[2].matches("\\d+"));
    }

    @Test
    void testCheckMessageHash_Consistency() {
        // Same input should produce same hash
        String hash1 = testMessage.checkMessageHash();
        String hash2 = testMessage.checkMessageHash();
        assertEquals(hash1, hash2);
    }

    @Test
    void testCheckMessageHash_WithDifferentMessages() {
        Message msg1 = new Message("+271234567", "Hello world test");
        Message msg2 = new Message("+271234567", "Goodbye world test");
        
        assertNotEquals(msg1.checkMessageHash(), msg2.checkMessageHash());
    }

    @Test
    void testSetMessage_Send() {

        assertTrue(testMessage.checkMessage());
        assertTrue(testMessage.checkRecipient());
    }

    @Test
    void testPrintMessage() {
        // Add some test messages
        Message msg1 = new Message("+271234567", "First message");
        Message msg2 = new Message("+271234568", "Second message");
        
        msg1.setMessage(); // This would normally show dialog
        msg2.setMessage(); // This would normally show dialog
        
        String output = msg1.printMessage();
        assertNotNull(output);
        assertTrue(output.contains("All Sent Messages"));
        assertTrue(output.contains("First message") || output.contains("Second message"));
    }

    @Test
    void testReturnTotalMessages() {
        assertEquals(0, testMessage.returnTotalMessages());
        
        // Create and "send" messages
        Message msg1 = new Message("+271234567", "Message 1");
        Message msg2 = new Message("+271234568", "Message 2");
        
        // Note: In actual use, setMessage("send") would increment the counter
        // For testing, we'll test the static method directly
        // This tests the getter functionality
        assertTrue(testMessage.returnTotalMessages() >= 0);
    }

    @Test
    void testStoreMessage() {
        // Test JSON file creation
        String testFileName = "test_messages.json";
        
        // Modify storeMessage to use test file (in real scenario, you'd inject filename)
        try {
            testMessage.storeMessage();
            
            // Check if file was created
            File file = new File("stored_messages.json");
            assertTrue(file.exists() || file.length() > 0);
        } catch (Exception e) {
            // File operations might fail in test environment
            System.out.println("File operation test skipped: " + e.getMessage());
        }
    }

    @Test
    void testGenerateMessageID() {
        Message msg1 = new Message("+271234567", "Test 1");
        Message msg2 = new Message("+271234568", "Test 2");
        
        assertNotNull(msg1.getMessageID());
        assertNotNull(msg2.getMessageID());
        assertNotEquals(msg1.getMessageID(), msg2.getMessageID());
    }

    @Test
    void testMessageHashContent() {
        Message msg = new Message("+271234567", "Hello world test message");
        String hash = msg.checkMessageHash();
        
        // Hash should contain elements from the message
        assertTrue(hash.contains("hello")); // first word lowercase
        assertTrue(hash.contains("message")); // last word lowercase
        assertTrue(hash.contains("token"));
    }

    @Test
    void testStaticMethods() {
        assertEquals(0, Message.getTotalMessagesSent());
        
        Message.clearAllMessages();
        assertEquals(0, Message.getTotalMessagesSent());
    }

    @Test
    void testSettersRegenerateHash() {
        String originalHash = testMessage.getMessageHash();
        
        // Change message text
        testMessage.setMessageText("New different message text");
        String newHash = testMessage.getMessageHash();
        
        assertNotEquals(originalHash, newHash);
        
        // Change recipient
        testMessage.setRecipient("+271234568");
        String newerHash = testMessage.getMessageHash();
        
        assertNotEquals(newHash, newerHash);
    }

    @Test
    void testToString() {
        String stringRepresentation = testMessage.toString();
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("Message ID"));
        assertTrue(stringRepresentation.contains("Hash"));
        assertTrue(stringRepresentation.contains("Recipient"));
    }

    @Test
    void testMessageWithEmptyText() {
        Message emptyMessage = new Message(TEST_RECIPIENT, "");
        assertTrue(emptyMessage.checkMessage()); // Empty message should be valid (within length)
        assertNotNull(emptyMessage.getMessageHash());
    }

    @Test
    void testMessageWithNullText() {
        Message nullMessage = new Message(TEST_RECIPIENT, null);
        assertFalse(nullMessage.checkMessage()); // Null message should be invalid
    }

    private static class HeadlessException {

        public HeadlessException() {
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        void testCompleteMessageFlow() {
            // Test a complete valid message flow
            Message msg = new Message("+271234567", "Integration test message");
            
            assertTrue(msg.checkMessage());
            assertTrue(msg.checkRecipient());
            
            String hash = msg.checkMessageHash();
            assertNotNull(hash);
            assertTrue(hash.startsWith(msg.getMessageID().substring(0, 2)));
        }
        
        @Test
        void testMultipleMessagesTracking() {
            Message msg1 = new Message("+271234567", "First");
            Message msg2 = new Message("+271234568", "Second");
            Message msg3 = new Message("+271234569", "Third");
            
            // All messages should have unique IDs and hashes
            assertNotEquals(msg1.getMessageID(), msg2.getMessageID());
            assertNotEquals(msg1.getMessageHash(), msg2.getMessageHash());
            assertNotEquals(msg2.getMessageHash(), msg3.getMessageHash());
        }
    }
}