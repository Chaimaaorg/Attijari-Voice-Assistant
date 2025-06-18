import requests

# Remplacer ces chemins d'accès par les chemins d'accès à vos fichiers audio
original_audio_path = "chemin/vers/fichier/audio/original.wav"
user_audio_path = "chemin/vers/fichier/audio/user.wav"

# Créer un dictionnaire de fichiers à envoyer à l'API
files = {
    "original_audio": open(original_audio_path, "rb"),
    "user_audio": open(user_audio_path, "rb")
}

# Envoyer une requête POST à l'API
response = requests.post("http://localhost:8002/", files=files)

# Afficher la réponse
print("Response:", response.text)
