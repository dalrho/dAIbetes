package org.example.daibetes.modules.detection.imageProcessing.implementations;

import javafx.scene.image.*;
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

        if (img == null) return null;
        if (targetWidth <= 0 || (int)img.getWidth() == targetWidth) return img;

        double oldWidth = img.getWidth();
        double oldHeight = img.getHeight();
        double ratio = oldHeight / oldWidth;
        int targetHeight = (int) (targetWidth * ratio);

        WritableImage output = new WritableImage(targetWidth, targetHeight);
        PixelReader reader = img.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int sourceX = (int) (x * oldWidth / targetWidth);
                int sourceY = (int) (y * oldHeight / targetHeight);
                writer.setArgb(x, y, reader.getArgb(sourceX, sourceY));
            }
        }

        return output;
    }
}