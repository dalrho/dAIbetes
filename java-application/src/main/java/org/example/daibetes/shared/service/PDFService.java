package org.example.daibetes.shared.service;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.embed.swing.SwingFXUtils;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PDFService {

    public static void generateDiagnosticReport(String dest, ReportData data, javafx.scene.image.Image retinalImage, int reportId, String doctorName) throws Exception {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Styling Colors
        DeviceRgb primarySlate = new DeviceRgb(30, 41, 59);
        DeviceRgb accentBlue = new DeviceRgb(59, 130, 246);
        DeviceRgb tableHeaderGray = new DeviceRgb(241, 245, 249);

        // 1. Header & Branding
        Table header = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
        header.addCell(new Cell().add(new Paragraph("dAIbetes").setFontSize(24).setBold().setFontColor(primarySlate)).setBorder(Border.NO_BORDER));
        header.addCell(new Cell().add(new Paragraph("DIAGNOSTIC REPORT").setFontSize(10).setBold().setFontColor(accentBlue).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        document.add(header);
        document.add(new Paragraph("Advanced Retinal Screening System").setFontSize(9).setItalic().setMarginBottom(15));

        // 2. Patient & Doctor Info Grid
        Table infoGrid = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth().setMarginBottom(20);

        Cell pInfo = new Cell().add(new Paragraph("PATIENT DETAILS").setBold().setFontSize(9).setFontColor(accentBlue));
        pInfo.add(new Paragraph("Name: " + data.getPatientName()).setFontSize(11));
        pInfo.add(new Paragraph("Report ID: #" + reportId).setFontSize(10));
        pInfo.add(new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).setFontSize(10));
        infoGrid.addCell(pInfo.setBorder(Border.NO_BORDER));

        Cell dInfo = new Cell().add(new Paragraph("CLINICAL DETAILS").setBold().setFontSize(9).setFontColor(accentBlue));
        dInfo.add(new Paragraph("Practitioner: " + doctorName).setFontSize(11));
        dInfo.add(new Paragraph("Status: Verified Electronic Record").setFontSize(10));
        infoGrid.addCell(dInfo.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
        document.add(infoGrid);

        // 3. Retinal Image
        if (retinalImage != null) {
            BufferedImage bImage = SwingFXUtils.fromFXImage(retinalImage, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "png", baos);
            Image pdfImg = new Image(ImageDataFactory.create(baos.toByteArray()));
            document.add(new Paragraph("REFERENCE SCAN").setBold().setFontSize(9).setFontColor(primarySlate));
            document.add(pdfImg.setAutoScale(true).setMaxHeight(220).setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginBottom(20));
        }

        // 4. Criticality Assessment (The formal Dark Bar)
        String crit = data.getCriticality() != null ? data.getCriticality().toUpperCase() : "PENDING";
        Table critTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth().setMarginBottom(20);
        critTable.addCell(new Cell().add(new Paragraph("FINAL ASSESSMENT: " + crit).setBold().setFontColor(ColorConstants.WHITE).setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(primarySlate).setPadding(6));
        document.add(critTable);

        // 5. Clinical Findings Table
        document.add(new Paragraph("CLINICAL OBSERVATIONS").setBold().setFontSize(9).setMarginBottom(5));
        Table table = new Table(UnitValue.createPercentArray(new float[]{60, 40})).useAllAvailableWidth();
        table.addHeaderCell(new Cell().add(new Paragraph("Feature").setBold()).setBackgroundColor(tableHeaderGray));
        table.addHeaderCell(new Cell().add(new Paragraph("Result").setBold()).setBackgroundColor(tableHeaderGray));

        addZebraRow(table, "Microaneurysms", data.getMicroaneurysms());
        addZebraRow(table, "Retinal Hemorrhages", data.getHemorrhages());
        addZebraRow(table, "Hard Exudates", data.getHardExudates());
        addZebraRow(table, "Cotton-Wool Spots", data.getCottonWoolSpots());
        addZebraRow(table, "Macular Edema (DME)", data.getDmeGrade());
        addZebraRow(table, "Diabetic Retinopathy Grade", data.getDrGrade());
        document.add(table.setMarginBottom(20));

        // 6. Management & Notes
        document.add(new Paragraph("MANAGEMENT & RECOMMENDATIONS").setBold().setFontSize(9).setFontColor(accentBlue));
        if (data.getRecommendations() != null) {
            List list = new List().setFontSize(10);
            for (String rec : data.getRecommendations()) list.add(rec);
            document.add(list);
        }

        document.add(new Paragraph("\nCLINICAL NOTES / REASONING").setBold().setFontSize(9).setFontColor(accentBlue));
        document.add(new Paragraph(data.getCriticalityReasoning() + "\n" + data.getClinicalNotes()).setFontSize(10).setItalic());

        // Footer
        document.add(new Paragraph("\n\nThis is a computer-generated report verified by a clinician. End of Report.")
                .setFontSize(8).setFontColor(ColorConstants.GRAY).setTextAlignment(TextAlignment.CENTER));

        document.close();
    }

    private static void addZebraRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setFontSize(10)));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "-").setBold().setFontSize(10).setFontColor(new DeviceRgb(59, 130, 246))));
    }
}