package org.example.daibetes.modules.doctor.ui.patients;

import java.time.LocalDateTime;

public class MyPatientReport {
    private final int reportId;
    private final int testId;
    private final int patientId;
    private final String patientName;
    private final LocalDateTime lastReported;
    private final String criticalityLevel;

    public MyPatientReport(
            int reportId,
            int testId,
            int patientId,
            String patientName,
            LocalDateTime lastReported,
            String criticalityLevel
    ) {
        this.reportId = reportId;
        this.testId = testId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.lastReported = lastReported;
        this.criticalityLevel = criticalityLevel;
    }

    public int getReportId() {
        return reportId;
    }

    public int getTestId() {
        return testId;
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

    public String getCriticalityLevel() {
        return criticalityLevel;
    }

    public int getCriticalityRank() {
        if (criticalityLevel == null) return 0;

        String value = criticalityLevel.trim().toLowerCase();

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