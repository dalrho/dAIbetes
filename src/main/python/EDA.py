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

df['eye']     = df['image'].str.extract(r'_(left|right)')
df['patient'] = df['image'].str.extract(r'(\d+)_')

# Check if DR severity differs between eyes
print("Mean DR grade by eye side:")
print(df.groupby('eye')['level'].mean())

# Sample images per class to visually audit
IMG_DIR = '/kaggle/input/diabetic-retinopathy-detection/train'
fig, axes = plt.subplots(1, 5, figsize=(20, 4))
fig.suptitle('Sample image per DR grade (raw)', fontsize=13)

for grade in range(5):
    sample = df[df['level'] == grade].sample(1).iloc[0]
    path   = os.path.join(IMG_DIR, sample['image'] + '.jpeg')
    img    = cv2.cvtColor(cv2.imread(path), cv2.COLOR_BGR2RGB)
    axes[grade].imshow(img)
    axes[grade].set_title(f'Grade {grade}\n{LABEL_NAMES[grade]}', fontsize=10)
    axes[grade].axis('off')
plt.tight_layout()
plt.show()

def preprocess_fundus(img, img_size=512):
    """
    Ben Graham's preprocessing — used in the winning Kaggle DR solution.
    Subtracts local average to remove illumination variation across cameras.
    """
    img = cv2.resize(img, (img_size, img_size))

    # Local average subtraction: amplifies vessel/lesion detail
    blurred = cv2.GaussianBlur(img, (0, 0), img_size / 30)
    img = cv2.addWeighted(img, 4, blurred, -4, 128)

    # Circular mask: crops out black border artifacts
    mask = np.zeros(img.shape, dtype=np.uint8)
    h, w = img.shape[:2]
    cv2.circle(mask, (w // 2, h // 2), int(min(w, h) * 0.47), (1, 1, 1), -1)
    img = img * mask + 128 * (1 - mask)

    return img.astype(np.uint8)

# Visualize before vs after
sample_path = os.path.join(IMG_DIR, df.sample(1).iloc[0]['image'] + '.jpeg')
raw = cv2.cvtColor(cv2.imread(sample_path), cv2.COLOR_BGR2RGB)
processed = preprocess_fundus(raw.copy())

fig, axes = plt.subplots(1, 2, figsize=(12, 5))
axes[0].imshow(raw);       axes[0].set_title('Raw image');           axes[0].axis('off')
axes[1].imshow(processed); axes[1].set_title('After Ben Graham');    axes[1].axis('off')
plt.suptitle('Preprocessing effect — vessel detail enhancement', fontsize=12)
plt.tight_layout()
plt.show()