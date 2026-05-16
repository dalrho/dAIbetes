package org.example.daibetes.shared.models;

public class Notification {
    private final int notificationId;
    private final int pId;
    private final int requestId;
    private final String message;
    private final String actionType;
    private final boolean isRead;

    public Notification(int notificationId, int pId, int requestId, String message, String actionType, boolean isRead) {
        this.notificationId = notificationId;
        this.pId = pId;
        this.requestId = requestId;
        this.message = message;
        this.actionType = actionType;
        this.isRead = isRead;
    }

    public int getNotificationId() { return notificationId; }
    public int getPId() { return pId; }
    public int getRequestId() { return requestId; }
    public String getMessage() { return message; }
    public String getActionType() { return actionType; }
    public boolean isRead() { return isRead; }
}