package org.example.daibetes.modules.doctor.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.core.database.TestDAO;

import java.io.File;


public class DoctorDashboardController {


    // These are inside the new diagnosis pane
    @FXML private Button closePopupButton;
    @FXML private Button uploadImageButton;
    @FXML private Button openCameraButton;
    private int selectedPatientId;
    private int loggedInDoctorId;

    // This is the whole new diagnosis pane from FXML - assuming nga mao ni naa sa fxml
    @FXML private javafx.scene.layout.Pane diagnosisPopup;

    private File selectedImage;



    public void handleNewDiagnosis(ActionEvent actionEvent) {
        diagnosisPopup.setVisible(true);
        diagnosisPopup.setManaged(true);

    }

    public void uploadImage(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Scan Image");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files (*.png, *.jpg, *.jpeg)",
                        "*.png", "*.jpg", "*.jpeg"
                )
        );

        selectedImage = fileChooser.showOpenDialog(uploadImageButton.getScene().getWindow());

        if (selectedImage != null) {

            // 1 = raw scan image
            ImageDAO imageDAO = new ImageDAO();
            int rawImgId = imageDAO.createImage(selectedImage, 1);

            if (rawImgId == -1) {
                showAlert("Error", "Failed to save image.");
                return;
            }

            // automatically create test record
            int patientId = selectedPatientId;
            int doctorId = loggedInDoctorId;

            TestDAO testDAO = new TestDAO();
            int testId = testDAO.createTest(patientId, doctorId, rawImgId);

            if (testId == -1) {
                showAlert("Error", "Failed to create test record.");
                return;
            }

        }
    }
    public void setSelectedPatientId(int selectedPatientId) {
        this.selectedPatientId = selectedPatientId;
    }

    public void setLoggedInDoctorId(int loggedInDoctorId) {
        this.loggedInDoctorId = loggedInDoctorId;
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleViewRecords(ActionEvent actionEvent) {
    }

    public void handleReports(ActionEvent actionEvent) {
    }


    public void handleLogout(ActionEvent actionEvent) {


    }
}
