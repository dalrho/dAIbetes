package Inbox;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * InboxController manages the messaging interface with conversation list,
 * message display, and real-time messaging functionality
 */
public class InboxController {

    // FXML Components - Header
    @FXML private TextField searchField;
    @FXML private Button newMessageBtn;

    // FXML Components - Left Panel
    @FXML private Button inboxTab;
    @FXML private Button archivedTab;
    @FXML private ListView<Conversation> conversationList;

    // FXML Components - Right Panel
    @FXML private HBox messageHeader;
    @FXML private Circle userAvatar;
    @FXML private Label userName;
    @FXML private Label lastSeenLabel;
    @FXML private VBox emptyState;
    @FXML private ScrollPane messagesScrollPane;
    @FXML private VBox messagesContainer;
    @FXML private HBox inputArea;
    @FXML private TextArea messageInput;
    @FXML private Button sendBtn;

    // Data Models
    private ObservableList<Conversation> conversations;
    private ObservableList<Conversation> filteredConversations;
    private Conversation selectedConversation;
    private static final String[] AVATAR_COLORS = {"#3b82f6", "#ef4444", "#10b981", "#f59e0b", "#8b5cf6", "#ec4899"};
    private int avatarIndex = 0;

    @FXML
    public void initialize() {
        setupConversationList();
        setupSearch();
        setupMessageInput();
        loadSampleData();
    }

    @FXML
    private void handleCloseInbox() {
        Stage stage = (Stage) searchField.getScene().getWindow();
        stage.close();
    }
    /**
     * Setup conversation list with custom cell rendering
     */
    private void setupConversationList() {
        conversations = FXCollections.observableArrayList();
        filteredConversations = FXCollections.observableArrayList();

        conversationList.setItems(filteredConversations);
        conversationList.setCellFactory(param -> new ConversationCell());
        conversationList.setOnMouseClicked(event -> {
            if (!conversationList.getSelectionModel().isEmpty()) {
                selectedConversation = conversationList.getSelectionModel().getSelectedItem();
                displayConversation(selectedConversation);
            }
        });
    }

