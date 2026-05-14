package Inbox;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ConsultationRequestDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * InboxController manages the messaging interface with conversation list,
 * message display, and consultation request accept/reject functionality.
 *
 * DB integration:
 *   - loadSampleData() replaced by loadFromDatabase()
 *   - Pending requests  → Inbox tab
 *   - Responded requests → Archived tab
 *   - Accept/Reject buttons injected into inputArea when request is selected
 */
public class InboxController {

    // ── Header ────────────────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private Button    newMessageBtn;

    // ── Left panel ────────────────────────────────────────────────────────────
    @FXML private Button                    inboxTab;
    @FXML private Button                    archivedTab;
    @FXML private ListView<Conversation>    conversationList;

    // ── Right panel ───────────────────────────────────────────────────────────
    @FXML private HBox       messageHeader;
    @FXML private Circle     userAvatar;
    @FXML private Label      userName;
    @FXML private Label      lastSeenLabel;
    @FXML private VBox       emptyState;
    @FXML private ScrollPane messagesScrollPane;
    @FXML private VBox       messagesContainer;
    @FXML private HBox       inputArea;
    @FXML private TextArea   messageInput;
    @FXML private Button     sendBtn;

    // ── State ─────────────────────────────────────────────────────────────────
    private ObservableList<Conversation> conversations;
    private ObservableList<Conversation> filteredConversations;
    private Conversation                 selectedConversation;
    private boolean                      viewingInbox = true;

    private static final String[] AVATAR_COLORS = {
            "#3b82f6", "#ef4444", "#10b981", "#f59e0b", "#8b5cf6", "#ec4899"
    };
    private int avatarIndex = 0;

    private final ConsultationRequestDAO requestDAO = new ConsultationRequestDAO();
    private int doctorId;

    // =========================================================================
    // Initialize
    // =========================================================================

    @FXML
    public void initialize() {
        // Resolve doctor from session
        User currentUser = AppContext.getInstance().getCurrentUser();
        if (currentUser instanceof Doctor) {
            doctorId = ((Doctor) currentUser).getDId();
        } else {
            System.err.println("ERROR: InboxController — current user is not a Doctor.");
        }

        setupConversationList();
        setupSearch();
        setupMessageInput();
        loadFromDatabase(true); // load pending requests on open
    }

    // =========================================================================
    // DB loading
    // =========================================================================

    /**
     * Replaces loadSampleData().
     * Fetches real consultation requests and maps them to Conversation objects.
     *
     * @param pending true = inbox (responded_on IS NULL),
     *                false = archived (responded_on IS NOT NULL)
     */
    private void loadFromDatabase(boolean pending) {
        conversations.clear();

        List<String[]> rows = pending
                ? requestDAO.getPendingRequests(doctorId)
                : requestDAO.getRespondedRequests(doctorId);

        // row: [0]=request_id [1]=test_id [2]=patient_name
        //      [3]=requested_on [4]=p_id
        for (String[] row : rows) {
            String color = getAvatarColor();
            Conversation conv = new Conversation(row[2], color, row[3]);

            conv.setRequestId(Integer.parseInt(row[0]));
            conv.setTestId(Integer.parseInt(row[1]));
            conv.setPatientId(Integer.parseInt(row[4]));
            conv.setResponded(!pending);

            // Build the initial message bubble from request details
            String preview = pending
                    ? "Requesting consultation. Test ID: " + row[1]
                    : "Request responded on: " + row[3];

            conv.setLastMessage(preview);
            conv.addMessage(new Message(preview, false, LocalDateTime.now()));

            conversations.add(conv);
        }

        filteredConversations.setAll(conversations);

        if (conversations.isEmpty()) {
            String hint = pending ? "No pending requests." : "No archived requests.";
            Conversation empty = new Conversation(hint, "#9ca3af", "");
            filteredConversations.add(empty);
        }
    }

    // =========================================================================
    // Conversation list setup
    // =========================================================================

