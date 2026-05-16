package org.example.daibetes.app;

import javafx.scene.image.Image;
import org.example.daibetes.core.domain.Appointment;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.Appointment;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppContext {
    private static AppContext instance;

    private Image selectedImage;
    private int selectedImageId;
    private int currentTestId;
    private File selectedImageFile;
    private String selectedPatientName;
    private String diagnosisNotes;

    private User currentUser;
    private List<File> galleryFiles = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();

    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    // --- Image is for displaying in JavaFX ---
    public Image getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Image image) {
        this.selectedImage = image;
    }

    public int getSelectedImageId() {
        return selectedImageId;
    }

    public void setSelectedImageId(int selectedImageId) {
        this.selectedImageId = selectedImageId;
    }

    // --- File is for uploading in FastAPI ---
    public File getSelectedImageFile() {
        return selectedImageFile;
    }

    public void setSelectedImageFile(File selectedImageFile) {
        this.selectedImageFile = selectedImageFile;
    }

    // --- Current Test ---
    public int getCurrentTestId() {
        return currentTestId;
    }

    public void setCurrentTestId(int currentTestId) {
        this.currentTestId = currentTestId;
    }

    // --- Session ---
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
        this.selectedPatientName = null; // Clear diagnosis data
        this.diagnosisNotes = null;      // Clear diagnosis data
        this.galleryFiles.clear();
    }

    // --- Gallery ---
// --- Gallery ---
    public List<File> getGalleryFiles() { return galleryFiles; }
    public void setGalleryFiles(List<File> files) { this.galleryFiles = files; }

    // --- Appointments (calendar) ---
    // Loaded fresh from DB by PatientCalendarController on each open.
    // Stored here so the list survives navigation back to the calendar.


    public List<org.example.daibetes.core.domain.Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(
            List<org.example.daibetes.core.domain.Appointment> appointments) {
        this.appointments = appointments;
    }

    public void addAppointment(
            org.example.daibetes.core.domain.Appointment appointment) {
        this.appointments.add(appointment);
    }

    /**
     * Updates the status of an appointment in the in-memory list by request ID.
     * Called by the doctor calendar after accepting or rejecting a request.
     * The actual DB update is handled by ConsultationRequestDAO — this just
     * keeps the in-memory list consistent without a full reload.
     */
    public void updateAppointmentStatus(int requestId, boolean accepted) {
        for (org.example.daibetes.core.domain.Appointment app : appointments) {
            if (app.getRequestId() == requestId) {
                // Rebuild with updated status — Appointment fields are final
                // so we replace the entry in the list
                int idx = appointments.indexOf(app);
                appointments.set(idx, new org.example.daibetes.core.domain.Appointment(
                        app.getRequestId(),
                        app.getPatientId(),
                        app.getDoctorId(),
                        app.getPatientName(),
                        app.getDoctorName(),
                        app.getDate(),
                        app.getTime(),
                        accepted
                                ? org.example.daibetes.core.domain.Appointment.Status.ACCEPTED
                                : org.example.daibetes.core.domain.Appointment.Status.REJECTED
                ));
                break;
            }
        }
    }

    public String getSelectedPatientName() {
        return selectedPatientName;
    }

    public void setSelectedPatientName(String selectedPatientName) {
        this.selectedPatientName = selectedPatientName;
    }

    public String getDiagnosisNotes() {
        return diagnosisNotes;
    }

    public void setDiagnosisNotes(String diagnosisNotes) {
        this.diagnosisNotes = diagnosisNotes;
    }

}
