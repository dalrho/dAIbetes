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