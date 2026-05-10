package org.example.daibetes.modules.ai.dto;

import java.util.Map;

public class PredictionDTO {

    private String predicted_class;
    private double confidence;
    private Map<String, Double> class_probabilities;

    public String getPredicted_class() {
        return predicted_class;
    }

    public void setPredicted_class(String predicted_class) {
        this.predicted_class = predicted_class;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Map<String, Double> getClass_probabilities() {
        return class_probabilities;
    }

    public void setClass_probabilities(Map<String, Double> class_probabilities) {
        this.class_probabilities = class_probabilities;
    }
}