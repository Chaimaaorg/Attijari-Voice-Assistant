import torch.nn as nn
import torch
# Define the Siamese network architecture in PyTorch

class SiameseNetwork(nn.Module):
    def __init__(self):
        super(SiameseNetwork, self).__init__()
        self.conv_layers = nn.Sequential(
            nn.Conv2d(1, 32, kernel_size=3, stride=1, padding=0),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2),
            nn.Conv2d(32, 64, kernel_size=3, stride=1, padding=0),
            nn.ReLU(),
            nn.MaxPool2d(kernel_size=2)
        )

        self.embedding_layer = nn.Sequential(
            nn.Flatten(),
            nn.Dropout(0.5),
            nn.Linear(64 * 9* 1, 16),
            nn.ReLU()
        )

    def forward(self, x):
        x = self.conv_layers(x)
        x = self.embedding_layer(x)
        return x
# Define the Siamese model in PyTorch
class SiameseModel(nn.Module):
    def __init__(self, siamese_network):
        super(SiameseModel, self).__init__()
        self.siamese_network = siamese_network
        self.fc = nn.Linear(16, 1)

    def forward(self,x1, x2):
        embedding_1 = self.siamese_network(x1)
        embedding_2 = self.siamese_network(x2)
#         output  = F.cosine_similarity(embedding_1, embedding_2)
        output = torch.abs(embedding_1 - embedding_2)  # Absolute difference
        output = self.fc(output)
        output = torch.sigmoid(output)  # Apply sigmoid activation for binary classification
        return output.squeeze()