    /**
     * Setup search functionality
     */
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterConversations(newVal.toLowerCase());
        });
    }

    /**
     * Filter conversations based on search query
     */
    private void filterConversations(String query) {
        if (query.isEmpty()) {
            filteredConversations.setAll(conversations);
        } else {
            filteredConversations.clear();
            conversations.stream()
                    .filter(conv -> conv.getParticipantName().toLowerCase().contains(query))
                    .forEach(filteredConversations::add);
        }
    }

    /**
     * Setup message input area
     */
    private void setupMessageInput() {
        messageInput.setWrapText(true);
        messageInput.setPrefRowCount(3);
    }

    /**
     * Display selected conversation
     */
    private void displayConversation(Conversation conversation) {
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        messagesScrollPane.setVisible(true);
        messagesScrollPane.setManaged(true);
        messageHeader.setVisible(true);
        messageHeader.setManaged(true);
        inputArea.setVisible(true);
        inputArea.setManaged(true);

        // Update header
        userName.setText(conversation.getParticipantName());
        lastSeenLabel.setText(conversation.getLastSeen());
        userAvatar.setStyle("-fx-fill: " + conversation.getAvatarColor() + ";");

        // Clear and reload messages
        messagesContainer.getChildren().clear();
        for (Message message : conversation.getMessages()) {
            addMessageToUI(message);
        }

        // Scroll to bottom
        Platform.runLater(() -> {
            messagesScrollPane.setVvalue(1.0);
        });

        messageInput.clear();
    }

    /**
     * Add message to UI
     */
    private void addMessageToUI(Message message) {
        HBox messageBox = new HBox();
        messageBox.setSpacing(12);
        messageBox.setAlignment(Pos.TOP_LEFT);

        if (message.isSent()) {
            messageBox.setAlignment(Pos.TOP_RIGHT);
        }

        // Avatar
        Circle avatar = new Circle(18);
        avatar.setStyle("-fx-fill: " + (message.isSent() ? "#3b82f6" : getAvatarColor()) + ";");

        // Message content
        VBox contentBox = new VBox(4);
        Label senderLabel = new Label(message.isSent() ? "You" : selectedConversation.getParticipantName());
        senderLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #9ca3af; -fx-font-weight: bold;");

        Label messageLabel = new Label(message.getContent());
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-text-fill: " +
                (message.isSent() ? "#ffffff" : "#1f2937") + "; -fx-background-color: " +
                (message.isSent() ? "#3b82f6" : "#f3f4f6") + "; -fx-background-radius: 12;");

        Label timeLabel = new Label(message.getFormattedTime());
        timeLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #d1d5db;");

        contentBox.getChildren().addAll(senderLabel, messageLabel, timeLabel);

        if (message.isSent()) {
            messageBox.getChildren().addAll(contentBox, avatar);
        } else {
            messageBox.getChildren().addAll(avatar, contentBox);
        }

        messagesContainer.getChildren().add(messageBox);
    }

    /**
     * Handle new message button
     */
    @FXML
    private void handleNewMessage() {
        // Create simple dialog for new message
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Message");
        dialog.setHeaderText("Start a new conversation");
        dialog.setContentText("Recipient name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String recipientName = result.get().trim();
            Conversation newConv = new Conversation(recipientName, getAvatarColor(), "Now");
            conversations.add(newConv);
            filteredConversations.add(0, newConv);
            conversationList.getSelectionModel().select(0);
            displayConversation(newConv);
        }
    }

    /**
     * Handle send message
     */
    @FXML
    private void handleSendMessage() {
        if (selectedConversation == null) {
            showAlert("Select a conversation first!");
            return;
        }

        String content = messageInput.getText().trim();
        if (content.isEmpty()) {
            showAlert("Message cannot be empty!");
            return;
        }

        // Create and add message
        Message message = new Message(content, true, LocalDateTime.now());
        selectedConversation.addMessage(message);
        addMessageToUI(message);

        // Update conversation preview
        selectedConversation.setLastMessage(content);
        selectedConversation.setLastSeen("Now");
        conversationList.refresh();

        messageInput.clear();

        // Scroll to bottom
        Platform.runLater(() -> {
            messagesScrollPane.setVvalue(1.0);
        });

        // Simulate received message after 1 second
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                Platform.runLater(() -> {
                    Message receivedMessage = new Message("Thanks for the message! 👋", false, LocalDateTime.now());
                    selectedConversation.addMessage(receivedMessage);
                    addMessageToUI(receivedMessage);
                    Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Handle inbox tab
     */
    @FXML
    private void handleInboxTab() {
        inboxTab.setStyle("-fx-padding: 8 12; -fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #3b82f6; -fx-background-color: transparent; -fx-border-color: #3b82f6; -fx-border-width: 0 0 2 0;");
        archivedTab.setStyle("-fx-padding: 8 12; -fx-font-size: 12; -fx-font-weight: 600; -fx-text-fill: #6b7280; -fx-background-color: transparent; -fx-border-width: 0;");
        filterConversations(searchField.getText());
    }

    /**
     * Handle archived tab
     */
    @FXML
    private void handleArchivedTab() {
        archivedTab.setStyle("-fx-padding: 8 12; -fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #3b82f6; -fx-background-color: transparent; -fx-border-color: #3b82f6; -fx-border-width: 0 0 2 0;");
        inboxTab.setStyle("-fx-padding: 8 12; -fx-font-size: 12; -fx-font-weight: 600; -fx-text-fill: #6b7280; -fx-background-color: transparent; -fx-border-width: 0;");
        filteredConversations.clear();
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Get next avatar color
     */
    private String getAvatarColor() {
        return AVATAR_COLORS[avatarIndex++ % AVATAR_COLORS.length];
    }

    /**
     * Load sample data
     */
    private void loadSampleData() {
        String[] names = {"Sarah Chen", "Alex Morgan", "Jordan Lee", "Emma Wilson"};
        String[] lastMessages = {
                "That sounds great! When should we schedule...",
                "Just sent you the files. Let me know if you need...",
                "Thanks for your help yesterday!",
                "Are we still on for tomorrow?"
        };

        for (int i = 0; i < names.length; i++) {
            Conversation conv = new Conversation(names[i], getAvatarColor(), "2h ago");
            conv.setLastMessage(lastMessages[i]);

            // Add sample messages
            conv.addMessage(new Message("Hey, how are you?", false, LocalDateTime.now().minusHours(2)));
            conv.addMessage(new Message("I'm doing great! How about you?", true, LocalDateTime.now().minusHours(1).minusMinutes(50)));
            conv.addMessage(new Message(lastMessages[i], false, LocalDateTime.now().minusHours(1)));

            conversations.add(conv);
        }

        filteredConversations.setAll(conversations);
    }

    /**
     * Custom cell renderer for conversations
     */
    private class ConversationCell extends ListCell<Conversation> {
        @Override
        protected void updateItem(Conversation conversation, boolean empty) {
            super.updateItem(conversation, empty);

            if (empty || conversation == null) {
                setGraphic(null);
                return;
            }

            HBox cellContent = new HBox(12);
            cellContent.setAlignment(Pos.CENTER_LEFT);
            cellContent.setPadding(new Insets(12, 12, 12, 12));
            cellContent.setStyle("-fx-padding: 12; -fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

            // Avatar
            Circle avatar = new Circle(28);
            avatar.setStyle("-fx-fill: " + conversation.getAvatarColor() + ";");

            // Conversation info
            VBox infoBox = new VBox(4);
            infoBox.setSpacing(4);

            Label nameLabel = new Label(conversation.getParticipantName());
            nameLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

            Label messageLabel = new Label(conversation.getLastMessage());
            messageLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #6b7280;");
            messageLabel.setMaxWidth(200);
            messageLabel.setWrapText(true);

            infoBox.getChildren().addAll(nameLabel, messageLabel);

            // Time and unread indicator
            VBox timeBox = new VBox();
            timeBox.setAlignment(Pos.TOP_RIGHT);
            Label timeLabel = new Label(conversation.getLastSeen());
            timeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #9ca3af;");
            timeBox.getChildren().add(timeLabel);

            cellContent.getChildren().addAll(avatar, infoBox, new Region(), timeBox);
            HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);

            setGraphic(cellContent);
            setStyle("-fx-padding: 0; -fx-background-color: " + (isSelected() ? "#f0f4ff" : "#ffffff") + ";");
        }
    }
}

/**
 * Conversation model
 */
class Conversation {
    private String participantName;
    private String avatarColor;
    private String lastSeen;
    private String lastMessage;
    private List<Message> messages;

    public Conversation(String participantName, String avatarColor, String lastSeen) {
        this.participantName = participantName;
        this.avatarColor = avatarColor;
        this.lastSeen = lastSeen;
        this.messages = new ArrayList<>();
        this.lastMessage = "Start a new conversation...";
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public String getParticipantName() { return participantName; }
    public String getAvatarColor() { return avatarColor; }
    public String getLastSeen() { return lastSeen; }
    public void setLastSeen(String lastSeen) { this.lastSeen = lastSeen; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public List<Message> getMessages() { return messages; }
}

/**
 * Message model
 */
class Message {
    private String content;
    private boolean sent;
    private LocalDateTime timestamp;

    public Message(String content, boolean sent, LocalDateTime timestamp) {
        this.content = content;
        this.sent = sent;
        this.timestamp = timestamp;
    }

    public String getContent() { return content; }
    public boolean isSent() { return sent; }
    public String getFormattedTime() {
        return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    public LocalDateTime getTimestamp() { return timestamp; }
}