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
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static javafx.application.Application.launch;

public class ReviewResultsController {

    @FXML private ImageView reportImageView;
    @FXML private Label lblFinalCriticality, lblReasoning, lblNotes;
    @FXML private Label valMA, valHem, valExu, valCWS, valME, valVB, valDRGrade, valDME;
    @FXML private FlowPane recommendationsContainer;

    private ReportData currentReportData;

    /**
     * Populates the view with data passed from the selection screen.
     */
    public void setReportData(ReportData data) {
        this.currentReportData = data;

        // Populate Image
        if (data.getScanImage() != null) {
            reportImageView.setImage(data.getScanImage());
        }

        // Assessment
        lblFinalCriticality.setText(data.getCriticality() != null ? data.getCriticality().toUpperCase() : "PENDING");
        lblReasoning.setText(data.getCriticalityReasoning());
        lblNotes.setText(data.getClinicalNotes());

        // Findings
        valMA.setText(data.getMicroaneurysms());
        valHem.setText(data.getHemorrhages());
        valExu.setText(data.getHardExudates());
        valCWS.setText(data.getCottonWoolSpots());
        valME.setText(data.getMacularEdema());
        valVB.setText(data.getVenousBeading());
        valDRGrade.setText(data.getDrGrade());
        valDME.setText(data.getDmeGrade());

        // Recommendations (styled as tags)
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
    private void handleScheduleFollowUp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Schedule Appointment");
        alert.setHeaderText("Appointment Booking System");
        alert.setContentText("The follow-up scheduling module is currently being integrated. Please contact your physician's office directly or visit our online booking portal.");
        alert.showAndWait();
    }

    @FXML
    private void handleExportPDF() {
        if (currentReportData == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Diagnostic Report");
        fileChooser.setInitialFileName("My_Report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf");
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
    @FXML
    private void handleEdit() {
        System.out.println("Editing..");
    }

    private void generatePDF(String dest) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("dAIbetes DIAGNOSTIC REPORT")
                .setFontSize(22).setBold().setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Official Patient Record Summary")
                .setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        document.add(new Paragraph("Patient: " + currentReportData.getPatientName()).setBold());
        document.add(new Paragraph("Date Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        // Criticality Box
        Table assessment = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setMarginTop(15);
        assessment.addCell(new Cell().add(new Paragraph("FINAL ASSESSMENT: " + currentReportData.getCriticality().toUpperCase()).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY).setPadding(10));
        document.add(assessment);

        // Pathological Findings Table
        Table table = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth().setMarginTop(15);
        table.addCell(new Cell().add(new Paragraph("Observation").setBold()));
        table.addCell(new Cell().add(new Paragraph("Value").setBold()));

        table.addCell(new Cell().add(new Paragraph("DR Grade")));
        table.addCell(new Cell().add(new Paragraph(currentReportData.getDrGrade())));

        table.addCell(new Cell().add(new Paragraph("Macular Edema Status")));
        table.addCell(new Cell().add(new Paragraph(currentReportData.getDmeGrade())));

        document.add(table);
        document.add(new Paragraph("\nDoctor's Clinical Notes:").setBold());
        document.add(new Paragraph(currentReportData.getCriticalityReasoning()));

        document.close();
    }

    @FXML
    private void handleBack() {
        // Logic to return to the records/home screen
        System.out.println("Returning to home...");
    }
}
