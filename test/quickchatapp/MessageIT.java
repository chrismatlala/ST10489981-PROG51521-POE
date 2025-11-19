
package quickchatapp;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class quickchatappMessageTest {
    private Message testMessage;
    private static final String TEST_RECIPIENT = "+27726030002";
    private static final String TEST_MESSAGE_TEXT = "Hi Mike, can you you join us for dinner tonight";
    
    @BeforeEach
    void setUp() {
        testMessage = new Message();
        testMessage.setRecipient(TEST_RECIPIENT);
        testMessage.setMessage(TEST_MESSAGE_TEXT);
        
        // Clear any existing JSON files before each test
        try {
            Files.deleteIfExists(Paths.get("sent_messages.json"));
            Files.deleteIfExists(Paths.get("stored_messages.json"));
            Files.deleteIfExists(Paths.get("message_statistics.json"));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        try {
            Files.deleteIfExists(Paths.get("sent_messages.json"));
            Files.deleteIfExists(Paths.get("stored_messages.json"));
            Files.deleteIfExists(Paths.get("message_statistics.json"));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }

    // Test Case 1: Message ID Generation
    @Test
    @DisplayName("Test Message ID generation and format")
    void testMessageIDGeneration() {
        Message message = new Message();
        String messageID = message.getMessageID();
        
        assertNotNull(messageID, "Message ID should not be null");
        assertEquals(10, messageID.length(), "Message ID should be 10 digits long");
        assertTrue(messageID.matches("\\d{10}"), "Message ID should contain only digits");
        
        // Test that multiple messages have different IDs
        Message message2 = new Message();
        assertNotEquals(messageID, message2.getMessageID(), "Message IDs should be unique");
    }

    // Test Case 2: Recipient Validation
    @Test
    @DisplayName("Test recipient cell phone number validation")
    void testRecipientCellValidation() {
        Message message = new Message();
        
        // Valid recipients
        String[] validRecipients = {
            "+27726030002",    // South Africa
            "+441234567890",   // UK
            "+11234567890",    // USA
            "+27123456789"     // South Africa
        };
        
        for (String recipient : validRecipients) {
            assertEquals(1, message.checkRecipientCell(recipient), 
                "Should accept valid recipient: " + recipient);
        }
        
        // Invalid recipients
        String[] invalidRecipients = {
            "27726030002",     // Missing +
            "+1234567890123",  // Too long (14 chars)
            "+123456789",      // Too short (10 chars)
            "+12abc34567",     // Contains letters
            "",                // Empty string
            null               // Null
        };
        
        for (String recipient : invalidRecipients) {
            assertEquals(0, message.checkRecipientCell(recipient), 
                "Should reject invalid recipient: " + recipient);
        }
    }

    // Test Case 3: Message Hash Creation
    @Test
    @DisplayName("Test message hash creation and format")
    void testCreateMessageHash() {
        Message message = new Message();
        message.setMessage("Hello this is a test message");
        
        String hash = message.createMessageHash();
        assertNotNull(hash, "Message hash should not be null");
        
        // Hash should contain first two digits of message ID, message count, and words
        String[] parts = hash.split(":");
        assertEquals(3, parts.length, "Hash should have 3 parts separated by colons");
        
        // First part should be first two digits of message ID
        assertEquals(2, parts[0].length());
        
        // Second part should be message count
        assertTrue(parts[1].matches("\\d+"));
        
        // Third part should contain first and last words
        assertTrue(parts[2].contains("HELLO"));
        assertTrue(parts[2].contains("MESSAGE"));
    }

    // Test Case 4: Message Hash with Different Messages
    @Test
    @DisplayName("Test message hash with various message formats")
    void testMessageHashWithVariousMessages() {
        String[][] testMessages = {
            {"Hello world", "HELLOWORLD"},
            {"Quick chat application", "QUICKAPPLICATION"},
            {"Single", "SINGLESINGLE"}, // Single word message
            {"Multiple words in this message", "MULTIPLEMESSAGE"},
            {"", ""} // Empty message
        };
        
        for (String[] testData : testMessages) {
            Message message = new Message();
            message.setMessage(testData[0]);
            String hash = message.createMessageHash();
            
            if (!testData[0].isEmpty()) {
                assertTrue(hash.contains(testData[1]), 
                    "Hash should contain combined words for: " + testData[0]);
            }
        }
    }

    // Test Case 5: Message Sending Functionality
    @Test
    @DisplayName("Test message sending functionality")
    void testSentMessage() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage(TEST_MESSAGE_TEXT);
        
        // Test sending message
        String result = message.sentMessage(1);
        assertEquals("Message successfully sent.", result);
        assertEquals("sent", message.getStatus());
        
        // Verify message count increased
        assertTrue(Message.returnTotalMessages() > 0);
    }

    // Test Case 6: Message Storage Functionality
    @Test
    @DisplayName("Test message storage functionality")
    void testStoreMessage() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage(TEST_MESSAGE_TEXT);
        
        String result = message.sentMessage(3); // Store message
        assertEquals("Message successfully stored.", result);
        assertEquals("stored", message.getStatus());
    }

    // Test Case 7: Message Discard Functionality
    @Test
    @DisplayName("Test message discard functionality")
    void testDiscardMessage() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage(TEST_MESSAGE_TEXT);
        
        String result = message.sentMessage(2); // Discard message
        assertEquals("Press 0 to delete message.", result);
        assertEquals("discarded", message.getStatus());
    }

    // Test Case 8: Invalid Choice Handling
    @Test
    @DisplayName("Test invalid choice handling")
    void testInvalidChoice() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage(TEST_MESSAGE_TEXT);
        
        String result = message.sentMessage(99); // Invalid choice
        assertEquals("Invalid option.", result);
    }

    // Test Case 9: Message Printing
    @Test
    @DisplayName("Test message printing format")
    void testPrintMessages() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage(TEST_MESSAGE_TEXT);
        message.setStatus("sent");
        
        String printedMessage = message.printMessages();
        
        assertNotNull(printedMessage);
        assertTrue(printedMessage.contains("MessageID:"));
        assertTrue(printedMessage.contains("Message Hash:"));
        assertTrue(printedMessage.contains("Recipient:"));
        assertTrue(printedMessage.contains("Message:"));
        assertTrue(printedMessage.contains("Status:"));
        assertTrue(printedMessage.contains(TEST_RECIPIENT));
        assertTrue(printedMessage.contains(TEST_MESSAGE_TEXT));
    }

    // Test Case 10: Total Messages Tracking
    @Test
    @DisplayName("Test total messages tracking")
    void testReturnTotalMessages() {
        int initialCount = Message.returnTotalMessages();
        
        // Send multiple messages
        for (int i = 0; i < 3; i++) {
            Message message = new Message();
            message.setRecipient(TEST_RECIPIENT);
            message.setMessage("Test message " + i);
            message.sentMessage(1); // Send message
        }
        
        int finalCount = Message.returnTotalMessages();
        assertEquals(initialCount + 3, finalCount, 
            "Total messages should increase by 3 after sending 3 messages");
    }

    // Test Case 11: Message Count Increment
    @Test
    @DisplayName("Test message count increment")
    void testMessageCountIncrement() {
        Message message1 = new Message();
        Message message2 = new Message();
        Message message3 = new Message();
        
        // Each new message should have an incremented count
        assertTrue(message2.getMessageCount() > message1.getMessageCount());
        assertTrue(message3.getMessageCount() > message2.getMessageCount());
    }

    // Test Case 12: Edge Case - Empty Message
    @Test
    @DisplayName("Test empty message handling")
    void testEmptyMessage() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage(""); // Empty message
        
        String hash = message.createMessageHash();
        assertNotNull(hash);
        
        // Should still be able to send empty message
        String result = message.sentMessage(1);
        assertEquals("Message successfully sent.", result);
    }

    // Test Case 13: Edge Case - Very Long Message
    @Test
    @DisplayName("Test very long message handling")
    void testVeryLongMessage() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        
        // Create a long message
        StringBuilder longMessage = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longMessage.append("word").append(i).append(" ");
        }
        message.setMessage(longMessage.toString().trim());
        
        String hash = message.createMessageHash();
        assertNotNull(hash);
        assertTrue(hash.contains("WORD0")); // First word
        assertTrue(hash.contains("WORD99")); // Last word
    }

    // Test Case 14: Message Status Transitions
    @Test
    @DisplayName("Test message status transitions")
    void testMessageStatusTransitions() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage(TEST_MESSAGE_TEXT);
        
        assertEquals("pending", message.getStatus());
        
        message.sentMessage(1); // Send
        assertEquals("sent", message.getStatus());
        
        // Create new message for storage test
        Message message2 = new Message();
        message2.setRecipient(TEST_RECIPIENT);
        message2.setMessage("Another message");
        message2.sentMessage(3); // Store
        assertEquals("stored", message2.getStatus());
        
        // Create new message for discard test
        Message message3 = new Message();
        message3.setRecipient(TEST_RECIPIENT);
        message3.setMessage("Third message");
        message3.sentMessage(2); // Discard
        assertEquals("discarded", message3.getStatus());
    }

    // Test Case 15: Message Hash Consistency
    @Test
    @DisplayName("Test message hash consistency")
    void testMessageHashConsistency() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage("Consistent message for testing");
        
        String hash1 = message.createMessageHash();
        String hash2 = message.createMessageHash();
        
        assertEquals(hash1, hash2, "Message hash should be consistent for same message");
        
        // Hash should change if message changes
        message.setMessage("Different message now");
        String hash3 = message.createMessageHash();
        assertNotEquals(hash1, hash3, "Hash should change when message content changes");
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Complete message lifecycle test")
        void testCompleteMessageLifecycle() {
            // Create message
            Message message = new Message();
            assertNotNull(message.getMessageID());
            assertEquals("pending", message.getStatus());
            
            // Set recipient and message
            message.setRecipient("+27726030002");
            message.setMessage("Integration test message");
            
            // Verify recipient validation
            assertEquals(1, message.checkRecipientCell(message.getRecipient()));
            
            // Verify hash creation
            String hash = message.createMessageHash();
            assertNotNull(hash);
            assertTrue(hash.contains("INTEGRATION"));
            assertTrue(hash.contains("MESSAGE"));
            
            // Send message
            String sendResult = message.sentMessage(1);
            assertEquals("Message successfully sent.", sendResult);
            assertEquals("sent", message.getStatus());
            
            // Verify message printing
            String printed = message.printMessages();
            assertTrue(printed.contains(message.getMessageID()));
            assertTrue(printed.contains(hash));
            assertTrue(printed.contains("+27726030002"));
            assertTrue(printed.contains("Integration test message"));
            assertTrue(printed.contains("sent"));
        }
        
        @Test
        @DisplayName("Multiple messages with different actions")
        void testMultipleMessagesDifferentActions() {
            int initialTotal = Message.returnTotalMessages();
            
            // Send a message
            Message sentMessage = new Message();
            sentMessage.setRecipient("+27123456789");
            sentMessage.setMessage("This message will be sent");
            sentMessage.sentMessage(1);
            
            // Store a message
            Message storedMessage = new Message();
            storedMessage.setRecipient("+27876543210");
            storedMessage.setMessage("This message will be stored");
            storedMessage.sentMessage(3);
            
            // Discard a message
            Message discardedMessage = new Message();
            discardedMessage.setRecipient("+27654321987");
            discardedMessage.setMessage("This message will be discarded");
            discardedMessage.sentMessage(2);
            
            // Verify statuses
            assertEquals("sent", sentMessage.getStatus());
            assertEquals("stored", storedMessage.getStatus());
            assertEquals("discarded", discardedMessage.getStatus());
            
            // Verify total count (only sent messages should count)
            assertEquals(initialTotal + 1, Message.returnTotalMessages());
        }
        
        @Test
        @DisplayName("Bulk message creation test")
        void testBulkMessageCreation() {
            int batchSize = 10;
            Message[] messages = new Message[batchSize];
            
            // Create multiple messages
            for (int i = 0; i < batchSize; i++) {
                messages[i] = new Message();
                messages[i].setRecipient("+2772603000" + (i % 10));
                messages[i].setMessage("Bulk test message " + i);
            }
            
            // Verify all messages have unique IDs
            for (int i = 0; i < batchSize; i++) {
                for (int j = i + 1; j < batchSize; j++) {
                    assertNotEquals(messages[i].getMessageID(), messages[j].getMessageID(),
                        "All message IDs should be unique");
                }
            }
            
            // Verify message counts are sequential
            for (int i = 1; i < batchSize; i++) {
                assertTrue(messages[i].getMessageCount() > messages[i-1].getMessageCount(),
                    "Message counts should be sequential");
            }
        }
    }

    @Test
    @DisplayName("Message with special characters")
    void testMessageWithSpecialCharacters() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage("Hello! This message has @special# characters$ & symbols*");
        
        String hash = message.createMessageHash();
        assertNotNull(hash);
        // Hash should still be created successfully despite special characters
        assertTrue(hash.contains("HELLO"));
        assertTrue(hash.contains("SYMBOLS"));
    }

    @Test
    @DisplayName("Message with only one word")
    void testSingleWordMessage() {
        Message message = new Message();
        message.setRecipient(TEST_RECIPIENT);
        message.setMessage("Singleword");
        
        String hash = message.createMessageHash();
        assertNotNull(hash);
        // For single word messages, first and last word should be the same
        assertTrue(hash.contains("SINGLEWORDSINGLEWORD"));
    }
}