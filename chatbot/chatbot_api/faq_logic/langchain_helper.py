import os

from langchain.chains import RetrievalQA
from langchain.document_loaders.csv_loader import CSVLoader
from langchain.prompts import PromptTemplate
from langchain_community.embeddings import HuggingFaceInstructEmbeddings
from langchain_community.vectorstores.faiss import FAISS
from langchain_google_genai import GoogleGenerativeAI

from chatbot_api.utils.contstants import GOOGLE_CLOUD_API, HF_TOKEN

os.environ["HF_TOKEN"] = HF_TOKEN


# Create Google Palm LLM model
llm = GoogleGenerativeAI(model="models/gemini-1.0-pro-001", google_api_key=GOOGLE_CLOUD_API, temperature=0.1)

vectordb_file_path = r"C:\Users\HP\Documents\AProjetPFE\NLPScripts\chatbot_api\faq_logic\faiss_index"
dataset_path = (r"C:\Users\HP\Documents\AProjetPFE\NLPScripts\chatbot_api\faq_logic\Dataset-NLP-for-Text-Generation"
                r".csv")

model_name = "dangvantuan/sentence-camembert-large"
model_kwargs = {'device': 'cpu'}
encode_kwargs = {'normalize_embeddings': True}

# Initialize instructor embeddings using the Hugging Face model
embeddings = HuggingFaceInstructEmbeddings(
    model_name=model_name,
    model_kwargs=model_kwargs,
    encode_kwargs=encode_kwargs
)


def get_or_create_vector_db():
    try:
        # Try to load the existing vector database
        vectordb = FAISS.load_local(vectordb_file_path, embeddings, allow_dangerous_deserialization=True)
    except FileNotFoundError:
        # If the vector database doesn't exist, create it
        vectordb = create_vector_db()
    return vectordb


def create_vector_db():
    # Load data from FAQ sheet
    loader = CSVLoader(file_path=dataset_path, source_column="question",encoding='utf-8')

    # Store the loaded data in the 'data' variable
    data = loader.load()

    # Create a FAISS instance for vector database from 'data'
    vectordb = FAISS.from_documents(documents=data, embedding=embeddings)

    # Save vector database locally
    vectordb.save_local(vectordb_file_path)

    return vectordb


def get_qa_chain():
    # Load the vector database from the local folder
    vectordb = FAISS.load_local(vectordb_file_path, embeddings, allow_dangerous_deserialization=True)

    # Create a retriever for querying the vector database
    retriever = vectordb.as_retriever(score_threshold=0.7)

    # Define the prompt template
    prompt_template = """
        Étant donné le contexte suivant et une question, générez une réponse basée uniquement sur ce contexte.
        Dans la réponse, essayez de fournir autant de texte que possible à partir de la section "response" dans le contexte du document source sans apporter beaucoup de modifications.
        Si la réponse ne se trouve pas dans le contexte, veuillez dire « Veuillez contacter l'assistance en ligne pour plus d'informations! ». N'essayez pas d'inventer une réponse.
    
        CONTEXT: {context}
    
        QUESTION: {question}
        """

    # Create a prompt template
    PROMPT = PromptTemplate(template=prompt_template, input_variables=["context", "question"])

    # Configure chain parameters
    chain_type_kwargs = {"prompt": PROMPT}

    # Create the QA chain
    chain = RetrievalQA.from_chain_type(
        llm=llm,
        chain_type="stuff",
        retriever=retriever,
        input_key="query",
        return_source_documents=True,
        chain_type_kwargs=chain_type_kwargs
    )

    return chain

if __name__ == "__main__":
    # create_vector_db()
    chain = get_qa_chain()
    print(chain("Do you have javascript course?"))
