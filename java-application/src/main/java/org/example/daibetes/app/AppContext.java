package org.example.daibetes.app;

import javafx.scene.image.Image;
import org.example.daibetes.core.domain.Appointment;
import org.example.daibetes.core.domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppContext {
    private static AppContext instance;

    // --- Selected Image ---
    private Image selectedImage;
    private File selectedImageFile;
    private int selectedImageId;

    // --- Current Test ---
    private int currentTestId;

    // --- Selected Patient for New Diagnosis ---
    private int selectedPatientId;
    private String selectedPatientName;

    // --- Diagnosis ---
    private String diagnosisNotes;

    // --- Session ---
    private User currentUser;

    // --- Gallery ---
    private List<File> galleryFiles = new ArrayList<>();

    // --- Appointments ---
    private List<Appointment> appointments = new ArrayList<>();

    private int selectedRecordsPatientId;
    private String selectedRecordsPatientName;
    private int selectedRecordsDoctorId;
    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    // =========================
    // SELECTED IMAGE
    // =========================

    public Image getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Image image) {
        this.selectedImage = image;
    }

    public File getSelectedImageFile() {
        return selectedImageFile;
    }

    public void setSelectedImageFile(File selectedImageFile) {
        this.selectedImageFile = selectedImageFile;
    }

    public int getSelectedImageId() {
        return selectedImageId;
    }

    public void setSelectedImageId(int selectedImageId) {
        this.selectedImageId = selectedImageId;
    }

    // =========================
    // CURRENT TEST
    // =========================

    public int getCurrentTestId() {
        return currentTestId;
    }

    public void setCurrentTestId(int currentTestId) {
        this.currentTestId = currentTestId;
    }

    // =========================
    // SELECTED PATIENT
    // =========================

    public int getSelectedPatientId() {
        return selectedPatientId;
    }

    public void setSelectedPatientId(int selectedPatientId) {
        this.selectedPatientId = selectedPatientId;
    }

    public String getSelectedPatientName() {
        return selectedPatientName;
    }

    public void setSelectedPatientName(String selectedPatientName) {
        this.selectedPatientName = selectedPatientName;
    }

    // =========================
    // DIAGNOSIS NOTES
    // =========================

    public String getDiagnosisNotes() {
        return diagnosisNotes;
    }

    public void setDiagnosisNotes(String diagnosisNotes) {
        this.diagnosisNotes = diagnosisNotes;
    }

    // =========================
    // SESSION
    // =========================

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void clearSession() {
        this.currentUser = null;

        this.selectedImage = null;
        this.selectedImageFile = null;
        this.selectedImageId = 0;

        this.currentTestId = 0;

        this.selectedPatientId = 0;
        this.selectedPatientName = null;

        this.diagnosisNotes = null;

        this.galleryFiles.clear();
        this.appointments.clear();
    }

    // =========================
    // GALLERY
    // =========================

    public List<File> getGalleryFiles() {
        return galleryFiles;
    }

    public void setGalleryFiles(List<File> files) {
        this.galleryFiles = files;
    }

    // =========================
    // APPOINTMENTS
    // =========================

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public void updateAppointmentStatus(int requestId, boolean accepted) {
        for (Appointment app : appointments) {
            if (app.getRequestId() == requestId) {
                int index = appointments.indexOf(app);

                appointments.set(index, new Appointment(
                        app.getRequestId(),
                        app.getPatientId(),
                        app.getDoctorId(),
                        app.getPatientName(),
                        app.getDoctorName(),
                        app.getDate(),
                        app.getTime(),
                        accepted
                                ? Appointment.Status.ACCEPTED
                                : Appointment.Status.REJECTED
                ));

                break;
            }
        }
    }

    public int getSelectedRecordsPatientId() {
        return selectedRecordsPatientId;
    }

    public void setSelectedRecordsPatientId(int selectedRecordsPatientId) {
        this.selectedRecordsPatientId = selectedRecordsPatientId;
    }

    public String getSelectedRecordsPatientName() {
        return selectedRecordsPatientName;
    }

    public void setSelectedRecordsPatientName(String selectedRecordsPatientName) {
        this.selectedRecordsPatientName = selectedRecordsPatientName;
    }

    public int getSelectedRecordsDoctorId() {
        return selectedRecordsDoctorId;
    }

    public void setSelectedRecordsDoctorId(int selectedRecordsDoctorId) {
        this.selectedRecordsDoctorId = selectedRecordsDoctorId;
    }
}