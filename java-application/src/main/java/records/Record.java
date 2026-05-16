package records;

public class Record {
    private String patientId;
    private String patientName;
    private String scanDate;
    private String followUp;
    private String status;
    private String criticalityLevel;

    public Record(
            String patientId,
            String patientName,
            String scanDate,
            String followUp,
            String status,
            String criticalityLevel
    ) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.scanDate = scanDate;
        this.followUp = followUp;
        this.status = status;
        this.criticalityLevel = criticalityLevel;
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

    public String getStatus() {
        return status;
    }

    public String getCriticalityLevel() {
        return criticalityLevel;
    }
}