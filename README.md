# 🧠 dAIbetes
### AI-Powered Retinal Diagnostic Support System for Early Detection of Diabetic Retinopathy & Glaucoma

<p align="center">
  <img src="https://via.placeholder.com/1200x400.png?text=dAIbetes+AI+Retinal+Diagnostic+System" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/Python-3.9+-blue?style=for-the-badge&logo=python" />
  <img src="https://img.shields.io/badge/FastAPI-Gemini_AI-009688?style=for-the-badge&logo=fastapi" />
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql" />
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20DAO-green?style=for-the-badge" />
</p>

---

## 🚀 Overview

**dAIbetes** is an intelligent medical decision-support system designed to assist healthcare professionals in the early detection of diabetic retinopathy and glaucoma. By combining advanced Digital Image Processing (DIP) with Generative AI (Gemini), the system provides doctors with enhanced visualization and high-accuracy diagnostic predictions.

---

## 🧩 System Architecture

The project utilizes a decoupled **Client-Server architecture**. The JavaFX desktop application manages the medical workflow and data persistence, while a Python FastAPI microservice handles AI inference and image processing tasks.

```mermaid
flowchart TD
    subgraph Java_Frontend [JavaFX Desktop App]
        A[View - FXML] <--> B[ViewModel]
        B <--> C[Controllers]
        C <--> D[ScanAnalysisService]
    end

    subgraph Python_Backend [AI Service - FastAPI]
        E[FastAPI Endpoint] <--> F[OpenCV Processing]
        E <--> G[Gemini AI Inference]
    end

    subgraph Data_Layer [Persistence]
        D <--> H[GenericDAO / ImageDAO]
        H <--> I[(MySQL Database)]
    end

    D -- REST API --> E
```

---

# 🏗️ Design Patterns & Implementation

Based on the system's class diagram, dAIbetes follows strict Object-Oriented Programming (OOP) principles and enterprise design patterns:

- **Decorator Pattern**  
  Used for `ImageFilterDecorator`. This allows doctors to stack image enhancements (e.g., CLAHE, Brightness, Sharpen, Denoise) dynamically without altering the original image class.

- **Factory Pattern**  
  Implemented via `UserFactory`, `DoctorFactory`, and `PatientFactory` to handle secure and abstracted object creation for different user roles.

- **DAO Pattern (Data Access Object)**  
  Centralized data logic using `GenericDAO`, ensuring that controllers interact with data via a standardized interface (`ImageDAO`, `ReportDAO`, `MyPatientsDAO`).

- **MVVM Pattern**  
  Separates the UI (View) from the Business Logic (ViewModel), making the system highly maintainable and testable.

---

# 🧠 UML Class Diagram

```mermaid
classDiagram

%% =========================
%% USERS
%% =========================

class User {
    <<abstract>>
    -int userId
    -String firstName
    -String lastName
    -String email
    -String password
    +login()
    +logout()
}

class Doctor {
    -String specialization
    +reviewDiagnosis()
    +approveDiagnosis()
    +generateReport()
}

class Patient {
    -String medicalHistory
    +viewDiagnosis()
    +viewReports()
}

User <|-- Doctor
User <|-- Patient

%% =========================
%% AI & IMAGE PROCESSING
%% =========================

class RetinalImage {
    -int imageId
    -String imagePath
    -Date uploadDate
    +uploadImage()
    +processImage()
}

class ScanAnalysisService {
    +analyzeImage()
    +requestInference()
}

class GeminiAIService {
    +predictDisease()
    +generateExplanation()
}

class ImageProcessor {
    +applyCLAHE()
    +applyGaussianBlur()
    +enhanceContrast()
}

class ImageFilterDecorator {
    <<Decorator>>
    +applyFilter()
}

class SharpenFilter {
    +applyFilter()
}

class DenoiseFilter {
    +applyFilter()
}

ImageFilterDecorator <|-- SharpenFilter
ImageFilterDecorator <|-- DenoiseFilter

ScanAnalysisService --> GeminiAIService
ScanAnalysisService --> ImageProcessor
ImageProcessor --> RetinalImage

%% =========================
%% DIAGNOSIS & REPORTS
%% =========================

class Diagnosis {
    -int diagnosisId
    -String disease
    -String severity
    -Date diagnosisDate
    +generateDiagnosis()
}

class Report {
    -int reportId
    +exportPDF()
}

Doctor --> Diagnosis
Patient --> Diagnosis
Diagnosis --> Report
Diagnosis --> RetinalImage

%% =========================
%% DATABASE / DAO
%% =========================

class GenericDAO~T~ {
    <<interface>>
    +save()
    +update()
    +delete()
    +findById()
}

class ImageDAO {
    +saveImage()
    +fetchImages()
}

class ReportDAO {
    +saveReport()
    +fetchReports()
}

class MyPatientsDAO {
    +getPatients()
}

GenericDAO <|.. ImageDAO
GenericDAO <|.. ReportDAO
GenericDAO <|.. MyPatientsDAO

class DatabaseManager {
    <<Singleton>>
    +getConnection()
}

ImageDAO --> DatabaseManager
ReportDAO --> DatabaseManager
MyPatientsDAO --> DatabaseManager

%% =========================
%% FACTORY PATTERN
%% =========================

class UserFactory {
    +createUser()
}

class DoctorFactory {
    +createDoctor()
}

class PatientFactory {
    +createPatient()
}

UserFactory <|-- DoctorFactory
UserFactory <|-- PatientFactory
```

