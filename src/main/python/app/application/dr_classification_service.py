# app/application/dr_classification_service.py

import torch
import numpy as np
from PIL import Image

class DRClassificationService:
    """
    Service layer responsible for handling the business logic of
    Diabetic Retinopathy (DR) image classification using a PyTorch model.
    """

    def __init__(self, model, device, transform):
        """
        Initializes the service with necessary model components.

        Args:
            model: The pre-trained PyTorch model instance.
            device: The target device for computation (e.g., 'cuda' or 'cpu').
            transform: The composition of image preprocessing steps.
        """
        self.model = model
        self.device = device
        self.transform = transform

    def predict(self, image: Image.Image):
        """
        Processes an input image and performs model inference.

        Args:
            image (Image.Image): Raw PIL image object.

        Returns:
            tuple: (predicted_class: int, probabilities: np.ndarray)
        """
        # Ensure image is in RGB format and apply preprocessing transforms
        image = image.convert("RGB")
        tensor = self.transform(image).unsqueeze(0).to(self.device)

        # Execute model inference without gradient tracking
        with torch.no_grad():
            outputs = self.model(tensor)
            probs = torch.nn.functional.softmax(outputs, dim=1)

        # Convert result to CPU-bound numpy array for standard processing
        probs = probs.cpu().numpy()[0]
        predicted_class = int(np.argmax(probs))

        return predicted_class, probs