    private void setupConversationList() {
        conversations         = FXCollections.observableArrayList();
        filteredConversations = FXCollections.observableArrayList();

        conversationList.setItems(filteredConversations);
        conversationList.setCellFactory(param -> new ConversationCell());

        conversationList.setOnMouseClicked(event -> {
            if (!conversationList.getSelectionModel().isEmpty()) {
                selectedConversation =
                        conversationList.getSelectionModel().getSelectedItem();
                // Don't open detail for the "empty" placeholder row
                if (selectedConversation.getRequestId() != 0) {
                    displayConversation(selectedConversation);
                }
            }
        });
    }

    // =========================================================================
    // Search
    // =========================================================================

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) ->
                filterConversations(newVal.toLowerCase()));
    }

    private void filterConversations(String query) {
        if (query.isEmpty()) {
            filteredConversations.setAll(conversations);
        } else {
            filteredConversations.clear();
            conversations.stream()
                    .filter(c -> c.getParticipantName().toLowerCase().contains(query))
                    .forEach(filteredConversations::add);
        }
    }

    // =========================================================================
    // Message input setup
    // =========================================================================

    private void setupMessageInput() {
        messageInput.setWrapText(true);
        messageInput.setPrefRowCount(3);
    }

    // =========================================================================
    // Display conversation
    // =========================================================================

    private void displayConversation(Conversation conversation) {
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        messagesScrollPane.setVisible(true);
        messagesScrollPane.setManaged(true);
        messageHeader.setVisible(true);
        messageHeader.setManaged(true);
        inputArea.setVisible(true);
        inputArea.setManaged(true);

        userName.setText(conversation.getParticipantName());
        lastSeenLabel.setText("Requested: " + conversation.getLastSeen());
        userAvatar.setStyle("-fx-fill: " + conversation.getAvatarColor() + ";");

        messagesContainer.getChildren().clear();
        for (Message message : conversation.getMessages()) {
            addMessageToUI(message);
        }

        Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));
        messageInput.clear();

        // Inject accept/reject for pending requests; hide for archived
        refreshActionArea(conversation);
    }

    // =========================================================================
    // Accept / Reject action area
    // =========================================================================

    /**
     * Replaces the default send button area with Accept/Reject buttons
     * when viewing a pending request, or hides actions for archived ones.
     */
    private void refreshActionArea(Conversation conversation) {
        // Remove any previously injected action buttons
        inputArea.getChildren().removeIf(n ->
                n instanceof Button &&
                        ("ACCEPT".equals(((Button) n).getText()) ||
                                "REJECT".equals(((Button) n).getText())));

        // Hide the default message input and send for request conversations
        messageInput.setVisible(false);
        messageInput.setManaged(false);
        sendBtn.setVisible(false);
        sendBtn.setManaged(false);

        if (conversation.isResponded()) {
            // Archived — no actions available
            Label respondedLbl = new Label("✓  This request has already been responded to.");
            respondedLbl.setStyle(
                    "-fx-text-fill: #6b7280; -fx-font-size: 13px; -fx-padding: 8 0;");
            inputArea.getChildren().add(respondedLbl);
            return;
        }

        // Pending — show Reject + Accept
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button rejectBtn = new Button("REJECT");
        rejectBtn.setStyle(
                "-fx-padding: 10 28; -fx-font-size: 13px; -fx-font-weight: bold; " +
                        "-fx-text-fill: #C0392B; -fx-background-color: transparent; " +
                        "-fx-border-color: #C0392B; -fx-border-radius: 8; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;");

        Button acceptBtn = new Button("ACCEPT");
        acceptBtn.setStyle(
                "-fx-padding: 10 28; -fx-font-size: 13px; -fx-font-weight: bold; " +
                        "-fx-text-fill: white; -fx-background-color: #1f2937; " +
                        "-fx-background-radius: 8; -fx-cursor: hand;");

        rejectBtn.setOnAction(e -> handleReject(conversation, rejectBtn, acceptBtn));
        acceptBtn.setOnAction(e -> handleAccept(conversation, rejectBtn, acceptBtn));

        inputArea.getChildren().addAll(spacer, rejectBtn, acceptBtn);
    }

    private void handleAccept(Conversation conversation,
                              Button rejectBtn, Button acceptBtn) {
        boolean success = requestDAO.acceptRequest(conversation.getRequestId());
        if (success) {
            conversation.setResponded(true);
            conversation.setAccepted(true);
            conversation.setLastMessage("✓ Request accepted.");
            conversation.addMessage(
                    new Message("You accepted this consultation request.",
                            true, LocalDateTime.now()));

            filteredConversations.remove(conversation);
            conversations.remove(conversation);
            conversationList.refresh();

            // Show confirmation in message pane then clear selection
            addMessageToUI(conversation.getMessages()
                    .get(conversation.getMessages().size() - 1));
            Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));

            disableActionButtons(rejectBtn, acceptBtn, "Accepted");
        } else {
            System.err.println("Failed to accept request ID: "
                    + conversation.getRequestId());
        }
    }

    private void handleReject(Conversation conversation,
                              Button rejectBtn, Button acceptBtn) {
        boolean success = requestDAO.rejectRequest(conversation.getRequestId());
        if (success) {
            conversation.setResponded(true);
            conversation.setAccepted(false);
            conversation.setLastMessage("✗ Request rejected.");
            conversation.addMessage(
                    new Message("You rejected this consultation request.",
                            true, LocalDateTime.now()));

            filteredConversations.remove(conversation);
            conversations.remove(conversation);
            conversationList.refresh();

            addMessageToUI(conversation.getMessages()
                    .get(conversation.getMessages().size() - 1));
            Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));

            disableActionButtons(rejectBtn, acceptBtn, "Rejected");
        } else {
            System.err.println("Failed to reject request ID: "
                    + conversation.getRequestId());
        }
    }

    /**
     * Disables both buttons and shows a status label after action.
     */
    private void disableActionButtons(Button rejectBtn, Button acceptBtn,
                                      String status) {
        rejectBtn.setDisable(true);
        acceptBtn.setDisable(true);

        Label done = new Label("✓  " + status);
        done.setStyle("-fx-text-fill: #065f46; -fx-font-size: 13px; " +
                "-fx-font-weight: bold; -fx-padding: 8 0;");
        inputArea.getChildren().add(done);
    }

    // =========================================================================
    // Add message bubble to UI
    // =========================================================================

    private void addMessageToUI(Message message) {
        HBox messageBox = new HBox();
        messageBox.setSpacing(12);
        messageBox.setAlignment(message.isSent() ? Pos.TOP_RIGHT : Pos.TOP_LEFT);

        Circle avatar = new Circle(18);
        avatar.setStyle("-fx-fill: " +
                (message.isSent() ? "#3b82f6" : getAvatarColor()) + ";");

        VBox contentBox = new VBox(4);

        Label senderLabel = new Label(
                message.isSent() ? "You" : selectedConversation.getParticipantName());
        senderLabel.setStyle(
                "-fx-font-size: 11; -fx-text-fill: #9ca3af; -fx-font-weight: bold;");

        Label messageLabel = new Label(message.getContent());
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setStyle(
                "-fx-padding: 10; -fx-font-size: 13; -fx-text-fill: " +
                        (message.isSent() ? "#ffffff" : "#1f2937") +
                        "; -fx-background-color: " +
                        (message.isSent() ? "#3b82f6" : "#f3f4f6") +
                        "; -fx-background-radius: 12;");

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

    // =========================================================================
    // Tab handlers
    // =========================================================================

    @FXML
    private void handleInboxTab() {
        viewingInbox = true;
        inboxTab.setStyle(
                "-fx-padding: 8 12; -fx-font-size: 12; -fx-font-weight: bold; " +
                        "-fx-text-fill: #3b82f6; -fx-background-color: transparent; " +
                        "-fx-border-color: #3b82f6; -fx-border-width: 0 0 2 0;");
        archivedTab.setStyle(
                "-fx-padding: 8 12; -fx-font-size: 12; -fx-font-weight: 600; " +
                        "-fx-text-fill: #6b7280; -fx-background-color: transparent; " +
                        "-fx-border-width: 0;");
        loadFromDatabase(true);
        clearDetailPane();
    }

    @FXML
    private void handleArchivedTab() {
        viewingInbox = false;
        archivedTab.setStyle(
                "-fx-padding: 8 12; -fx-font-size: 12; -fx-font-weight: bold; " +
                        "-fx-text-fill: #3b82f6; -fx-background-color: transparent; " +
                        "-fx-border-color: #3b82f6; -fx-border-width: 0 0 2 0;");
        inboxTab.setStyle(
                "-fx-padding: 8 12; -fx-font-size: 12; -fx-font-weight: 600; " +
                        "-fx-text-fill: #6b7280; -fx-background-color: transparent; " +
                        "-fx-border-width: 0;");
        loadFromDatabase(false);
        clearDetailPane();
    }

    // =========================================================================
    // New message — kept from original, opens dialog
    // =========================================================================

    @FXML
    private void handleNewMessage() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Message");
        dialog.setHeaderText("Start a new conversation");
        dialog.setContentText("Recipient name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String recipientName = result.get().trim();
            Conversation newConv = new Conversation(
                    recipientName, getAvatarColor(), "Now");
            conversations.add(newConv);
            filteredConversations.add(0, newConv);
            conversationList.getSelectionModel().select(0);
            displayConversation(newConv);
        }
    }

    // =========================================================================
    // Send message — kept for future free-form messaging
    // =========================================================================

    @FXML
    private void handleSendMessage() {
        if (selectedConversation == null) return;
        String content = messageInput.getText().trim();
        if (content.isEmpty()) return;

        Message message = new Message(content, true, LocalDateTime.now());
        selectedConversation.addMessage(message);
        addMessageToUI(message);
        selectedConversation.setLastMessage(content);
        conversationList.refresh();
        messageInput.clear();

        Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private void clearDetailPane() {
        messageHeader.setVisible(false);
        messageHeader.setManaged(false);
        emptyState.setVisible(true);
        emptyState.setManaged(true);
        messagesScrollPane.setVisible(false);
        messagesScrollPane.setManaged(false);
        inputArea.setVisible(false);
        inputArea.setManaged(false);
        messagesContainer.getChildren().clear();
        selectedConversation = null;
    }

    private String getAvatarColor() {
        return AVATAR_COLORS[avatarIndex++ % AVATAR_COLORS.length];
    }

    // =========================================================================
    // Custom cell renderer — kept from original with minor style alignment
    // =========================================================================

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
            cellContent.setPadding(new Insets(12));
            cellContent.setStyle(
                    "-fx-border-color: #f3f4f6; -fx-border-width: 0 0 1 0;");

            Circle avatar = new Circle(28);
            avatar.setStyle("-fx-fill: " + conversation.getAvatarColor() + ";");

            VBox infoBox = new VBox(4);
            Label nameLabel = new Label(conversation.getParticipantName());
            nameLabel.setStyle(
                    "-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

            Label messageLabel = new Label(conversation.getLastMessage());
            messageLabel.setStyle(
                    "-fx-font-size: 12; -fx-text-fill: #6b7280;");
            messageLabel.setMaxWidth(200);
            messageLabel.setWrapText(true);

            infoBox.getChildren().addAll(nameLabel, messageLabel);

            VBox timeBox = new VBox();
            timeBox.setAlignment(Pos.TOP_RIGHT);

            Label timeLabel = new Label(conversation.getLastSeen());
            timeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #9ca3af;");

            // Status badge — Pending (amber) or Responded (green)
            Label badge = new Label(
                    conversation.isResponded() ? "Responded" : "Pending");
            badge.setStyle(conversation.isResponded()
                    ? "-fx-background-color: #d1fae5; -fx-text-fill: #065f46; " +
                    "-fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 8;"
                    : "-fx-background-color: #fef3c7; -fx-text-fill: #d97706; " +
                    "-fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 8;");

            timeBox.getChildren().addAll(timeLabel, badge);

            cellContent.getChildren().addAll(avatar, infoBox, new Region(), timeBox);
            HBox.setHgrow(infoBox, Priority.ALWAYS);

            setGraphic(cellContent);
            setStyle("-fx-padding: 0; -fx-background-color: " +
                    (isSelected() ? "#f0f4ff" : "#ffffff") + ";");
        }
    }
}

/**
 * Conversation model
 */


