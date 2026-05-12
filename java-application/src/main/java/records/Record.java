package records;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Record {

    private final StringProperty patientId;
    private final StringProperty patientName;
    private final StringProperty scanDate;
    private final StringProperty scanType;
    private final StringProperty status;
    private final StringProperty diagnosis;

    public Record(String patientId, String patientName, String scanDate,
                  String scanType, String status, String diagnosis) {

        this.patientId = new SimpleStringProperty(patientId);
        this.patientName = new SimpleStringProperty(patientName);
        this.scanDate = new SimpleStringProperty(scanDate);
        this.scanType = new SimpleStringProperty(scanType);
        this.status = new SimpleStringProperty(status);
        this.diagnosis = new SimpleStringProperty(diagnosis);
    }

    public String getPatientId() { return patientId.get(); }
    public StringProperty patientIdProperty() { return patientId; }

    public String getPatientName() { return patientName.get(); }
    public StringProperty patientNameProperty() { return patientName; }

    public String getScanDate() { return scanDate.get(); }
    public StringProperty scanDateProperty() { return scanDate; }

    public String getScanType() { return scanType.get(); }
    public StringProperty scanTypeProperty() { return scanType; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }

    public String getDiagnosis() { return diagnosis.get(); }
    public StringProperty diagnosisProperty() { return diagnosis; }
}