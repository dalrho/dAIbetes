package reviewResults;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewResultsController {

    @FXML private ImageView reportImageView;
    @FXML private Label lblFinalCriticality;
    @FXML private Label lblReasoning;
    @FXML private Label lblNotes;

    // Pathological Findings Labels
    @FXML private Label valMA, valHem, valExu, valCWS, valME, valVB, valDRGrade, valDME;
    @FXML private FlowPane recommendationsContainer;

    private ReportData currentReportData;

    /**
     * Injects data from the generator screen into this review screen
     */
    public void setReportData(ReportData data) {
        this.currentReportData = data;

        // 1. Set Image
        if (data.getScanImage() != null) {
            reportImageView.setImage(data.getScanImage());
        }

        // 2. Set Assessments
        lblFinalCriticality.setText(data.getCriticality() != null ? data.getCriticality().toUpperCase() : "PENDING");
        lblReasoning.setText(data.getCriticalityReasoning());
        lblNotes.setText(data.getClinicalNotes());

        // 3. Set Findings
        valMA.setText(data.getMicroaneurysms());
        valHem.setText(data.getHemorrhages());
        valExu.setText(data.getHardExudates());
        valCWS.setText(data.getCottonWoolSpots());
        valME.setText(data.getMacularEdema());
        valVB.setText(data.getVenousBeading());
        valDRGrade.setText(data.getDrGrade());
        valDME.setText(data.getDmeGrade());

        // 4. Set Recommendation Tags
        recommendationsContainer.getChildren().clear();
        for (String rec : data.getRecommendations()) {
            Label tag = new Label(rec);
            tag.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; " +
                    "-fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold;");
            recommendationsContainer.getChildren().add(tag);
        }
    }

    @FXML
    private void handleExportPDF() {
        if (currentReportData == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No data available to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Professional Report");
        fileChooser.setInitialFileName("Medical_Report_" + currentReportData.getPatientName().replace(" ", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(lblNotes.getScene().getWindow());

        if (file != null) {
            try {
                generatePDF(file.getAbsolutePath());
                showAlert(Alert.AlertType.INFORMATION, "Success", "PDF Report generated successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Export Failed", "Could not generate PDF: " + e.getMessage());
            }
        }
    }

    private void generatePDF(String path) throws Exception {
        PdfWriter writer = new PdfWriter(path);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // --- Header ---
        document.add(new Paragraph("dAIbetes DIAGNOSTIC REPORT")
                .setFontSize(24).setBold().setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.DARK_GRAY));
        document.add(new Paragraph("Official Ophthalmological Assessment")
                .setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        // --- Patient Meta Table ---
        Table metaTable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 20, 30})).useAllAvailableWidth();
        metaTable.addCell(new Cell().add(new Paragraph("Patient:")).setBold().setBorder(Border.NO_BORDER));
        metaTable.addCell(new Cell().add(new Paragraph(currentReportData.getPatientName())).setBorder(Border.NO_BORDER));
        metaTable.addCell(new Cell().add(new Paragraph("Date:")).setBold().setBorder(Border.NO_BORDER));
        metaTable.addCell(new Cell().add(new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))).setBorder(Border.NO_BORDER));
        document.add(metaTable.setMarginBottom(20));

        // --- Criticality Assessment Box ---
        document.add(new Paragraph("EXECUTIVE SUMMARY").setBold());
        Table summaryBox = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        Cell summaryCell = new Cell().setPadding(10).setBackgroundColor(ColorConstants.LIGHT_GRAY);
        summaryCell.add(new Paragraph("FINAL CRITICALITY: " + currentReportData.getCriticality().toUpperCase()).setBold().setFontSize(14));
        summaryCell.add(new Paragraph("Reasoning: " + currentReportData.getCriticalityReasoning()).setItalic());
        summaryBox.addCell(summaryCell);
        document.add(summaryBox.setMarginBottom(20));

        // --- Findings Table ---
        document.add(new Paragraph("PATHOLOGICAL FINDINGS").setBold());
        Table findings = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        addPdfRow(findings, "Microaneurysms", currentReportData.getMicroaneurysms());
        addPdfRow(findings, "Retinal Hemorrhages", currentReportData.getHemorrhages());
        addPdfRow(findings, "Hard Exudates", currentReportData.getHardExudates());
        addPdfRow(findings, "Cotton-Wool Spots", currentReportData.getCottonWoolSpots());
        addPdfRow(findings, "Macular Edema", currentReportData.getMacularEdema());
        addPdfRow(findings, "Venous Beading", currentReportData.getVenousBeading());
        addPdfRow(findings, "Final DR Grade", currentReportData.getDrGrade());
        addPdfRow(findings, "DME Grade", currentReportData.getDmeGrade());
        document.add(findings.setMarginBottom(20));

        // --- Recommendations ---
        document.add(new Paragraph("RECOMMENDATIONS").setBold());
        String recs = String.join(", ", currentReportData.getRecommendations());
        document.add(new Paragraph(recs.isEmpty() ? "No specific recommendations." : recs));

        // --- Notes ---
        document.add(new Paragraph("\nCLINICAL NOTES").setBold().setFontSize(10));
        document.add(new Paragraph(currentReportData.getClinicalNotes()).setFontSize(10));

        // --- Signature ---
        document.add(new Paragraph("\n\n\n__________________________").setTextAlignment(TextAlignment.RIGHT));
        document.add(new Paragraph("Physician Signature").setTextAlignment(TextAlignment.RIGHT).setFontSize(10));

        document.close();
    }

    private void addPdfRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "Not Observed")));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // TODO: navigate
        Scene scene = register.sceneLoader.load(
                "doctorDashboard",
                "doctor-dashboard.fxml",
                null
        );

        if (scene == null) {
            System.out.println("Failed to load doctors screen");
            return;
        }

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(scene);
    }

    @FXML
    private void handleEditReport(ActionEvent event) {
        navigate(event, "generateReport", "generate-report.fxml");
    }

    private void navigate(ActionEvent event, String name, String fxml) {
        // Assuming 'register' is globally accessible or part of your framework
        // If 'register' is not accessible, you'll need to pass your SceneLoader reference here
        try {
            // Placeholder logic based on your previous snippet
            // Scene scene = register.sceneLoader.load(name, fxml, null);
            // Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            // stage.setScene(scene);
            System.out.println("Navigating to: " + fxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}