import os, cv2, numpy as np, pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from collections import Counter

import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import Dataset, DataLoader, WeightedRandomSampler
import torchvision.transforms as T

import timm
import albumentations as A
from albumentations.pytorch import ToTensorV2
from sklearn.model_selection import StratifiedKFold
from sklearn.metrics import cohen_kappa_score, confusion_matrix
import warnings
warnings.filterwarnings('ignore')

DEVICE = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
print(f"Device: {DEVICE}")  # Should print: Device: cuda

df = pd.read_csv('/kaggle/input/diabetic-retinopathy-detection/trainLabels.csv')
df.columns = ['image', 'level']

print(f"Total images : {len(df)}")
print(f"Unique patients: {df['image'].str.extract(r'(\d+)_')[0].nunique()}")
print("\nClass distribution:")
print(df['level'].value_counts().sort_index())