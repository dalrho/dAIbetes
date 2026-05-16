package org.example.daibetes.modules.doctor.ui.patients;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.MyPatientsDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.User;
import register.sceneLoader;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class MyPatientsController {

    @FXML private TextField searchField;
    @FXML private GridPane patientGrid;
    @FXML private Label paginationLabel;
    @FXML private ToggleGroup filterGroup;

    private final ObservableList<MyPatientCard> masterData = FXCollections.observableArrayList();
    private FilteredList<MyPatientCard> filteredData;
    private SortedList<MyPatientCard> sortedData;

    private int loggedInDoctorId = 1;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 9;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    @FXML
    public void initialize() {
        loadLoggedInDoctorId();
        loadPatientsFromDatabase();

        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);

        searchField.textProperty().addListener((obs, old, newVal) -> {
            filteredData.setPredicate(patient -> {
                if (newVal == null || newVal.isBlank()) {
                    return true;
                }

                String search = newVal.toLowerCase();

                return patient.getPatientName().toLowerCase().contains(search)
                        || patient.getLatestCriticalityLevel().toLowerCase().contains(search);
            });

            currentPage = 1;
            refreshGrid();
        });

        filterGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) return;

            String text = ((ToggleButton) newVal).getText();

            if (text.equals("RECENT")) {
                sortedData.setComparator(Comparator.comparing(MyPatientCard::getLastReported).reversed());
            } else if (text.equals("OLDEST")) {
                sortedData.setComparator(Comparator.comparing(MyPatientCard::getLastReported));
            } else if (text.equals("MOST CRITICAL")) {
                sortedData.setComparator(Comparator.comparing(MyPatientCard::getCriticalityRank).reversed());
            } else if (text.equals("LEAST CRITICAL")) {
                sortedData.setComparator(Comparator.comparing(MyPatientCard::getCriticalityRank));
            }

            currentPage = 1;
            refreshGrid();
        });

        sortedData.setComparator(Comparator.comparing(MyPatientCard::getLastReported).reversed());
        refreshGrid();
    }

    private void loadLoggedInDoctorId() {
        User currentUser = AppContext.getInstance().getCurrentUser();

        if (currentUser instanceof Doctor doctor) {
            loggedInDoctorId = doctor.getDId();
        }
    }

    private void loadPatientsFromDatabase() {
        MyPatientsDAO dao = new MyPatientsDAO();
        List<MyPatientCard> patients = dao.getPatientCardsByDoctorId(loggedInDoctorId);
        masterData.setAll(patients);
    }

    private void refreshGrid() {
        patientGrid.getChildren().clear();


        int totalItems = sortedData.size();
        int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE);

        if (totalPages == 0) {
            paginationLabel.setText("0 out of 0");

            Label emptyLabel = new Label("No patients found.");
            emptyLabel.getStyleClass().add("pagination-text");
            patientGrid.add(emptyLabel, 0, 0);

            return;
        }

        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, totalItems);

        for (int i = start; i < end; i++) {
            VBox card = createPatientCard(sortedData.get(i));
            patientGrid.add(card, (i - start) % 3, (i - start) / 3);
        }

        paginationLabel.setText(currentPage + " out of " + totalPages);
    }

    private VBox createPatientCard(MyPatientCard patient) {
        VBox card = new VBox(8);
        card.getStyleClass().add("patient-card");

        Label name = new Label(patient.getPatientName());
        name.getStyleClass().add("card-name");

        Label date = new Label("Last Report: " + patient.getLastReported().format(formatter));
        Label crit = new Label("Critical Level: " + patient.getLatestCriticalityLevel());
        Label count = new Label("Reports: " + patient.getReportCount());

        card.getChildren().addAll(name, date, crit, count);

        card.setOnMouseClicked(event -> openPatientRecords(patient));

        return card;
    }

    private void openPatientRecords(MyPatientCard patient) {
        AppContext context = AppContext.getInstance();

        context.setSelectedRecordsPatientId(patient.getPatientId());
        context.setSelectedRecordsPatientName(patient.getPatientName());
        context.setSelectedRecordsDoctorId(loggedInDoctorId);

        Stage stage = (Stage) patientGrid.getScene().getWindow();

        Scene scene = sceneLoader.load(
                "records",
                "records-screen.fxml",
                null
        );

        if (scene == null) {
            showAlert("Navigation Error", "Could not load patient records.");
            return;
        }

        stage.setScene(scene);
        stage.setTitle("Patient Records - " + patient.getPatientName());
        stage.show();
    }

    @FXML
    private void handlePrev() {
        if (currentPage > 1) {
            currentPage--;
            refreshGrid();
        }
    }

    @FXML
    private void handleNext() {
        int totalPages = (int) Math.ceil((double) sortedData.size() / ITEMS_PER_PAGE);

        if (currentPage < totalPages) {
            currentPage++;
            refreshGrid();
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) paginationLabel.getScene().getWindow();

        Scene scene = sceneLoader.load(
                "doctorDashboard",
                "doctor-dashboard.fxml",
                "/styles/doctorDashboard.css"
        );

        if (scene == null) {
            showAlert("Navigation Error", "Could not load doctor dashboard.");
            return;
        }

        stage.setScene(scene);
        stage.setTitle("Doctor Dashboard");
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}