package org.example.daibetes.modules.detection.imageProcessing.implementations;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;

public class ResizeDecorator extends ImageFilterDecorator {
    private final int targetWidth;

    public ResizeDecorator(ImagePreprocessor inner, int targetWidth) {
        super(inner);
        this.targetWidth = targetWidth;
    }

    @Override
    public Image process(Image input) {
        Image img = inner.process(input);
        if (targetWidth <= 0 || (int)img.getWidth() == targetWidth) return img;

        ImageView tempView = new ImageView(img);
        tempView.setFitWidth(targetWidth);
        tempView.setPreserveRatio(true);
        tempView.setSmooth(true);
        return tempView.snapshot(null, null);
    }
}