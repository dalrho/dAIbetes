package org.example.daibetes.modules.detection.imageProcessing.implementations;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;

public class GrayscaleDecorator extends ImageFilterDecorator{
    public GrayscaleDecorator(ImagePreprocessor inner) {
        super(inner);
    }

    @Override
    public Image process(Image input) {
        Image img = inner.process(input);
        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        WritableImage output = new WritableImage(w, h);
        PixelReader pixelReader = img.getPixelReader();
        PixelWriter pixelWriter = output.getPixelWriter();

        for(int y = 0; y <h; y++){
            for(int x = 0; x< w; x++){
                pixelWriter.setColor(x,y, pixelReader.getColor(x,y).grayscale());
            }
        }
        return output;
    }
}
