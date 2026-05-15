package org.example.daibetes.modules.doctor.ui.patients;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Comparator;

public class MyPatientsController {
    @FXML private TextField searchField;
    @FXML private GridPane patientGrid;
    @FXML private Label paginationLabel;
    @FXML private ToggleGroup filterGroup;

    private ObservableList<DummyPatient> masterData = FXCollections.observableArrayList();
    private FilteredList<DummyPatient> filteredData;
    private SortedList<DummyPatient> sortedData;

    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 9;

    @FXML
    public void initialize() {
        generateDummyData();
        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);

        // 1. Search Logic
        searchField.textProperty().addListener((obs, old, newVal) -> {
            filteredData.setPredicate(p -> newVal == null || newVal.isEmpty() ||
                    p.getName().toLowerCase().contains(newVal.toLowerCase()));
            currentPage = 1;
            refreshGrid();
        });

        // 2. Filter Pill Logic
        filterGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) return;
            String text = ((ToggleButton)newVal).getText();
            if (text.equals("RECENT")) sortedData.setComparator(Comparator.comparing(DummyPatient::getReportDate).reversed());
            else if (text.equals("OLDEST")) sortedData.setComparator(Comparator.comparing(DummyPatient::getReportDate));
            else if (text.equals("MOST CRITICAL")) sortedData.setComparator(Comparator.comparing(p -> p.getCriticality().rank, Comparator.reverseOrder()));
            else if (text.equals("LEAST CRITICAL")) sortedData.setComparator(Comparator.comparing(p -> p.getCriticality().rank));
            refreshGrid();
        });

        refreshGrid();
    }

    private void refreshGrid() {
        patientGrid.getChildren().clear();
        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, sortedData.size());

        for (int i = start; i < end; i++) {
            VBox card = createPatientCard(sortedData.get(i));
            patientGrid.add(card, (i - start) % 3, (i - start) / 3);
        }

        int totalPages = (int) Math.ceil((double)sortedData.size() / ITEMS_PER_PAGE);
        paginationLabel.setText(currentPage + " out of " + Math.max(1, totalPages));
    }

    private VBox createPatientCard(DummyPatient p) {
        VBox card = new VBox(8);
        card.getStyleClass().add("patient-card");

        // CLICK ACTION
        card.setOnMouseClicked(event -> openPatientRecords(p));

        Label name = new Label(p.getName());
        name.getStyleClass().add("card-name");
        Label date = new Label("Last Report: " + p.getReportDate());
        Label crit = new Label("Critical Level: " + p.getCriticality());

        card.getChildren().addAll(name, date, crit);
        return card;
    }

    private void openPatientRecords(DummyPatient p) {
        System.out.println("Opening Records for " + p.getName());
        // Insert your sceneLoader.load(...) logic here
    }

    @FXML private void handlePrev() { if (currentPage > 1) { currentPage--; refreshGrid(); } }
    @FXML private void handleNext() {
        if (currentPage < Math.ceil((double)sortedData.size()/ITEMS_PER_PAGE)) { currentPage++; refreshGrid(); }
    }
    @FXML private void handleBack() {
        try {
            // 1. Get the current stage
            // Use any FXML element you have (like notesArea or reportImageView) to get the scene
            Stage stage = (Stage) paginationLabel.getScene().getWindow();

            // 2. Locate the previous FXML
            // Based on your previous structure, it should be in /imageProcessing/
            var resource = getClass().getResource("/doctorDashboard/doctor-dashboard.fxml");

            if (resource == null) {
                System.err.println("ERROR: Could not find /imageProcessing/image-processing.fxml");
                // If the folder is named differently (e.g. all lowercase), update the string above
                return;
            }

            // 3. Load and set the scene
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            System.out.println("Returning to Image Processing screen.");

        } catch (Exception e) {
            System.err.println("Navigation Error (Back): " + e.getMessage());
            e.printStackTrace();
        }



    }

    private void generateDummyData() {
        for (int i = 1; i <= 35; i++) {
            masterData.add(new DummyPatient("Patient " + i, LocalDate.now().minusDays(i*2), DummyPatient.Criticality.values()[i%5]));
        }
    }
}