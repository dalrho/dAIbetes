import torch
import numpy as np
from PIL import Image

from app.domain.dr_result import DRResult

class DRClassificationService:

    def __init__(self, model, device, transform):
        self.model = model
        self.device = device
        self.transform = transform

    def predict(self, image: Image.Image):

        image = image.convert("RGB")
        tensor = self.transform(image).unsqueeze(0).to(self.device)

        with torch.no_grad():
            outputs = self.model(tensor)
            probs = torch.nn.functional.softmax(outputs, dim=1)

        probs = probs.cpu().numpy()[0]
        predicted_class = int(np.argmax(probs))

        return DRResult(predicted_class, probs)