---

# 🧱 MVVM Architecture Diagram

```mermaid
flowchart LR

A[FXML Views] --> B[Controllers]
B --> C[ViewModels]
C --> D[Models]
D --> E[(MySQL Database)]
C --> F[Python FastAPI Service]
```

---

# ⚙️ Installation & Setup

## 1️⃣ Prerequisites

- Java JDK 17+
- MySQL 8.0+
- Python 3.9+
- Maven

---

# 🐍 Python AI Service Setup (Crucial)

The AI engine runs as a separate microservice. You must configure this environment before running the Java application.

```bash
# 1. Navigate to the Python service directory
cd python-ai-service/python

# 2. Create a virtual environment (venv)
python -m venv venv

# 3. Activate the virtual environment

# Windows
venv\Scripts\activate

# Mac/Linux
source venv/bin/activate

# 4. Install dependencies
pip install -r requirements.txt

# 5. Configure Environment Variables
# Create a .env file and add your Gemini API Key

GEMINI_API_KEY=your_actual_gemini_api_key_here

# 6. Run the FastAPI Server
uvicorn app.main:app --reload
```

---

# 💻 JavaFX Desktop Setup

```bash
# Clone the repository
git clone https://github.com/dalrho/dAIbetes.git

# Setup MySQL
# Import the schema from:
src/main/resources/db/db_schema.sql

# Run the JavaFX application
mvn clean javafx:run
```

---

# 🧠 Core Modules

## 🩺 Doctor Module

- AI Inference Service communicating with FastAPI + Gemini LLM
- Advanced retinal preprocessing using OpenCV
- Dynamic image enhancement using Decorator Pattern
- PDF report generation and patient monitoring
- Human-in-the-loop diagnosis approval system

---

## 👤 Patient Module

- Secure diagnostic history portal
- Consultation and follow-up management
- Simplified AI-generated explanations
- Long-term eye health monitoring

---

# 🏗️ Tech Stack

| Layer | Technology           |
|------|----------------------|
| Frontend | JavaFX (FXML + MVVM) |
| Backend | Java 17+             |
| AI Service | FastAPI              |
| AI Engine | Pytorch + ResNet50   |
| Image Processing | TorchVision          |
| Database | MySQL 8+             |
| Build Tool | Maven                |

---

# 📊 System Goals

- 🧠 Early detection of retinal diseases
- 🤖 AI-assisted medical decision support
- 🔐 Secure patient data handling
- 📈 Scalable enterprise architecture
- 🧑‍⚕️ Human-supervised AI diagnosis

---

# 🌟 Why dAIbetes?

- Reduces diagnostic delays
- Assists overloaded healthcare professionals
- Enhances accuracy using AI + doctor validation
- Designed for real-world clinical workflows
- Modular and scalable enterprise structure

---

# 👥 Development Team

- Angela Jahziel Encabo — Lead Developer
- Harold Shichiya I. Amistad — AI Engineer & Backend Developer
- Gerald Ares — Frontend Developer
- Ycia Debby Magnanao — Backend & Database
- Jhen Aloyon — Backend & Database

---

# 📌 Mission Statement

> “Early detection saves vision.”

dAIbetes is committed to reducing preventable blindness through accessible, high-performance AI-powered medical technology.
