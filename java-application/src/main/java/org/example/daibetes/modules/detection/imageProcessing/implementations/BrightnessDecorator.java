package org.example.daibetes.modules.detection.imageProcessing.implementations;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;

public class BrightnessDecorator extends ImageFilterDecorator {
    private final double level;
    public BrightnessDecorator(ImagePreprocessor inner, double level) {
        super(inner);
        this.level = level;
    }

    @Override
    public Image process(Image input) {
        Image img = inner.process(input);
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();

        WritableImage output = new WritableImage(w, h);
        PixelReader pr = img.getPixelReader();
        PixelWriter pw = output.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = pr.getColor(x, y);
                // brightnessValue is likely your double (e.g., 0.2 for +20%)
                double r = Math.min(1.0, Math.max(0.0, c.getRed() + level));
                double g = Math.min(1.0, Math.max(0.0, c.getGreen() + level));
                double b = Math.min(1.0, Math.max(0.0, c.getBlue() + level));

                pw.setColor(x, y, new Color(r, g, b, c.getOpacity()));
            }
        }
        return output;
    }

    private double clamp(double val) { return Math.max(0, Math.min(1, val)); }

}
