package patientsdashboard;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.daibetes.core.database.PatientDashboardDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * ViewModel for patientsdashboard.
 * Owns all state for: View Diagnosis, Schedule Follow-Up,
 * My Doctors, Recent Visits, and Request Scan.
 */
public class PatientDashboardViewModelTest {

    private final PatientDashboardDAO dao = new PatientDashboardDAO();
    private Patient currentPatient;

    // ── Shared ──────────────────────────────────────────────────────────────
    private final StringProperty  statusMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading     = new SimpleBooleanProperty(false);

    // ── Patient info ────────────────────────────────────────────────────────
    private final StringProperty patientName = new SimpleStringProperty("");
    private final StringProperty patientId   = new SimpleStringProperty("");

    // ── VIEW: diagnosis list ─────────────────────────────────────────────────
    private final ObservableList<String[]> diagnoses = FXCollections.observableArrayList();

    // ── SCHEDULE: all tests ──────────────────────────────────────────────────
    private final ObservableList<String[]> schedule = FXCollections.observableArrayList();

    // ── SCHEDULE: next follow-up countdown ──────────────────────────────────
    private final StringProperty  daysUntilFollowUp   = new SimpleStringProperty("—");
    private final StringProperty  followUpDetails      = new SimpleStringProperty("No upcoming follow-up");
    private final StringProperty  followUpDoctor       = new SimpleStringProperty("");

    // ── MY DOCTORS ───────────────────────────────────────────────────────────
    private final ObservableList<Doctor> myDoctors = FXCollections.observableArrayList();

    // ── SEARCH (for Request flow) ────────────────────────────────────────────
    private final StringProperty         searchKeyword = new SimpleStringProperty("");
    private final ObservableList<Doctor> searchResults = FXCollections.observableArrayList();
    private Doctor selectedDoctor;

    // ── REQUEST ──────────────────────────────────────────────────────────────
    private final BooleanProperty requestSuccess = new SimpleBooleanProperty(false);

    // =========================================================================
    // Init — called by controller right after login
    // =========================================================================

    public void initData(Patient patient) {
        this.currentPatient = patient;

        patientName.set(patient.getFirstname() + " " + patient.getLastname());
        patientId.set("ID: " + patient.getPId());

        loadDiagnoses();
        loadSchedule();
        loadMyDoctors();
    }

    // =========================================================================
    // VIEW — load diagnoses
    // =========================================================================

    public void loadDiagnoses() {
        if (currentPatient == null) return;
        isLoading.set(true);

        List<String[]> rows = dao.getDiagnosesByPatient(currentPatient.getPId());
        diagnoses.setAll(rows);

        isLoading.set(false);
        statusMessage.set(rows.isEmpty() ? "No diagnosis records found." : "");
    }

    // =========================================================================
    // SCHEDULE — load all tests + compute follow-up countdown
    // =========================================================================

    public void loadSchedule() {
        if (currentPatient == null) return;
        isLoading.set(true);

        List<String[]> rows = dao.getScheduleByPatient(currentPatient.getPId());
        schedule.setAll(rows);

        // Compute days-until-follow-up from next pending test
        String[] next = dao.getNextSchedule(currentPatient.getPId());
        if (next != null) {
            try {
                LocalDate followUpDate = LocalDate.parse(
                        next[1], DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                long days = ChronoUnit.DAYS.between(LocalDate.now(), followUpDate);

                daysUntilFollowUp.set(days >= 0 ? String.valueOf(days) : "0");
                followUpDetails.set("Follow-up checkup on " + next[1]);
                followUpDoctor.set("Appointment with Dr. " + next[0]);
            } catch (Exception e) {
                daysUntilFollowUp.set("—");
                followUpDetails.set("Follow-up checkup");
                followUpDoctor.set("Dr. " + next[0]);
            }
        } else {
            daysUntilFollowUp.set("—");
            followUpDetails.set("No upcoming follow-up");
            followUpDoctor.set("");
        }

        isLoading.set(false);
    }

    // =========================================================================
    // MY DOCTORS
    // =========================================================================

    public void loadMyDoctors() {
        if (currentPatient == null) return;
        List<Doctor> doctors = dao.getDoctorsByPatient(currentPatient.getPId());
        myDoctors.setAll(doctors);
    }

    // =========================================================================
    // SEARCH (used in Request flow)
    // =========================================================================

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

    // =========================================================================
    // REQUEST
    // =========================================================================

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
            statusMessage.set("You already have a pending request with Dr. "
                    + selectedDoctor.getLastname() + ".");
            return;
        }

        isLoading.set(true);
        int testId = dao.requestTest(
                currentPatient.getPId(), selectedDoctor.getDId(), rawImageId);
        isLoading.set(false);

        if (testId != -1) {
            statusMessage.set("Request sent to Dr. "
                    + selectedDoctor.getFirstname() + " " + selectedDoctor.getLastname() + ".");
            requestSuccess.set(true);
            loadSchedule(); // refresh countdown + schedule card
            loadMyDoctors();
        } else {
            statusMessage.set("Failed to submit request. Please try again.");
        }
    }

    // =========================================================================
    // Property accessors
    // =========================================================================

    public StringProperty  patientNameProperty()        { return patientName; }
    public StringProperty  patientIdProperty()          { return patientId; }

    public ObservableList<String[]> getDiagnoses()      { return diagnoses; }
    public ObservableList<String[]> getSchedule()       { return schedule; }
    public ObservableList<Doctor>   getMyDoctors()      { return myDoctors; }
    public ObservableList<Doctor>   getSearchResults()  { return searchResults; }

    public StringProperty  daysUntilFollowUpProperty()  { return daysUntilFollowUp; }
    public StringProperty  followUpDetailsProperty()    { return followUpDetails; }
    public StringProperty  followUpDoctorProperty()     { return followUpDoctor; }

    public StringProperty  searchKeywordProperty()      { return searchKeyword; }
    public Doctor          getSelectedDoctor()          { return selectedDoctor; }
    public BooleanProperty requestSuccessProperty()     { return requestSuccess; }

    public StringProperty  statusMessageProperty()      { return statusMessage; }
    public BooleanProperty isLoadingProperty()          { return isLoading; }
    public Patient         getCurrentPatient()          { return currentPatient; }
}