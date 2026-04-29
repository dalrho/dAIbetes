package org.example.daibetes.modules.detection.service;

import javafx.scene.image.Image;
import org.example.daibetes.modules.detection.imageProcessing.implementations.BasePreprocessor;
import org.example.daibetes.modules.detection.imageProcessing.implementations.BrightnessDecorator;
import org.example.daibetes.modules.detection.imageProcessing.implementations.GrayscaleDecorator;
import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;

public class ScanAnalysisService {

    public Image applyEnhancements(Image raw, boolean gray, double brightness, double contrast) {
        ImagePreprocessor pipeline = new BasePreprocessor();

        if (gray) {
            pipeline = new GrayscaleDecorator(pipeline);
        }
        if (brightness != 0) {
            pipeline = new BrightnessDecorator(pipeline, brightness);
        }

        return pipeline.process(raw);
    }
}