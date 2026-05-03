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

LABEL_NAMES = {0: 'No DR', 1: 'Mild', 2: 'Moderate', 3: 'Severe', 4: 'Proliferative'}
COLORS = ['#4C9BE8', '#5DCAA5', '#EF9F27', '#E8593C', '#A855F7']

counts = df['level'].value_counts().sort_index()
pct    = counts / counts.sum() * 100

fig, axes = plt.subplots(1, 2, figsize=(14, 5))
fig.suptitle('Diabetic Retinopathy — Class Distribution', fontsize=14, fontweight='bold')

# Bar chart
bars = axes[0].bar([LABEL_NAMES[i] for i in counts.index], counts.values, color=COLORS)
axes[0].set_ylabel('Image count')
axes[0].set_title('Absolute count per class')
for bar, p in zip(bars, pct.values):
    axes[0].text(bar.get_x() + bar.get_width()/2, bar.get_height() + 100,
                 f'{p:.1f}%', ha='center', va='bottom', fontsize=10)

# Pie chart
axes[1].pie(counts.values, labels=[LABEL_NAMES[i] for i in counts.index],
            colors=COLORS, autopct='%1.1f%%', startangle=90)
axes[1].set_title('Class proportion')

plt.tight_layout()
plt.savefig('class_distribution.png', dpi=120, bbox_inches='tight')
plt.show()
print("\n⚠️  Class 0 dominates — ~73% of all images. Must handle imbalance!")