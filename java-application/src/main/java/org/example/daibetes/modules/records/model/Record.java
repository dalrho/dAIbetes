package org.example.daibetes.modules.records.model;

public class Record {
    private int reportId;
    private String patientId;
    private String patientName;
    private String scanDate;
    private String followUp;
    private String criticalityLevel;

    public Record(
            int reportId,
            String patientId,
            String patientName,
            String scanDate,
            String followUp,
            String criticalityLevel
    ) {
        this.reportId = reportId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.scanDate = scanDate;
        this.followUp = followUp;
        this.criticalityLevel = criticalityLevel;
    }

    public int getReportId() {
        return reportId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getScanDate() {
        return scanDate;
    }

    public String getFollowUp() {
        return followUp;
    }

    public String getCriticalityLevel() {
        return criticalityLevel;
    }
}