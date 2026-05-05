# app/api/inference_controller.py

from fastapi import APIRouter, UploadFile, File
from PIL import Image
import numpy as np

router = APIRouter()

@router.post("/predict/dr")
async def predict_dr(file: UploadFile = File(...)):
    """
    Handles image uploads and returns model predictions for Diabetic Retinopathy (DR).

    Args:
        file (UploadFile): The image file uploaded by the user.

    Returns:
        dict: A dictionary containing the predicted class label and a list of
              class probabilities.
    """
    # Open the uploaded image file using PIL
    image = Image.open(file.file)

    # Perform inference using the global classifier instance
    predicted_class, probs = classifier.predict(image)

    return {
        "predicted_class": predicted_class,
        "probabilities": probs.tolist()
    }