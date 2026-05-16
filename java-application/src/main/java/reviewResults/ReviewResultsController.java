package reviewResults;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    @FXML private Label lblFinalCriticality, lblReasoning, lblNotes;
    @FXML private Label valMA, valHem, valExu, valCWS, valME, valVB, valDRGrade, valDME;
    @FXML private FlowPane recommendationsContainer;

    private ReportData currentReportData;

    public void setReportData(ReportData data) {
        System.out.println("DEBUG: Setting report data for: " + data.getPatientName());
        this.currentReportData = data;

        if (data.getScanImage() != null) {
            reportImageView.setImage(data.getScanImage());
        }

        lblFinalCriticality.setText(data.getCriticality() != null ? data.getCriticality().toUpperCase() : "PENDING");
        lblReasoning.setText(data.getCriticalityReasoning());
        lblNotes.setText(data.getClinicalNotes());

        valMA.setText(data.getMicroaneurysms());
        valHem.setText(data.getHemorrhages());
        valExu.setText(data.getHardExudates());
        valCWS.setText(data.getCottonWoolSpots());
        valME.setText(data.getMacularEdema());
        valVB.setText(data.getVenousBeading());
        valDRGrade.setText(data.getDrGrade());
        valDME.setText(data.getDmeGrade());

        recommendationsContainer.getChildren().clear();
        if (data.getRecommendations() != null) {
            for (String rec : data.getRecommendations()) {
                Label tag = new Label(rec);
                tag.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; " +
                        "-fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold;");
                recommendationsContainer.getChildren().add(tag);
            }
        }
    }

    @FXML
    private void handleExportPDF() {
        System.out.println("DEBUG: Export PDF Clicked");

        if (currentReportData == null) {
            System.err.println("ERROR: No report data found to export.");
            new Alert(Alert.AlertType.WARNING, "No report data is loaded yet.").show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Diagnostic Report");
        fileChooser.setInitialFileName("Diagnostic_Report_" + currentReportData.getPatientName().replace(" ", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(lblNotes.getScene().getWindow());

        if (file != null) {
            try {
                generatePDF(file.getAbsolutePath());
                new Alert(Alert.AlertType.INFORMATION, "Report successfully exported to PDF.").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to generate PDF: " + e.getMessage()).show();
            }
        }
    }

    private void generatePDF(String dest) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        document.add(new Paragraph("dAIbetes DIAGNOSTIC REPORT")
                .setFontSize(22).setBold().setFontColor(ColorConstants.DARK_GRAY));

        document.add(new Paragraph("Patient: " + currentReportData.getPatientName())
                .setBold().setFontSize(12));
        document.add(new Paragraph("Date Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        // Criticality Section
        Table assessment = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setMarginTop(15);
        assessment.addCell(new Cell().add(new Paragraph("FINAL ASSESSMENT: " + currentReportData.getCriticality().toUpperCase()).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY).setPadding(10));
        document.add(assessment);

        // Pathological Findings (INCLUDING ALL CONTENT FROM IMAGE 2)
        document.add(new Paragraph("\nCLINICAL PATHOLOGICAL FINDINGS").setBold().setFontSize(14));
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth().setMarginTop(10);

        addTableRow(table, "1. Microaneurysms", currentReportData.getMicroaneurysms());
        addTableRow(table, "2. Retinal Hemorrhages", currentReportData.getHemorrhages());
        addTableRow(table, "3. Hard Exudates", currentReportData.getHardExudates());
        addTableRow(table, "4. Cotton-Wool Spots", currentReportData.getCottonWoolSpots());
        addTableRow(table, "5. Macular Edema", currentReportData.getMacularEdema());
        addTableRow(table, "6. Venous Beading", currentReportData.getVenousBeading());
        addTableRow(table, "7. IRMA", currentReportData.getIrma());
        addTableRow(table, "8. Neovascularization", currentReportData.getNeovascularization());
        addTableRow(table, "9. Vitreous Hemorrhage", currentReportData.getVitreousHemorrhage());
        addTableRow(table, "10. Retinal Detachment", currentReportData.getRetinalDetachment());

        // Grades
        addTableRow(table, "Final DR Grade", currentReportData.getDrGrade());
        addTableRow(table, "Macular Edema (DME) Grade", currentReportData.getDmeGrade());

        document.add(table);

        // Management
        document.add(new Paragraph("\nMANAGEMENT & RECOMMENDATIONS").setBold().setFontSize(14));
        if (currentReportData.getRecommendations() != null) {
            com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
            for (String rec : currentReportData.getRecommendations()) list.add(rec);
            document.add(list);
        }

        // Clinical Reasoning/Notes
        document.add(new Paragraph("\nDOCTOR'S CLINICAL REASONING").setBold());
        document.add(new Paragraph(currentReportData.getCriticalityReasoning()));

        document.add(new Paragraph("\nCLINICAL NOTES").setBold());
        document.add(new Paragraph(currentReportData.getClinicalNotes()));

        document.close();
        System.out.println("DEBUG: PDF Generation complete.");
    }

    private void addTableRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label)));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "-")).setBold());
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/patientsdashboard/patients-dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleScheduleFollowUp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Schedule Appointment");
        alert.setHeaderText("Appointment Booking System");
        alert.setContentText("The follow-up scheduling module is currently being integrated.");
        alert.showAndWait();
    }
}