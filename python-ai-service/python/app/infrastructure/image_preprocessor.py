# app/infrastructure/image_preprocessor.py

from torchvision import transforms

def get_transform():
    """
    Constructs a standard ImageNet pre-processing pipeline for input images.

    The pipeline performs the following steps:
    1. Resizes the image to 224x224 pixels.
    2. Converts the image to a PyTorch tensor (scaling pixel values to [0, 1]).
    3. Normalizes the tensor using the mean and standard deviation of the ImageNet dataset.

    Returns:
        torchvision.transforms.Compose: A composition of image transformations.
    """
    return transforms.Compose([
        transforms.Resize((224, 224)),
        transforms.ToTensor(),
        transforms.Normalize(
            mean=[0.485, 0.456, 0.406],
            std=[0.229, 0.224, 0.225]
        )
    ])