package results;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
public class resultController {

    @FXML
    private TextArea notesArea;

    @FXML
    private Label reportStatus;

    @FXML
    public void handleGenerateReport() {
        reportStatus.setText("Report Generated (Prototype)");
    }

    @FXML
    public void handleFinalize() {
        reportStatus.setText("Marked as Finalized");
    }

    @FXML
    public void handlePublish() {
        reportStatus.setText("Published to Patient Portal");
    }
}