package org.example.daibetes.app;

import javafx.scene.image.Image;
import org.example.daibetes.core.domain.User;

import java.io.File;
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
        this.galleryFiles.clear();
    }

    // --- Gallery ---
    public List<File> getGalleryFiles() {
        return galleryFiles;
    }

    public void setGalleryFiles(List<File> files) {
        this.galleryFiles = files;
    }
}