# app/api/inference_controller.py

from fastapi import APIRouter, UploadFile, File
from PIL import Image
import numpy as np

router = APIRouter()

@router.post("/predict/dr")
async def predict_dr(file: UploadFile = File(...)):
    image = Image.open(file.file)

    predicted_class, probs = classifier.predict(image)

    return {
        "predicted_class": predicted_class,
        "probabilities": probs.tolist()
    }