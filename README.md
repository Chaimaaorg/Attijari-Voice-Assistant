# AttijariAssistant: Secure Voice-Based eBanking for the Visually Impaired People

## Overview
AttijariAssistant is a final-year project developed for Attijariwafa Bank, a secure voice-based banking assistant designed to make eBanking accessible to visually impaired users. The system leverages biometric voice authentication and natural language processing (NLP) to facilitate seamless banking transactions through voice commands.
## Features
- **Biometric Voice Authentication**: Secure login using a Siamese Convolutional Neural Network-based speaker recognition system.
- **Natural Language Understanding**: Intent recognition powered by a fine-tuned CamemBERT model, achieving high accuracy in understanding user requests.
- **Text-to-Speech (TTS) & Speech-to-Text (STT)**: High-precision voice interaction for accessibility.
- **Secure Transactions**: Two-factor authentication (voice + security question) for enhanced security.
- **Banking Operations**: Users can check balances, transfer funds, and inquire about transactions using voice commands.
- **FAQ & Customer Support**: An NLP-powered FAQ system answers common user queries.

## Architecture
The project is structured into several components:
1. **Mobile Application**: Front-end application enabling voice interactions.
   - Source Code: [AttijariAssistant Mobile App](https://github.com/Chaimaaorg/Attijari-Voice-Assistant/tree/main/mobile-application)
2. **Banking API**: A simulated banking API for processing financial operations.
   - Source Code: [Banking API](https://github.com/Chaimaaorg/Attijari-Voice-Assistant/tree/main/banking-api)
3. **NLP API**: Processes voice commands using intent recognition and named entity extraction.
   - Source Code: [NLP Scripts](https://github.com/Chaimaaorg/Attijari-Voice-Assistant/tree/main/chatbot)
4. **Voice Biometrics API**: Implements speaker recognition for secure authentication.
   - Source Code: [Voice Biometrics API](https://github.com/Chaimaaorg/Attijari-Voice-Assistant/tree/main/voice-biometrics-api)
5. **Datasets & Notebooks**: Training datasets, notebooks, reports, and database scripts.
   - Repository: [Project Resources](https://github.com/Chaimaaorg/Attijari-Voice-Assistant/tree/main/notebooks)

## Technologies Used
- **Machine Learning**: TensorFlow, PyTorch, Siamese Networks
- **NLP**: CamemBERT, Named Entity Recognition (NER), Intent Classification
- **Speech Processing**: MFCC feature extraction, Speech-to-Text (STT), Text-to-Speech (TTS)
- **Mobile Development**: Flutter
- **Back-end APIs**: Python (FastAPI, Flask), Spring Boot
- **Database**: PostgreSQL, MongoDB

## Installation & Setup
1. Clone the repositories:
   ```sh
   git clone https://github.com/Chaimaaorg/Attijari-Voice-Assistant.git
   ```
2. Install dependencies for each project following the instructions in respective repositories.
3. Run the APIs and mobile app as per the setup guides in each repo.

## Future Enhancements
- Integrating real-time fraud detection mechanisms.
- Expanding to multi-language support.
- Deploying on a production-ready cloud infrastructure.

For more details, refer to the full project report: [PFE Report](https://github.com/Chaimaaorg/Attijari-Voice-Assistant/blob/main/Rapport_PFE_2024_CHAIMAA_OURGANI_250218_221903.pdf)

---
