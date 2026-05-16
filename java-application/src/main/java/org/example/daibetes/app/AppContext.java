package org.example.daibetes.app;

import javafx.scene.image.Image;
import org.example.daibetes.core.domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppContext {
    private static AppContext instance;

    private Image selectedImage;
    private User currentUser;
    private List<File> galleryFiles = new ArrayList<>();
    private File selectedImageFile;
    private int currentTestId = -1;

    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // --- Existing ---
    public Image getSelectedImage() { return selectedImage; }
    public void setSelectedImage(Image image) { this.selectedImage = image; }

    // --- Selected image file (for AI inference — needs the raw File, not Image) ---
    public File getSelectedImageFile() { return selectedImageFile; }
    public void setSelectedImageFile(File file) {
        this.selectedImageFile = file;
        // Keep selectedImage in sync if a file is set
        if (file != null) {
            this.selectedImage = new Image(file.toURI().toString());
        }
    }

    // --- Current test ID (set after tblTests insert, used by report screens) ---
    public int  getCurrentTestId()          { return currentTestId; }
    public void setCurrentTestId(int testId){ this.currentTestId = testId; }

    // --- Session ---
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public void clearSession() { this.currentUser = null; }

    // --- Gallery ---
    public List<File> getGalleryFiles() { return galleryFiles; }
    public void setGalleryFiles(List<File> files) { this.galleryFiles = files; }

    // --- Appointments (calendar) ---
    // Loaded fresh from DB by PatientCalendarController on each open.
    // Stored here so the list survives navigation back to the calendar.
    private List<org.example.daibetes.core.domain.Appointment> appointments = new ArrayList<>();

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
}