package Singleton;

import java.util.List;

public class Message {
    private String message;
    private String senderEmail;
    private String receiverEmail;
    private long timestamp;

    // Default constructor (needed for Firebase deserialization)
    public Message() {}

    // Constructor
    public Message(String message, String senderEmail,String receiverEmail, long timestamp) {
        this.message = message;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.timestamp = timestamp;
    }
    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    // Ensure this setter is public for updating sender email if needed
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // New method to get the display name of the sender
    public String getSenderDisplayName() {
        // Replace "admin_email@example.com" with your actual admin email
        if ("admin2@example.com".equals(senderEmail)) {
            return "MDO Admin";
        }
        return senderEmail; // For users, show their email address
    }
}
