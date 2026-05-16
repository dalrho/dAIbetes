package org.example.daibetes.modules.doctor.ui.review.model;

import javafx.scene.image.Image;
import java.util.List;

public class ReportData {
    // Patient & General Info
    private String patientName;
    private Image scanImage;
    private String criticality;
    private String criticalityReasoning;
    private String clinicalNotes;

    // Pathological Findings (Original)
    private String microaneurysms;
    private String hemorrhages;
    private String hardExudates;
    private String cottonWoolSpots;
    private String macularEdema;
    private String venousBeading;

    // Pathological Findings (New from Image 2)
    private String irma;
    private String neovascularization;
    private String vitreousHemorrhage;
    private String retinalDetachment;

    // Grades & Management
    private String drGrade;
    private String dmeGrade;
    private List<String> recommendations;

    // Constructor
    public ReportData() {}

    // Getters and Setters
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Image getScanImage() { return scanImage; }
    public void setScanImage(Image scanImage) { this.scanImage = scanImage; }

    public String getCriticality() { return criticality; }
    public void setCriticality(String criticality) { this.criticality = criticality; }

    public String getCriticalityReasoning() { return criticalityReasoning; }
    public void setCriticalityReasoning(String criticalityReasoning) { this.criticalityReasoning = criticalityReasoning; }

    public String getClinicalNotes() { return clinicalNotes; }
    public void setClinicalNotes(String clinicalNotes) { this.clinicalNotes = clinicalNotes; }

    public String getMicroaneurysms() { return microaneurysms; }
    public void setMicroaneurysms(String microaneurysms) { this.microaneurysms = microaneurysms; }

    public String getHemorrhages() { return hemorrhages; }
    public void setHemorrhages(String hemorrhages) { this.hemorrhages = hemorrhages; }

    public String getHardExudates() { return hardExudates; }
    public void setHardExudates(String hardExudates) { this.hardExudates = hardExudates; }

    public String getCottonWoolSpots() { return cottonWoolSpots; }
    public void setCottonWoolSpots(String cottonWoolSpots) { this.cottonWoolSpots = cottonWoolSpots; }

    public String getMacularEdema() { return macularEdema; }
    public void setMacularEdema(String macularEdema) { this.macularEdema = macularEdema; }

    public String getVenousBeading() { return venousBeading; }
    public void setVenousBeading(String venousBeading) { this.venousBeading = venousBeading; }

    // New Getters/Setters for Additional Pathological Findings
    public String getIrma() { return irma; }
    public void setIrma(String irma) { this.irma = irma; }

    public String getNeovascularization() { return neovascularization; }
    public void setNeovascularization(String neovascularization) { this.neovascularization = neovascularization; }

    public String getVitreousHemorrhage() { return vitreousHemorrhage; }
    public void setVitreousHemorrhage(String vitreousHemorrhage) { this.vitreousHemorrhage = vitreousHemorrhage; }

    public String getRetinalDetachment() { return retinalDetachment; }
    public void setRetinalDetachment(String retinalDetachment) { this.retinalDetachment = retinalDetachment; }

    public String getDrGrade() { return drGrade; }
    public void setDrGrade(String drGrade) { this.drGrade = drGrade; }

    public String getDmeGrade() { return dmeGrade; }
    public void setDmeGrade(String dmeGrade) { this.dmeGrade = dmeGrade; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}