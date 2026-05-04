package org.example.daibetes.modules.detection.imageProcessing.implementations;

import javafx.scene.image.*;
import javafx.scene.paint.Color;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;

public class CLAHEDecorator extends ImageFilterDecorator {
    private final double contrast; // 1.0 is neutral, > 1.0 is high contrast

    public CLAHEDecorator(ImagePreprocessor inner, double contrast) {
        super(inner);
        this.contrast = contrast;
    }

    @Override
    public Image process(Image input) {
        Image img = inner.process(input);
        if (contrast == 1.0) return img;

        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        WritableImage output = new WritableImage(w, h);
        PixelReader reader = img.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = reader.getColor(x, y);
                // Linear Contrast Formula: (channel - 0.5) * contrast + 0.5
                writer.setColor(x, y, Color.color(
                        clamp((c.getRed() - 0.5) * contrast + 0.5),
                        clamp((c.getGreen() - 0.5) * contrast + 0.5),
                        clamp((c.getBlue() - 0.5) * contrast + 0.5),
                        c.getOpacity()
                ));
            }
        }
        return output;
    }

    private double clamp(double v) { return Math.max(0, Math.min(1, v)); }
}