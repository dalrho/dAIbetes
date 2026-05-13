package org.example.daibetes.modules.ai.dto;

public class AIResponseDTO {

    private PredictionDTO prediction;
    private String clinical_guidance;

    public AIResponseDTO() {}

    public PredictionDTO getPrediction() {
        return prediction;
    }

    public void setPrediction(PredictionDTO prediction) {
        this.prediction = prediction;
    }

    public String getClinical_guidance() {
        return clinical_guidance;
    }

    public void setClinical_guidance(String clinical_guidance) {
        this.clinical_guidance = clinical_guidance;
    }
}