# app/infrastructure/model_loader.py

import torch

def load_model(model_path: str, device: str = "cpu"):
    """
    Loads a serialized PyTorch model and prepares it for inference.

    Args:
        model_path (str): The filesystem path to the saved model file (.pt or .pth).
        device (str): The target hardware for the model (e.g., "cpu", "cuda", or "mps").
                      Defaults to "cpu".

    Returns:
        tuple: A tuple containing:
            - model (torch.nn.Module): The loaded model set to evaluation mode.
            - device (torch.device): The device object the model was loaded onto.
    """
    device = torch.device(device)

    # Load the model architecture and weights
    model = torch.load(
        model_path,
        map_location=device,
        weights_only=False
    )

    # Set to evaluation mode to disable dropout and batch normalization layers
    model.eval()
    return model, device