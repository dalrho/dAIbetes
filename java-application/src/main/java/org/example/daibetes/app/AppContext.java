package org.example.daibetes.app;

import javafx.scene.image.Image;

import java.io.File;
import java.util.List;

public class AppContext {
    private static AppContext instance;
    private Image selectedImage;
    private List<File> galleryFiles = new java.util.ArrayList<>();

    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    public Image getSelectedImage() { return selectedImage; }
    public void setSelectedImage(Image image) { this.selectedImage = image; }

    public List<java.io.File> getGalleryFiles() { return galleryFiles; }
    public void setGalleryFiles(List<java.io.File> files) { this.galleryFiles = files; }

    private int selectedImageId;

    public int getSelectedImageId() {
        return selectedImageId;
    }

    public void setSelectedImageId(int selectedImageId) {
        this.selectedImageId = selectedImageId;
    }
}
