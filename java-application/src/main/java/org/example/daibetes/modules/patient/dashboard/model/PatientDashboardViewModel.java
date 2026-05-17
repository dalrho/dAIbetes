package org.example.daibetes.modules.patient.dashboard.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.daibetes.core.database.PatientDashboardDAO;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.Patient;
import org.example.daibetes.shared.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class PatientDashboardViewModel {

    private final PatientDashboardDAO dao = new PatientDashboardDAO();
    private Patient currentPatient;

    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty patientName = new SimpleStringProperty("");

    private final ObservableList<Notification> notifications = FXCollections.observableArrayList();
    private final ObservableList<String[]> acceptedSchedule = FXCollections.observableArrayList();

    private final StringProperty daysUntilFollowUp = new SimpleStringProperty("—");
    private final StringProperty followUpDetails = new SimpleStringProperty("No upcoming follow-up");
    private final StringProperty followUpDoctor = new SimpleStringProperty("");

    private final StringProperty searchKeyword = new SimpleStringProperty("");
    private final ObservableList<Doctor> searchResults = FXCollections.observableArrayList();
    private Doctor selectedDoctor;

    private final BooleanProperty requestSuccess = new SimpleBooleanProperty(false);

    public void initData(Patient patient) {
        this.currentPatient = patient;
        patientName.set(patient.getFirstname() + " " + patient.getLastname());
        refreshDashboardData();
    }

    public void refreshDashboardData() {
        if (currentPatient == null) return;

        List<Notification> activeFeed = new ArrayList<>(dao.getRecentActivities(currentPatient.getPId()));

        if (selectedDoctor != null && dao.hasPendingRequest(currentPatient.getPId(), selectedDoctor.getDId())) {
            activeFeed.add(0, new Notification(
                    -1,
                    currentPatient.getPId(),
                    -1,
                    "Scheduled appointment still pending. Click here to view",
                    "GO_TO_CALENDAR",
                    false
            ));
        }
        notifications.setAll(activeFeed);

        acceptedSchedule.setAll(dao.getConfirmedSchedules(currentPatient.getPId()));

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
            requestSuccess.set(true);
            refreshDashboardData();
        } else {
            statusMessage.set("Failed to submit request. Please try again.");
        }
    }

    public StringProperty patientNameProperty() { return patientName; }
    public ObservableList<Notification> getNotifications() { return notifications; }
    public ObservableList<String[]> getAcceptedSchedule() { return acceptedSchedule; }
    public StringProperty daysUntilFollowUpProperty() { return daysUntilFollowUp; }
    public StringProperty followUpDetailsProperty() { return followUpDetails; }
    public StringProperty followUpDoctorProperty() { return followUpDoctor; }
    public StringProperty searchKeywordProperty() { return searchKeyword; }
    public ObservableList<Doctor> getSearchResults() { return searchResults; }
    public Doctor getSelectedDoctor() { return selectedDoctor; }
    public BooleanProperty requestSuccessProperty() { return requestSuccess; }
    public StringProperty statusMessageProperty() { return statusMessage; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public Patient getCurrentPatient() { return currentPatient; }
}