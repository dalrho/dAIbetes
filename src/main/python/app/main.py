from fastapi import FastAPI
import os
from app.api.inference_controller import router
from app.infrastructure.model_loader import load_model
from app.infrastructure.image_preprocessor import get_transform
from app.application.dr_classification_service import DRClassificationService

# --- Configuration ---
# Set up absolute paths for model artifact resolution
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
MODEL_PATH = os.path.join(BASE_DIR, "models", "diabetic_retinopathy_full_model.pth")

# --- Application Setup ---
app = FastAPI(
    title="Diabetic Retinopathy Classification API",
    description="Inference service for detecting DR stages from retinal images."
)

# --- Resource Initialization ---
# Initialize core ML components during startup to ensure low-latency inference
model, device = load_model(MODEL_PATH)
transform = get_transform()

# Instantiate the application service layer
classifier = DRClassificationService(model, device, transform)

# --- Dependency Injection ---
# Manually inject the service instance into the controller module
# to provide routing logic access to the classifier
import app.api.inference_controller as controller
controller.classifier = classifier

# Register API endpoints
app.include_router(router)