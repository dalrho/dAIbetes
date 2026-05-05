# app/domain/dr_result.py

class DRResult:
    """
    Represents the output of a Diabetic Retinopathy diagnostic analysis.

    Attributes:
        predicted_class (int/str): The identified category of the diagnosis.
        probabilities (list[float]): The confidence scores for each possible class.
    """

    def __init__(self, predicted_class, probabilities):
        """
        Initializes the DRResult with classification data.
        """
        self.predicted_class = predicted_class
        self.probabilities = probabilities

    def to_dict(self):
        """
        Serializes the result object into a dictionary format.

        Returns:
            dict: A dictionary containing the predicted class and probabilities.
        """
        return {
            "predicted_class": self.predicted_class,
            "probabilities": self.probabilities
        }