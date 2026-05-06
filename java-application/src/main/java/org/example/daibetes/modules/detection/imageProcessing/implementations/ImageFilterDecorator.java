package org.example.daibetes.modules.detection.imageProcessing.implementations;

import org.example.daibetes.modules.detection.imageProcessing.interfaces.ImagePreprocessor;

import java.awt.image.ImageFilter;

public abstract class ImageFilterDecorator implements ImagePreprocessor {
    protected ImagePreprocessor inner;
    public ImageFilterDecorator(ImagePreprocessor inner) {
        this.inner = inner;
    }
}
