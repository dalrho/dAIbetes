package org.example.daibetes.modules.detection.imageProcessing.implementations;

import javafx.scene.image.*;
import javafx.scene.paint.Color;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;
import java.util.Arrays;

public class DenoiseDecorator extends ImageFilterDecorator {
    private final double level; // 0 to 1

    public DenoiseDecorator(ImagePreprocessor inner, double level) {
        super(inner);
        this.level = level;
    }

    @Override
    public Image process(Image input) {
        Image img = inner.process(input);
        if (level <= 0) return img;

        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        WritableImage output = new WritableImage(w, h);
        PixelReader reader = img.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        // 3x3 RGB Median Filter
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                writer.setColor(x, y, getMedianRGB(reader, x, y));
            }
        }
        return output;
    }

    private Color getMedianRGB(PixelReader r, int x, int y) {
        double[] reds = new double[9];
        double[] greens = new double[9];
        double[] blues = new double[9];
        int k = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Color c = r.getColor(x + i, y + j);
                reds[k] = c.getRed();
                greens[k] = c.getGreen();
                blues[k] = c.getBlue();
                k++;
            }
        }
        Arrays.sort(reds);
        Arrays.sort(greens);
        Arrays.sort(blues);
        // Blend median with original based on 'level'
        Color original = r.getColor(x, y);
        Color median = Color.color(reds[4], greens[4], blues[4]);
        return original.interpolate(median, level);
    }
}