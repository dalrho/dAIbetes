package org.example.daibetes.modules.doctor.ui.review.controller;

// ... (existing imports)
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ReportDataDAO;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.service.PDFService;
import org.example.daibetes.shared.ui.SceneLoader;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DoctorViewDiagnosisController {

    private final SceneLoader sceneLoader = new SceneLoader();

    @FXML private ImageView reportImageView;
    @FXML private Label patientNameLabel;
    @FXML private Label lblFinalCriticality;
    @FXML private Label lblReasoning;
    @FXML private Label lblNotes;
    @FXML private Label valMA, valHem, valExu, valCWS, valME, valVB, valIRMA, valNV, valVH, valRD, valDRGrade, valDME;
    @FXML private FlowPane recommendationsContainer;
    @FXML private Button editDiagnosisBtn;

    // ADDED: Store report data globally in the controller for export
    private ReportData currentReportData;

    @FXML
    public void initialize() {
        int reportId = AppContext.getInstance().getSelectedReportId();
        if (reportId == 0) {
            showAlert("Missing Report", "No report was selected.");
            return;
        }
        styleClinicalValues();
        loadReportData(reportId);
    }

    private void loadReportData(int reportId) {
        ReportDataDAO dao = new ReportDataDAO();
        this.currentReportData = dao.getReportDataByReportId(reportId);

        if (currentReportData == null) {
            showAlert("Load Failed", "Could not load the selected report.");
            return;
        }

        // Set Name
        String name = currentReportData.getPatientName();
        if (name == null || name.isBlank()) {
            name = AppContext.getInstance().getSelectedRecordsPatientName();
        }
        patientNameLabel.setText(nullToDash(name));

        if (currentReportData.getScanImage() != null) {
            reportImageView.setImage(currentReportData.getScanImage());
        }

        lblFinalCriticality.setText(nullToDash(currentReportData.getCriticality()));
        lblReasoning.setText(nullToDash(currentReportData.getCriticalityReasoning()));
        valMA.setText(nullToDash(currentReportData.getMicroaneurysms()));
        valHem.setText(nullToDash(currentReportData.getHemorrhages()));
        valExu.setText(nullToDash(currentReportData.getHardExudates()));
        valCWS.setText(nullToDash(currentReportData.getCottonWoolSpots()));
        valME.setText(nullToDash(currentReportData.getMacularEdema()));
        valVB.setText(nullToDash(currentReportData.getVenousBeading()));
        valIRMA.setText(nullToDash(currentReportData.getIrma()));
        valNV.setText(nullToDash(currentReportData.getNeovascularization()));
        valVH.setText(nullToDash(currentReportData.getVitreousHemorrhage()));
        valRD.setText(nullToDash(currentReportData.getRetinalDetachment()));
        valDRGrade.setText(nullToDash(currentReportData.getDrGrade()));
        valDME.setText(nullToDash(currentReportData.getDmeGrade()));
        lblNotes.setText(nullToDash(currentReportData.getClinicalNotes()));

        loadRecommendations(currentReportData);
    }

    @FXML
    private void handleExportPDF() {
        if (currentReportData == null) {
            showAlert("Export Error", "Report data is not loaded.");
            return;
        }

        // 1. Sync the Patient Name (same logic as the UI label)
        String resolvedName = currentReportData.getPatientName();
        if (resolvedName == null || resolvedName.isBlank()) {
            resolvedName = AppContext.getInstance().getSelectedRecordsPatientName();
        }
        if (resolvedName == null || resolvedName.isBlank()) {
            resolvedName = AppContext.getInstance().getSelectedPatientName();
        }
        currentReportData.setPatientName(nullToDash(resolvedName));

        // 2. Setup FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Diagnostic Report");
        String safeFileName = currentReportData.getPatientName().replace(" ", "_");
        fileChooser.setInitialFileName("Diagnostic_Report_" + safeFileName + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(lblNotes.getScene().getWindow());

        if (file != null) {
            try {
                // 3. Resolve Doctor Name from Session
                String doctorName = "Attending Physician";
                if (AppContext.getInstance().getCurrentUser() instanceof Doctor d) {
                    doctorName = "Dr. " + d.getDoctorName();
                }

                // 4. Generate PDF using the formal Service
                PDFService.generateDiagnosticReport(
                        file.getAbsolutePath(),
                        currentReportData,
                        reportImageView.getImage(),
                        AppContext.getInstance().getSelectedReportId(),
                        doctorName
                );

                new Alert(Alert.AlertType.INFORMATION, "Professional report exported successfully.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "PDF Generation failed: " + e.getMessage()).show();
            }
        }
    }

    private void addTableRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label)));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "-")).setBold());
    }

    private void loadRecommendations(ReportData reportData) {
        recommendationsContainer.getChildren().clear();
        if (reportData.getRecommendations() != null) {
            for (String recommendation : reportData.getRecommendations()) {
                addRecommendationChip(recommendation);
            }
        }
        if (recommendationsContainer.getChildren().isEmpty()) {
            addRecommendationChip("No recommendations selected");
        }
    }

    private void addRecommendationChip(String text) {
        Label chip = new Label(text);
        chip.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #075985; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 20; -fx-font-size: 12px;");
        recommendationsContainer.getChildren().add(chip);
    }

    private String nullToDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    @FXML
    private void handleEditDiagnosis(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = sceneLoader.load("org/example/daibetes/modules/doctor/ui/report", "edit-generate-report.fxml", null);
        if (scene != null) {
            stage.setScene(scene);
            stage.setTitle("Edit Diagnosis");
            stage.show();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Scene scene = sceneLoader.load("org/example/daibetes/modules/doctor/ui/review", "records-screen.fxml", null);
        if (scene != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Patient Records");
            stage.show();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void styleClinicalValues() {
        Label[] labels = { valMA, valHem, valExu, valCWS, valME, valVB, valIRMA, valNV, valVH, valRD, valDRGrade, valDME };
        for (Label label : labels) {
            if (label != null) {
                label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #3B82F6;");
            }
        }
    }
}