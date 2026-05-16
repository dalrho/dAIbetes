package patientsdashboard;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.daibetes.core.database.PatientDashboardDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.Notification;

import java.util.List;

public class PatientDashboardViewModel {

    private final PatientDashboardDAO dao = new PatientDashboardDAO();
    private Patient currentPatient;

    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);

    private final StringProperty patientName = new SimpleStringProperty("");
    private final StringProperty patientId = new SimpleStringProperty("");

    // Observable structural nodes
    private final ObservableList<String[]> diagnoses = FXCollections.observableArrayList();
    private final ObservableList<Notification> notifications = FXCollections.observableArrayList();
    private final ObservableList<String[]> acceptedSchedule = FXCollections.observableArrayList();

    private final StringProperty daysUntilFollowUp = new SimpleStringProperty("—");
    private final StringProperty followUpDetails = new SimpleStringProperty("No upcoming follow-up");
    private final StringProperty followUpDoctor = new SimpleStringProperty("");

    private final ObservableList<Doctor> myDoctors = FXCollections.observableArrayList();
    private final StringProperty searchKeyword = new SimpleStringProperty("");
    private final ObservableList<Doctor> searchResults = FXCollections.observableArrayList();
    private Doctor selectedDoctor;

    private final BooleanProperty requestSuccess = new SimpleBooleanProperty(false);

    public void initData(Patient patient) {
        this.currentPatient = patient;
        // Dynamically configure presentation bounds using non-hardcoded mapped entities
        patientName.set(patient.getFirstname() + " " + patient.getLastname());
        patientId.set("ID: " + patient.getPId());

        loadDiagnoses();
        loadMyDoctors();
        refreshDashboardData();
    }

    public void loadDiagnoses() {
        if (currentPatient == null) return;
        isLoading.set(true);
        List<String[]> rows = dao.getDiagnosesByPatient(currentPatient.getPId());
        diagnoses.setAll(rows);
        isLoading.set(false);
        statusMessage.set(rows.isEmpty() ? "No diagnosis records found." : "");
    }

    public void refreshDashboardData() {
        if (currentPatient == null) return;

        // Fetch notifications feed
        notifications.setAll(dao.getNotificationsByPatient(currentPatient.getPId()));

        // Fetch filtered accepted schedules
        acceptedSchedule.setAll(dao.getAcceptedSchedules(currentPatient.getPId()));

        // Dynamic date countdown processing parsing
        String[] nearest = dao.getNearestUpcomingAppointment(currentPatient.getPId());
        if (nearest != null) {
            daysUntilFollowUp.set(nearest[2]);
            followUpDetails.set("Appointment on " + nearest[1]);
            followUpDoctor.set("With Dr. " + nearest[0]);
        } else {
            daysUntilFollowUp.set("—");
            followUpDetails.set("No upcoming appointments");
            followUpDoctor.set("");
        }
    }

    public void loadMyDoctors() {
        if (currentPatient == null) return;
        List<Doctor> doctors = dao.getDoctorsByPatient(currentPatient.getPId());
        myDoctors.setAll(doctors);
    }

    public void search() {
        String keyword = searchKeyword.get().trim();
        if (keyword.isEmpty()) {
            statusMessage.set("Please enter a doctor name or hospital.");
            return;
        }
        isLoading.set(true);
        statusMessage.set("");
        List<Doctor> results = dao.searchDoctors(keyword);
        searchResults.setAll(results);
        isLoading.set(false);
        if (results.isEmpty()) statusMessage.set("No doctors found for \"" + keyword + "\".");
    }

    public void selectDoctor(Doctor doctor) {
        this.selectedDoctor = doctor;
    }

    public void requestTest(int rawImageId) {
        requestSuccess.set(false);
        statusMessage.set("");

        if (selectedDoctor == null) {
            statusMessage.set("Please select a doctor first.");
            return;
        }
        if (rawImageId == -1) {
            statusMessage.set("Image upload failed. Please try again.");
            return;
        }
        if (dao.hasPendingRequest(currentPatient.getPId(), selectedDoctor.getDId())) {
            statusMessage.set("You already have a pending request with Dr. " + selectedDoctor.getLastname() + ".");
            return;
        }

        isLoading.set(true);
        int testId = dao.requestTest(currentPatient.getPId(), selectedDoctor.getDId(), rawImageId);
        isLoading.set(false);

        if (testId != -1) {
            statusMessage.set("Request sent to Dr. " + selectedDoctor.getFirstname() + " " + selectedDoctor.getLastname() + ".");
            boolean state = true;
            requestSuccess.set(state);
            refreshDashboardData();
            loadMyDoctors();
        } else {
            statusMessage.set("Failed to submit request. Please try again.");
        }
    }

    // Property Bindings Accessors
    public StringProperty patientNameProperty() { return patientName; }
    public StringProperty patientIdProperty() { return patientId; }
    public ObservableList<String[]> getDiagnoses() { return diagnoses; }
    public ObservableList<Notification> getNotifications() { return notifications; }
    public ObservableList<String[]> getAcceptedSchedule() { return acceptedSchedule; }
    public StringProperty daysUntilFollowUpProperty() { return daysUntilFollowUp; }
    public StringProperty followUpDetailsProperty() { return followUpDetails; }
    public StringProperty followUpDoctorProperty() { return followUpDoctor; }
    public ObservableList<Doctor> getMyDoctors() { return myDoctors; }
    public StringProperty searchKeywordProperty() { return searchKeyword; }
    public ObservableList<Doctor> getSearchResults() { return searchResults; }
    public Doctor getSelectedDoctor() { return selectedDoctor; }
    public BooleanProperty requestSuccessProperty() { return requestSuccess; }
    public StringProperty statusMessageProperty() { return statusMessage; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public Patient getCurrentPatient() { return currentPatient; }
}