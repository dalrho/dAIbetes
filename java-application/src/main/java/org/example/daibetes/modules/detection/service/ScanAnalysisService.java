package org.example.daibetes.modules.detection.service;

import javafx.scene.image.Image;
import org.example.daibetes.modules.detection.imageProcessing.implementations.*;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;
public class ScanAnalysisService {
    public Image applyEnhancements(Image raw, boolean gray, double brightness,
                                   double clahe, double sharp, double denoise, int resizeWidth) {
        ImagePreprocessor pipeline = new BasePreprocessor();

        if (resizeWidth > 0) pipeline = new ResizeDecorator(pipeline, resizeWidth);
        if (gray) pipeline = new GrayscaleDecorator(pipeline);
        if (brightness != 0) pipeline = new BrightnessDecorator(pipeline, brightness);
        if (clahe != 1.0) pipeline = new CLAHEDecorator(pipeline, clahe);
        if (sharp > 0) pipeline = new SharpenerDecorator(pipeline, sharp);
        if (denoise > 0) pipeline = new DenoiseDecorator(pipeline, denoise);

        return pipeline.process(raw);
    }
}