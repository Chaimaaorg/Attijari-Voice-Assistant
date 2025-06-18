import numpy as np
import librosa
import torch


def preprocess_audio(audio_data, samplerate, scaler):
    # Define parameters
    sr = 16000
    n_mfcc = 13
    n_fft = 2048
    hop_length = 512
    clip_duration = 1.4

    # Resample audio if necessary
    if samplerate != sr:
        audio_data = librosa.resample(audio_data, orig_sr=samplerate, target_sr=sr)

    # Take the first clip of specified duration
    step = sr * clip_duration
    audio_clip = audio_data[:int(step)]

    # Extract MFCC features
    mfcc = librosa.feature.mfcc(y=audio_clip, sr=sr, n_mfcc=n_mfcc, n_fft=n_fft, hop_length=hop_length)
    mfcc = mfcc.T  # Transpose to get the correct shape

    # Normalize the MFCC features
    mfcc = scaler.transform(mfcc)  # Use transform for subsequent inputs
    mfcc = np.expand_dims(mfcc, axis=0)  # Add batch dimension
    mfcc = np.expand_dims(mfcc, axis=0)  # Add channel dimension

    # Convert to tensor
    return torch.tensor(mfcc).float()


def predict(audio_data1, audio_data2, samplerate1, samplerate2, model, scaler, threshold=0.8):
    # Preprocess the audio data
    audio1 = preprocess_audio(audio_data1, samplerate1, scaler)
    audio2 = preprocess_audio(audio_data2, samplerate2, scaler)

    # Move tensors to the same device as model (if using GPU, modify accordingly)
    audio1 = audio1.to('cpu')
    audio2 = audio2.to('cpu')

    # Run the model inference
    with torch.no_grad():
        output = model(audio1, audio2)

    return output.item()  # Convert the output tensor to a Python scalar
