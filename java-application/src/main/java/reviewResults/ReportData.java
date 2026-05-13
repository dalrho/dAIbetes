package reviewResults;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class ReportData {
    private Image scanImage;
    private String patientName = "John Doe"; // Default or set from previous screen
    private String criticality;
    private String criticalityReasoning;
    private String microaneurysms, hemorrhages, hardExudates, cottonWoolSpots, macularEdema, venousBeading;
    private String drGrade, dmeGrade, clinicalNotes;
    private List<String> recommendations = new ArrayList<>();

    // Getters and Setters
    public Image getScanImage() { return scanImage; }
    public void setScanImage(Image scanImage) { this.scanImage = scanImage; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getCriticality() { return criticality; }
    public void setCriticality(String criticality) { this.criticality = criticality; }
    public String getCriticalityReasoning() { return criticalityReasoning; }
    public void setCriticalityReasoning(String criticalityReasoning) { this.criticalityReasoning = criticalityReasoning; }
    public String getMicroaneurysms() { return microaneurysms; }
    public void setMicroaneurysms(String v) { this.microaneurysms = v; }
    public String getHemorrhages() { return hemorrhages; }
    public void setHemorrhages(String v) { this.hemorrhages = v; }
    public String getHardExudates() { return hardExudates; }
    public void setHardExudates(String v) { this.hardExudates = v; }
    public String getCottonWoolSpots() { return cottonWoolSpots; }
    public void setCottonWoolSpots(String v) { this.cottonWoolSpots = v; }
    public String getMacularEdema() { return macularEdema; }
    public void setMacularEdema(String v) { this.macularEdema = v; }
    public String getVenousBeading() { return venousBeading; }
    public void setVenousBeading(String v) { this.venousBeading = v; }
    public String getDrGrade() { return drGrade; }
    public void setDrGrade(String v) { this.drGrade = v; }
    public String getDmeGrade() { return dmeGrade; }
    public void setDmeGrade(String v) { this.dmeGrade = v; }
    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String v) { this.clinicalNotes = v; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}