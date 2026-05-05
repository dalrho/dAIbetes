package org.example.daibetes.modules.detection.imageProcessing.implementations;

import javafx.scene.image.*;
import javafx.scene.paint.Color;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;

public class SharpenerDecorator extends ImageFilterDecorator {
    private final double intensity;

    public SharpenerDecorator(ImagePreprocessor inner, double intensity) {
        super(inner);
        this.intensity = intensity;
    }

    @Override
    public Image process(Image input) {
        Image img = inner.process(input);
        if (intensity == 0) return img;
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        WritableImage output = new WritableImage(w, h);
        PixelReader reader = img.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                writer.setColor(x, y, applySharpen(reader, x, y));
            }
        }
        return output;
    }

    private Color applySharpen(PixelReader r, int x, int y) {
        double red = 0, green = 0, blue = 0;
        // Sharpen Kernel: center is 4 + intensity, neighbors are -1
        double center = 4 + intensity;
        double[][] kernel = {{0, -1, 0}, {-1, center, -1}, {0, -1, 0}};

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Color c = r.getColor(x + i, y + j);
                red += c.getRed() * kernel[i + 1][j + 1];
                green += c.getGreen() * kernel[i + 1][j + 1];
                blue += c.getBlue() * kernel[i + 1][j + 1];
            }
        }
        return Color.color(clamp(red), clamp(green), clamp(blue));
    }
    private double clamp(double v) { return Math.max(0, Math.min(1, v)); }
}