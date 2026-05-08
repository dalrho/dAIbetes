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

    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // --- Existing ---
    public Image getSelectedImage() { return selectedImage; }
    public void setSelectedImage(Image image) { this.selectedImage = image; }

    // --- Session ---
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }
    public void clearSession() { this.currentUser = null; }

    // --- Gallery ---
    public List<File> getGalleryFiles() { return galleryFiles; }
    public void setGalleryFiles(List<File> files) { this.galleryFiles = files; }
}