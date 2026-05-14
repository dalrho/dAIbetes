package Inbox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; /**
 * Represents a single message bubble in a conversation.
 * For consultation requests, the initial message is auto-generated
 * from the request details. Future messages can be user-sent.
 */
public class Message {

    private String        content;
    private boolean       sent;      // true = doctor sent, false = patient/system
    private LocalDateTime timestamp;

    public Message(String content, boolean sent, LocalDateTime timestamp) {
        this.content   = content;
        this.sent      = sent;
        this.timestamp = timestamp;
    }

    public String        getContent()       { return content; }
    public boolean       isSent()           { return sent; }
    public LocalDateTime getTimestamp()     { return timestamp; }
    public String        getFormattedTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
