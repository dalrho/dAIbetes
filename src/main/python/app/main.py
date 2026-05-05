from app.api.inference_controller import router
from app.infrastructure.model_loader import load_model
from app.infrastructure.image_preprocessor import get_transform
from app.application.dr_classification_service import DRClassificationService
import os

BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
MODEL_PATH = os.path.join(BASE_DIR, "models", "diabetic_retinopathy_full_model.pth")

app = FastAPI()