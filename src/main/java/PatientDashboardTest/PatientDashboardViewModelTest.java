package PatientDashboardTest;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.daibetes.core.database.PatientDashboardDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;

import java.util.List;

/**
 * ViewModel for the Patient Dashboard.
 * Owns all state for Search, View, Request, and Schedule features.
 * Zero JavaFX Node references — fully testable.
 */
public class PatientDashboardViewModelTest {

    private final PatientDashboardDAO dao = new PatientDashboardDAO();

    // The logged-in patient — set once via initData()
    private Patient currentPatient;

    // =========================================================================
    // Shared state
    // =========================================================================

    private final StringProperty  statusMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading     = new SimpleBooleanProperty(false);

    // =========================================================================
    // SEARCH
    // =========================================================================

    private final StringProperty              searchKeyword = new SimpleStringProperty("");
    private final ObservableList<Doctor>      searchResults = FXCollections.observableArrayList();

    // The doctor the patient has selected from search results
    private Doctor selectedDoctor;

    public void search() {
        String keyword = searchKeyword.get().trim();

        if (keyword.isEmpty()) {
            statusMessage.set("Please enter a name or hospital to search.");
            return;
        }

        isLoading.set(true);
        statusMessage.set("");

        List<Doctor> results = dao.searchDoctors(keyword);
        searchResults.setAll(results);

        isLoading.set(false);

        if (results.isEmpty()) {
            statusMessage.set("No doctors found for \"" + keyword + "\".");
        }
    }

    public void selectDoctor(Doctor doctor) {
        this.selectedDoctor = doctor;
    }

    // =========================================================================
    // VIEW — patient's diagnosis records
    // =========================================================================

    private final ObservableList<String[]> diagnoses = FXCollections.observableArrayList();

    // The currently expanded/selected diagnosis for detail view
    private final ObjectProperty<String[]> selectedDiagnosis = new SimpleObjectProperty<>();

    public void loadDiagnoses() {
        if (currentPatient == null) return;

        isLoading.set(true);
        List<String[]> rows = dao.getDiagnosesByPatient(currentPatient.getPId());
        diagnoses.setAll(rows);
        isLoading.set(false);

        if (rows.isEmpty()) {
            statusMessage.set("No diagnosis records found.");
        }
    }

    public void viewDiagnosisDetail(int diagnosisId) {
        String[] detail = dao.getDiagnosisById(diagnosisId);
        selectedDiagnosis.set(detail);
    }

    // =========================================================================
    // REQUEST — submit a scan test request to a doctor
    // =========================================================================

    private final BooleanProperty requestSuccess = new SimpleBooleanProperty(false);

    /**
     * Submits a test request.
     * rawImageId must be obtained after the patient uploads their retinal image
     * via ImageDAO before calling this method.
     */
    public void requestTest(int rawImageId) {
        requestSuccess.set(false);
        statusMessage.set("");

        if (selectedDoctor == null) {
            statusMessage.set("Please select a doctor first.");
            return;
        }

        if (rawImageId == -1) {
            statusMessage.set("Please upload your retinal scan image.");
            return;
        }

        // Prevent duplicate pending requests
        if (dao.hasPendingRequest(currentPatient.getPId(), selectedDoctor.getDId())) {
            statusMessage.set("You already have a pending request with Dr. "
                    + selectedDoctor.getLastname() + ".");
            return;
        }

        isLoading.set(true);
        int testId = dao.requestTest(currentPatient.getPId(), selectedDoctor.getDId(), rawImageId);
        isLoading.set(false);

        if (testId != -1) {
            statusMessage.set("Request submitted successfully to Dr. "
                    + selectedDoctor.getFirstname() + " "
                    + selectedDoctor.getLastname() + ".");
            requestSuccess.set(true);
            loadSchedule(); // refresh schedule after new request
        } else {
            statusMessage.set("Failed to submit request. Please try again.");
        }
    }

    // =========================================================================
    // SCHEDULE — view all tests for the patient
    // =========================================================================

    private final ObservableList<String[]> schedule = FXCollections.observableArrayList();

    public void loadSchedule() {
        if (currentPatient == null) return;

        isLoading.set(true);
        List<String[]> rows = dao.getScheduleByPatient(currentPatient.getPId());
        schedule.setAll(rows);
        isLoading.set(false);

        if (rows.isEmpty()) {
            statusMessage.set("No scheduled tests found.");
        }
    }

    // =========================================================================
    // Init
    // =========================================================================

    public void initData(Patient patient) {
        this.currentPatient = patient;
        loadDiagnoses();
        loadSchedule();
    }

    // =========================================================================
    // Property accessors
    // =========================================================================

    public StringProperty              searchKeywordProperty()   { return searchKeyword; }
    public ObservableList<Doctor>      getSearchResults()        { return searchResults; }
    public Doctor                      getSelectedDoctor()       { return selectedDoctor; }

    public ObservableList<String[]>    getDiagnoses()            { return diagnoses; }
    public ObjectProperty<String[]>    selectedDiagnosisProperty(){ return selectedDiagnosis; }

    public BooleanProperty             requestSuccessProperty()  { return requestSuccess; }

    public ObservableList<String[]>    getSchedule()             { return schedule; }

    public StringProperty              statusMessageProperty()   { return statusMessage; }
    public BooleanProperty             isLoadingProperty()       { return isLoading; }
    public Patient                     getCurrentPatient()       { return currentPatient; }
}