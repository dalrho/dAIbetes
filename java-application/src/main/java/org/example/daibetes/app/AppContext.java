package org.example.daibetes.app;

import javafx.scene.image.Image;
import org.example.daibetes.core.domain.Appointment;
import org.example.daibetes.core.domain.User;

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

    private User currentUser;
    private List<File> galleryFiles = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();

    private AppContext() {
        // Initialize with some mock data for demonstration
        appointments.add(new Appointment(1, 1, 1, "Harold", "Dr. Cruz", LocalDate.of(2026, 5, 18), "3:00PM", false));
        appointments.add(new Appointment(2, 2, 1, "Alice", "Dr. Cruz", LocalDate.of(2026, 5, 19), "10:00AM", true));
        appointments.add(new Appointment(3, 3, 1, "Bob", "Dr. Cruz", LocalDate.of(2026, 5, 20), "11:30AM", false));
        appointments.add(new Appointment(4, 4, 1, "Charlie", "Dr. Cruz", LocalDate.of(2026, 5, 20), "2:00PM", true));
    }

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
        this.galleryFiles.clear();
    }

    // --- Gallery ---
    public List<File> getGalleryFiles() {
        return galleryFiles;
    }

    public void setGalleryFiles(List<File> files) {
        this.galleryFiles = files;
    }

    // --- Appointments ---
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void addAppointment(Appointment appointment) {
        this.appointments.add(appointment);
    }

    public void updateAppointmentStatus(int id, boolean accepted) {
        for (Appointment app : appointments) {
            if (app.getId() == id) {
                app.setAccepted(accepted);
                break;
            }
        }
    }
}