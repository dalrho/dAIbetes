package org.example.daibetes.modules.doctor.ui.patients;

import java.time.LocalDate;

public class DummyPatient {
    public enum Criticality {
        CRITICAL(4), HIGH(3), MODERATE(2), LOW(1), ABSENT(0);
        public final int rank;
        Criticality(int rank) { this.rank = rank; }
    }

    private String name;
    private LocalDate reportDate;
    private Criticality criticality;

    public DummyPatient(String name, LocalDate reportDate, Criticality criticality) {
        this.name = name;
        this.reportDate = reportDate;
        this.criticality = criticality;
    }

    public String getName() { return name; }
    public LocalDate getReportDate() { return reportDate; }
    public Criticality getCriticality() { return criticality; }
}