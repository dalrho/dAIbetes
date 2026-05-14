package Inbox;

import java.util.ArrayList;
import java.util.List; /**
 * Represents a consultation request as a conversation thread.
 * Wraps a pending/responded request from tblConsultationRequest.
 */
public class Conversation {

    private String  participantName; // patient full name
    private String  avatarColor;
    private String  lastSeen;        // requested_on formatted date
    private String  lastMessage;     // short preview shown in list
    private List<Message> messages;

    // DB-backed fields
    private int     requestId;
    private int     testId;
    private int     patientId;
    private boolean isAccepted;
    private boolean isResponded;     // true if responded_on IS NOT NULL

    public Conversation(String participantName, String avatarColor, String lastSeen) {
        this.participantName = participantName;
        this.avatarColor     = avatarColor;
        this.lastSeen        = lastSeen;
        this.messages        = new ArrayList<>();
        this.lastMessage     = "Consultation request pending...";
    }

    public void addMessage(Message message) { messages.add(message); }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String  getParticipantName()            { return participantName; }
    public String  getAvatarColor()                { return avatarColor; }
    public String  getLastSeen()                   { return lastSeen; }
    public void    setLastSeen(String lastSeen)    { this.lastSeen = lastSeen; }
    public String  getLastMessage()                { return lastMessage; }
    public void    setLastMessage(String msg)      { this.lastMessage = msg; }
    public List<Message> getMessages()             { return messages; }

    public int     getRequestId()                  { return requestId; }
    public void    setRequestId(int requestId)     { this.requestId = requestId; }
    public int     getTestId()                     { return testId; }
    public void    setTestId(int testId)           { this.testId = testId; }
    public int     getPatientId()                  { return patientId; }
    public void    setPatientId(int patientId)     { this.patientId = patientId; }
    public boolean isAccepted()                    { return isAccepted; }
    public void    setAccepted(boolean accepted)   { this.isAccepted = accepted; }
    public boolean isResponded()                   { return isResponded; }
    public void    setResponded(boolean responded) { this.isResponded = responded; }
}
