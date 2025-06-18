import uvicorn
from fastapi import FastAPI, UploadFile, File
import torch
import soundfile as sf
import pickle

from utils.siamese_model_helper import SiameseNetwork, SiameseModel
from utils.voice_print_helper import predict

api_description = """
This API provides endpoints for finding similarity between audios and detecting the identity of a user based on the provided embeddings from the banking API.
"""

app = FastAPI(
    description=api_description  # Include the custom description
)
MODEL_PATH = r'C:\Users\HP\Documents\AAAprojet PFE\docs pfe\models\model_epoch_16.pt'

# Load the scaler
scaler = pickle.load(open('scaler.pkl', 'rb'))

# Load the model architecture and weights
siamese_network = SiameseNetwork()
model = SiameseModel(siamese_network).to('cpu')
model.load_state_dict(torch.load(MODEL_PATH, map_location=torch.device('cpu')))
model.eval()

from pydantic import BaseModel


class RequestData(BaseModel):
    original_audio: UploadFile
    user_audio: UploadFile
    text: str


class ResponseData(BaseModel):
    response: str


@app.post('/')
async def upload_audio_files(
        original_audio: UploadFile = File(...),
        user_audio: UploadFile = File(...)

):
    # Read the audio files using soundfile
    audio_data1, samplerate1 = sf.read(original_audio.file)
    audio_data2, samplerate2 = sf.read(user_audio.file)

    # Predict using the preprocessed data
    result = predict(audio_data1, audio_data2, samplerate1, samplerate2, model, scaler)

    return result


if __name__ == "__main__":
    uvicorn.run(app, host='0.0.0.0', port=8002)
