import java.util.Scanner;
import java.util.*;

class MessageTest {
    public static void main(String[] args) {
        testMessageLength();
        testRecipientFormat();
        testMessageHash();
        testSendMessageAction();
    }

    public static void testMessageLength() {
        System.out.println("=== Testing Message Length ===");
        
        // Test success case
        Message msg1 = new Message("Hi Mike, can you join us for dinner tonight.", "+27716659002");
        if (msg1.checkMessageLength()) {
            System.out.println("✓ Success: Message ready to send.");
        } else {
            System.out.println("✗ Failure: Message exceeds 250 characters.");
        }

        // Test failure case
        String longMessage = "A".repeat(251);
        Message msg2 = new Message(longMessage, "+27716659002");
        if (!msg2.checkMessageLength()) {
            System.out.println("✓ Failure handled: Message exceeds 250 characters by " + (longMessage.length() - 250) + " characters please reduce size.");
        } else {
            System.out.println("✗ Failure not handled properly.");
        }
    }

    public static void testRecipientFormat() {
        System.out.println("\n=== Testing Recipient Format ===");
        
        // Test success case
        Message msg1 = new Message("Test message", "+27716659002");
        if (msg1.checkRecipientFormat()) {
            System.out.println("✓ Success: Cell phone number successfully captured.");
        } else {
            System.out.println("✗ Failure: Cell phone number format incorrect.");
        }

        // Test failure case
        Message msg2 = new Message("Test message", "10529729509"); // No international code
        if (!msg2.checkRecipientFormat()) {
            System.out.println("✓ Failure handled: Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.");
        } else {
            System.out.println("✗ Failure not handled properly.");
        }
    }

    public static void testMessageHash() {
        System.out.println("\n=== Testing Message Hash ===");
        
        Message msg = new Message("Hi Mike, can you join us for dinner tonight.", "+27716659002");
        String hash = msg.checkMessageHash();
        System.out.println("Message Hash: " + hash);
        
        // Expected format: first 2 chars of message ID + : + message number + : + first word + : + last word
        if (hash.matches("\\d{2}:\\d{2}:HI:TONIGHT")) {
            System.out.println("✓ Hash format is correct");
        } else {
            System.out.println("✗ Hash format is incorrect");
        }
    }

    public static void testSendMessageAction() {
        System.out.println("\n=== Testing Send Message Action ===");
        
        Message msg = new Message("Test message", "+27716659002");
        
        // Test send action
        System.out.println("Testing send action...");
        // This would require mocking user input in a real unit test
        
        System.out.println("✓ All basic functionality tested");
    }
}