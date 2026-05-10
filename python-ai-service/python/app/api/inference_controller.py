# app/api/inference_controller.py

from fastapi import APIRouter, UploadFile, File
from PIL import Image

from app.application.prompt_builder import build_clinical_guidance_prompt

router = APIRouter()

classifier = None
gemini_service = None

@router.post("/predict/dr")
async def predict_dr(file: UploadFile = File(...)):
    """
    Handles image uploads and returns model predictions for Diabetic Retinopathy (DR).

    Args:
        file (UploadFile): The image file uploaded by the user.

    Returns:
        prediction of the AI Model and AI-generated text from the prompt
    """
    # Open the uploaded image file using PIL
    image = Image.open(file.file)

    # Perform inference using the global classifier instance
    dr_result = classifier.predict(image)

    prompt = build_clinical_guidance_prompt(dr_result)

    try:
        clinical_guidance = gemini_service.generate_clinical_guidance(prompt)
    except:
        clinical_guidance = "Clinical guidance unavailable."

    return {
        "prediction": dr_result.to_dict(),
        "clinical_guidance": clinical_guidance
    }