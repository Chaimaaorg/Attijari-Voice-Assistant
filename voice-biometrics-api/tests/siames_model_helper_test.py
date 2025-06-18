import torch
from torch.utils.data import DataLoader, Dataset
import numpy as np

from utils.siamese_model_helper import SiameseNetwork, SiameseModel


# Define a dummy dataset
class DummyDataset(Dataset):
    def __init__(self, size):
        self.size = size

    def __len__(self):
        return self.size

    def __getitem__(self, idx):
        # Generate dummy data
        x1 = torch.randn(1, 1, 28, 28)  # Assuming input size is (1, 28, 28)
        x2 = torch.randn(1, 1, 28, 28)
        label = torch.randint(0, 2, (1,), dtype=torch.float32)  # Binary label
        return x1, x2, label


# Create the Siamese network
siamese_net = SiameseNetwork()

# Create the Siamese model
siamese_model = SiameseModel(siamese_net)

# Dummy dataset and dataloader
dataset = DummyDataset(size=100)
dataloader = DataLoader(dataset, batch_size=16, shuffle=True)

# Define loss function and optimizer
criterion = torch.nn.BCELoss()  # Binary Cross Entropy Loss
optimizer = torch.optim.Adam(siamese_model.parameters(), lr=0.001)

# Training loop
num_epochs = 10
for epoch in range(num_epochs):
    total_loss = 0
    for x1, x2, labels in dataloader:
        optimizer.zero_grad()
        output = siamese_model(x1, x2)
        loss = criterion(output, labels)
        loss.backward()
        optimizer.step()
        total_loss += loss.item()
    print(f"Epoch {epoch + 1}, Loss: {total_loss / len(dataloader)}")
