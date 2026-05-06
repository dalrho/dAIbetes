
package PatientDashboardTest;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;

import java.io.File;

    /**
     * Controller for patient-dashboard-screen.fxml.
     *
     * fx:id mapping:
     *
     * -- SHARED --
     *   statusLabel       → feedback messages
     *
     * -- SEARCH --
     *   searchField       → keyword input
     *   searchButton      → triggers search
     *   searchTable       → TableView<Doctor> results
     *   colDocName        → Doctor full name column
     *   colHospital       → Hospital column
     *   requestButton     → submits test request for selected doctor
     *
     * -- VIEW --
     *   diagnosisTable    → TableView<String[]> of diagnosis records
     *   colDiagDoctor     → Doctor name column
     *   colDiagDate       → Date column
     *   colDiagStatus     → Diagnosis text (truncated) column
     *   detailPane        → VBox shown when a diagnosis is selected
     *   detailDoctor      → Label: doctor name
     *   detailDiagnosis   → Label: full diagnosis text
     *   detailRecommend   → Label: recommendation
     *   detailDate        → Label: date
     *
     * -- SCHEDULE --
     *   scheduleTable     → TableView<String[]> of test records
     *   colSchedDoctor    → Doctor name column
     *   colSchedDate      → Test date column
     *   colSchedStatus    → Pending / Completed column
     */
    public class PatientDashboardControllerTest {

        // --- Shared ---
        @FXML private Label statusLabel;

        // --- Search ---
        @FXML private TextField              searchField;
        @FXML private Button                 searchButton;
        @FXML private Button                 requestButton;
        @FXML private TableView<Doctor>      searchTable;
        @FXML private TableColumn<Doctor, String> colDocName;
        @FXML private TableColumn<Doctor, String> colHospital;

        // --- View ---
        @FXML private TableView<String[]>         diagnosisTable;
        @FXML private TableColumn<String[], String> colDiagDoctor;
        @FXML private TableColumn<String[], String> colDiagDate;
        @FXML private TableColumn<String[], String> colDiagText;
        @FXML private Label detailDoctor;
        @FXML private Label detailDiagnosis;
        @FXML private Label detailRecommend;
        @FXML private Label detailDate;

        // --- Schedule ---
        @FXML private TableView<String[]>           scheduleTable;
        @FXML private TableColumn<String[], String> colSchedDoctor;
        @FXML private TableColumn<String[], String> colSchedDate;
        @FXML private TableColumn<String[], String> colSchedStatus;

        private final PatientDashboardViewModelTest viewModel = new PatientDashboardViewModelTest();

        // =========================================================================
        // Init (called by loginController after login)
        // =========================================================================

        public void initData(Patient patient) {
            viewModel.initData(patient);
        }

        // =========================================================================
        // JavaFX initialize
        // =========================================================================

        @FXML
        public void initialize() {

            // --- Shared bindings ---
            statusLabel.textProperty().bind(viewModel.statusMessageProperty());

            // ---- SEARCH table columns ----
            colDocName.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getFirstname() + " " + cell.getValue().getLastname()));
            colHospital.setCellValueFactory(cell -> new SimpleStringProperty(
                    cell.getValue().getHospital()));

            searchTable.setItems(viewModel.getSearchResults());

            // Bind search field ↔ ViewModel
            searchField.textProperty().bindBidirectional(viewModel.searchKeywordProperty());

            // Enable request button only when a doctor row is selected
            requestButton.setDisable(true);
            searchTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, selected) -> {
                        viewModel.selectDoctor(selected);
                        requestButton.setDisable(selected == null);
                    });

            // ---- VIEW (diagnosis) table columns ----
            colDiagDoctor.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()[1]));
            colDiagDate.setCellValueFactory(cell   -> new SimpleStringProperty(cell.getValue()[4]));
            colDiagText.setCellValueFactory(cell   -> new SimpleStringProperty(
                    truncate(cell.getValue()[2], 50)));

            diagnosisTable.setItems(viewModel.getDiagnoses());

            // Show detail panel when a diagnosis row is selected
            diagnosisTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, old, selected) -> {
                        if (selected != null) {
                            viewModel.viewDiagnosisDetail(Integer.parseInt(selected[0]));
                        }
                    });

            viewModel.selectedDiagnosisProperty().addListener((obs, old, detail) -> {
                if (detail != null) populateDetailPane(detail);
            });

            // ---- SCHEDULE table columns ----
            colSchedDoctor.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()[1]));
            colSchedDate.setCellValueFactory(cell   -> new SimpleStringProperty(cell.getValue()[2]));
            colSchedStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()[3]));

            scheduleTable.setItems(viewModel.getSchedule());
        }

        // =========================================================================
        // SEARCH handlers
        // =========================================================================

        @FXML
        private void onSearch() {
            viewModel.search();
        }

        // =========================================================================
        // REQUEST handler
        // =========================================================================

        @FXML
        private void onRequestTest() {
            // Step 1: prompt patient to upload retinal scan image
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Upload Retinal Scan");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Image Files (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));

            File selectedFile = fileChooser.showOpenDialog(requestButton.getScene().getWindow());

            if (selectedFile == null) {
                statusLabel.setText("Upload cancelled. No request submitted.");
                return;
            }

            // Step 2: save image to DB (image_type_id = 1 → Raw Scan)
            ImageDAO imageDAO = new ImageDAO();
            int rawImageId = imageDAO.createImage(new File(selectedFile.getAbsolutePath()), 1);

            // Step 3: delegate to ViewModel
            viewModel.requestTest(rawImageId);
        }

        // =========================================================================
        // SCHEDULE handler
        // =========================================================================

        @FXML
        private void onRefreshSchedule() {
            viewModel.loadSchedule();
        }

        // =========================================================================
        // Helpers
        // =========================================================================

        private void populateDetailPane(String[] detail) {
            // detail: [0]=id [1]=doctor [2]=diagnosis [3]=recommendation [4]=date
            detailDoctor.setText("Dr. " + detail[1]);
            detailDiagnosis.setText(detail[2]);
            detailRecommend.setText(detail[3]);
            detailDate.setText(detail[4]);
        }

        private String truncate(String text, int maxLength) {
            if (text == null) return "";
            return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
        }
    }

