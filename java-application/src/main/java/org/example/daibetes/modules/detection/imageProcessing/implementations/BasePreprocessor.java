package org.example.daibetes.modules.detection.imageProcessing.implementations;

import javafx.scene.image.Image;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;

public class BasePreprocessor implements ImagePreprocessor {
    @Override
    public Image process(Image input) {
        return input;
    }
}
