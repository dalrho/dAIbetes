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
       int w = (int)img.getWidth();
       int h = (int)img.getHeight();
        WritableImage output = new WritableImage(w, h);
        PixelWriter writer = output.getPixelWriter();
        PixelReader reader = img.getPixelReader();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = reader.getColor(x, y);
                writer.setColor(x, y, new Color(
                        clamp(c.getRed() + level),
                        clamp(c.getGreen() + level),
                        clamp(c.getBlue() + level),
                        c.getOpacity()
                ));
            }
        }
        return output;
    }

    private double clamp(double val) { return Math.max(0, Math.min(1, val)); }

}
