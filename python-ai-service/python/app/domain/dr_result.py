class DRResult:

    CLASS_LABELS = {
        0: "No DR",
        1: "Mild DR",
        2: "Moderate DR",
        3: "Severe DR",
        4: "PDR"
    }

    def __init__(self, predicted_class, probabilities):

        self.predicted_index = predicted_class
        self.predicted_class = self.CLASS_LABELS[predicted_class]

        self.probabilities = {
            self.CLASS_LABELS[i]: float(probabilities[i])
            for i in range(len(probabilities))
        }

        self.confidence = float(max(probabilities))

    def to_dict(self):

        return {
            "predicted_class": self.predicted_class,
            "confidence": self.confidence,
            "class_probabilities": self.probabilities
        }