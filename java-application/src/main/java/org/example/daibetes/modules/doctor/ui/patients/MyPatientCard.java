package org.example.daibetes.modules.doctor.ui.patients;

import java.time.LocalDateTime;
import java.util.List;

public class MyPatientCard {
    private final int patientId;
    private final String patientName;
    private final LocalDateTime lastReported;
    private final String latestCriticalityLevel;
    private final List<MyPatientReport> reports;

    public MyPatientCard(
            int patientId,
            String patientName,
            LocalDateTime lastReported,
            String latestCriticalityLevel,
            List<MyPatientReport> reports
    ) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.lastReported = lastReported;
        this.latestCriticalityLevel = latestCriticalityLevel;
        this.reports = reports;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public LocalDateTime getLastReported() {
        return lastReported;
    }

    public String getLatestCriticalityLevel() {
        return latestCriticalityLevel;
    }

    public List<MyPatientReport> getReports() {
        return reports;
    }

    public int getReportCount() {
        return reports == null ? 0 : reports.size();
    }

    public int getCriticalityRank() {
        if (latestCriticalityLevel == null) return 0;

        String value = latestCriticalityLevel.trim().toLowerCase();

        if (value.contains("critical") || value.contains("urgent") || value.contains("severe")) return 5;
        if (value.contains("high")) return 4;
        if (value.contains("moderate")) return 3;
        if (value.contains("low") || value.contains("mild")) return 2;
        if (value.contains("normal")) return 1;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}