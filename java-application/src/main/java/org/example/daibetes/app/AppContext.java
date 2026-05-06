package org.example.daibetes.app;

import javafx.scene.image.Image;

public class AppContext {
    private static AppContext instance;
    private Image selectedImage;

    private AppContext() {}

    public static AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    public Image getSelectedImage() { return selectedImage; }
    public void setSelectedImage(Image image) { this.selectedImage = image; }
}
