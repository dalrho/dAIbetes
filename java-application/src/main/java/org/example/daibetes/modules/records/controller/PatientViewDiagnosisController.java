package org.example.daibetes.modules.records.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.PatientDashboardDAO;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;
import org.example.daibetes.shared.service.PDFService;
import org.example.daibetes.shared.ui.SceneLoader;

import java.io.File;
import java.util.List;

public class PatientViewDiagnosisController {

    @FXML private ImageView reportImageView;
    @FXML private Label patientNameLabel;
    @FXML private Label lblFinalCriticality, lblReasoning, lblNotes;

    // The 12 Pathological Labels
    @FXML private Label valMA, valHem, valExu, valCWS, valME, valVB,
            valIRMA, valNV, valVH, valRD, valDRGrade, valDME;

    @FXML private FlowPane recommendationsContainer;

    private ReportData currentReportData;
    private final PatientDashboardDAO dao = new PatientDashboardDAO();

    @FXML
    public void initialize() {
        int selectedId = AppContext.getInstance().getSelectedReportId();
        if (selectedId > 0) {
            // Apply the same styling that makes the Doctor's end look clear
            styleClinicalValues();
            loadReportData(selectedId);
        }
    }

    private void loadReportData(int reportId) {
        this.currentReportData = dao.getFullReportDetails(reportId);
        if (currentReportData == null) return;

        // 1. Identity & Image
        patientNameLabel.setText(nullToDash(currentReportData.getPatientName()));
        if (currentReportData.getScanImage() != null) {
            reportImageView.setImage(currentReportData.getScanImage());
        }

        // 2. Technical Findings (The 1-10 labels)
        valMA.setText(currentReportData.getMicroaneurysms());    // 1
        valHem.setText(currentReportData.getHemorrhages());      // 2
        valExu.setText(currentReportData.getHardExudates());     // 3
        valCWS.setText(currentReportData.getCottonWoolSpots());  // 4
        valME.setText(currentReportData.getMacularEdema());      // 5
        valVB.setText(currentReportData.getVenousBeading());     // 6
        valIRMA.setText(currentReportData.getIrma());            // 7
        valNV.setText(currentReportData.getNeovascularization());// 8
        valVH.setText(currentReportData.getVitreousHemorrhage());// 9
        valRD.setText(currentReportData.getRetinalDetachment()); // 10

        // ==========================================
        // 3. CLINICAL PARSING (THE FIX)
        // ==========================================

        // --- PARSE FINAL DR GRADE (Based on Findings 1 and 8) ---
        String rawF8 = currentReportData.getNeovascularization(); // Neovascularization
        String rawF1 = currentReportData.getMicroaneurysms();      // Microaneurysms
        String parsedDR;

        if (rawF8.equals("Disc") || rawF8.equals("Elsewhere")) {
            parsedDR = "Proliferative DR (PDR)";
        } else if (rawF1.equals("Severe")) {
            parsedDR = "Severe NPDR (Grade 3)";
        } else if (rawF1.equals("Moderate")) {
            parsedDR = "Moderate NPDR (Grade 2)";
        } else if (rawF1.equals("Mild")) {
            parsedDR = "Mild NPDR (Grade 1)";
        } else {
            parsedDR = "No DR (Grade 0)";
        }
        valDRGrade.setText(parsedDR);

        // --- PARSE MACULAR EDEMA DME (Based on Finding 5: Macular Edema) ---
        String rawF5 = currentReportData.getMacularEdema();
        String parsedDME;

        if (rawF5.equals("CSME")) {
            parsedDME = "Severe";
        } else if (rawF5.equals("Suspect")) {
            parsedDME = "Moderate";
        } else if (rawF5.equals("Present") || rawF5.equals("Macular")) {
            parsedDME = "Mild";
        } else {
            parsedDME = "No DME";
        }
        valDME.setText(parsedDME);

        // 4. Criticality, Notes, and Styling
        lblFinalCriticality.setText(nullToDash(currentReportData.getCriticality()));
        lblReasoning.setText(nullToDash(currentReportData.getCriticalityReasoning()));
        lblNotes.setText(currentReportData.getClinicalNotes());

        loadRecommendations(currentReportData);
        styleClinicalValues();
    }
    /**
     * Ensures nulls or empty strings display a default "-" or "Absent"
     */
    private String nullToDash(String value) {
        if (value == null || value.trim().isEmpty() || value.equals("-")) {
            return "Absent";
        }
        return value;
    }

    /**
     * Loads recommendation strings into the FlowPane as styled chips
     */
    private void loadRecommendations(ReportData reportData) {
        // 1. Clear any old chips from the container
        recommendationsContainer.getChildren().clear();

        List<String> steps = reportData.getRecommendations();

        // 2. Check if the list is empty or null
        if (steps == null || steps.isEmpty()) {
            Label noSteps = new Label("No specific steps recommended at this time.");
            noSteps.setStyle("-fx-text-fill: #64748B; -fx-font-style: italic;");
            recommendationsContainer.getChildren().add(noSteps);
            return;
        }

        // 3. Loop through the strings and create the styled "chips"
        for (String stepText : steps) {
            Label chip = new Label(stepText);

            // This styling matches your blue chips in the screenshot
            chip.setStyle(
                    "-fx-background-color: #E0F2FE; " +  // Light blue background
                            "-fx-text-fill: #0369A1; " +         // Dark blue text
                            "-fx-padding: 8 15; " +              // Bubble padding
                            "-fx-background-radius: 20; " +      // Rounded corners
                            "-fx-font-weight: bold; " +          // Bold text
                            "-fx-font-size: 12px;"
            );

            recommendationsContainer.getChildren().add(chip);
        }
    }

    /**
     * Styles the dynamic labels to be blue and bold (matching the working Doctor view)
     */
    private void styleClinicalValues() {
        Label[] labels = { valMA, valHem, valExu, valCWS, valME, valVB,
                valIRMA, valNV, valVH, valRD, valDRGrade, valDME };
        for (Label label : labels) {
            if (label != null) {
                label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #3B82F6;");
            }
        }
    }

    @FXML
    private void handleBack() {
        SceneLoader.switchScene(recommendationsContainer, "org/example/daibetes/modules/records/controller", "records-screen-patient.fxml", "My Records", null);
    }

    @FXML
    private void scheduleAppointment() {
        // Logic to redirect patient to the consultation request screen
        SceneLoader.switchScene(recommendationsContainer, "org/example/daibetes/modules/patient/ui/calendar", "patient-calendar.fxml", "Schedule Appointment", null);
    }

    @FXML
    private void handleExportPDF() {
        if (currentReportData == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("Report_" + currentReportData.getPatientName().replace(" ", "_") + ".pdf");
        File file = fileChooser.showSaveDialog(lblNotes.getScene().getWindow());

        if (file != null) {
            try {
                // Get the doctor name from the data we loaded in loadReportData
                String docName = currentReportData.getDoctorName();
                if (docName == null || docName.isEmpty()) docName = "Attending Physician";

                PDFService.generateDiagnosticReport(
                        file.getAbsolutePath(),
                        currentReportData,
                        reportImageView.getImage(),
                        currentReportData.getReportId(),
                        "Dr. " + docName // Pass the doctor name here
                );

                new Alert(Alert.AlertType.INFORMATION, "Report exported successfully.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Export failed.").show();
            }
        }
    }


}