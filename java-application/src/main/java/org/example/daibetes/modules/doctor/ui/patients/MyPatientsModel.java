package org.example.daibetes.modules.doctor.ui.patients;// MyPatientsModel.java
import java.time.LocalDate;

public class MyPatientsModel {
    public enum Criticality {
        CRITICAL, HIGH, MODERATE, LOW, ABSENT
    }

    private String name;
    private LocalDate lastReportDate;
    private Criticality criticality;

    public MyPatientsModel(String name, LocalDate lastReportDate, Criticality criticality) {
        this.name = name;
        this.lastReportDate = lastReportDate;
        this.criticality = criticality;
    }
    // Getters here...
    public String getName() { return name; }
    public LocalDate getLastReportDate() { return lastReportDate; }
    public Criticality getCriticality() { return criticality; }
}