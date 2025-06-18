import numpy as np
import torch
from sklearn.preprocessing import StandardScaler  # Assuming you're using StandardScaler for scaling

from utils.voice_print_helper import preprocess_audio


# Dummy model (replace this with your actual model)
class DummyModel(torch.nn.Module):
    def forward(self, x1, x2):
        # Dummy output, assuming it's a binary classification
        return torch.randn(1)

# Dummy scaler (replace this with your actual scaler)
scaler = StandardScaler()

# Dummy audio data (replace these with your actual audio data)
audio_data1 = np.random.randn(16000)  # Audio data with samplerate 16000
audio_data2 = np.random.randn(16000)  # Audio data with samplerate 16000

# Samplerates (replace these with your actual samplerates)
samplerate1 = 16000
samplerate2 = 16000

# Create an instance of the model
model = DummyModel()

# Test the predict function
output = preprocess_audio(audio_data1, audio_data2, samplerate1, samplerate2, model, scaler)
print("Output:", output)
