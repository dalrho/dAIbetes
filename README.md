# 👁️ dAIbetes
### AI-Powered Retinal Diagnostic Support System for Early Detection of Diabetic Retinopathy 

---
<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/Python-3.9+-blue?style=for-the-badge&logo=python" />
  <img src="https://img.shields.io/badge/FastAPI-Gemini_AI-009688?style=for-the-badge&logo=fastapi" />
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql" />
  <img src="https://img.shields.io/badge/Architecture-MVVM%20%2B%20DAO-green?style=for-the-badge" />
</p>

---

## 📋 Project Overview

**dAIbetes** is an intelligent medical decision-support system designed to assist healthcare professionals in the early detection of diabetic retinopathy and glaucoma. By combining advanced Digital Image Processing (DIP) with Generative AI (Gemini), the system provides doctors with enhanced visualization and high-accuracy diagnostic predictions.

The platform integrates enterprise-level software engineering principles including Object-Oriented Programming (OOP), MVVM architecture, DAO-based persistence, multithreading, and modern design patterns to deliver a scalable, maintainable, and clinically oriented healthcare solution.

---

## 🏗️ System Architecture

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

## 🛠️ Software Engineering Concepts & Implementation

### 1. Object-Oriented Programming Principles

**How Applied:**
* **Encapsulation:** Enforced through private fields and controlled access methods across entity and service classes.
* **Inheritance:** Utilized through specialized user models such as `Doctor` and `Patient` extending the abstract `User` class.
* **Polymorphism:** Allows different user roles and modules to interact through shared interfaces and abstract behaviors.
* **Abstraction:** Abstract classes and interfaces define reusable structures for services, DAOs, and image-processing modules.

**Importance and Contribution:**
* These OOP principles established a maintainable and scalable architecture for future feature expansion.
* Clear class hierarchies reduced redundancy and improved modularity across the system.

### 2. Java Generics

**How Applied:**
* Generic collections such as `List<T>` and `Map<K,V>` are used for managing appointments, patient records, diagnostic results, and reports.
* Generic DAO interfaces (`GenericDAO<T>`) provide reusable CRUD functionality for multiple entities.

**Importance and Contribution:**
* Generics improved type safety and reduced unnecessary type casting.
* This approach enhanced code reusability and simplified maintenance across the persistence layer.

### 3. Multithreading and Concurrency

**How Applied:**
* Background threads are used for splash screen initialization, API communication, image processing, and asynchronous database operations.
* Synchronization mechanisms are implemented to safely manage concurrent access to shared resources.

**Importance and Contribution:**
* Multithreading ensures that the JavaFX interface remains responsive during intensive processing tasks.
* Concurrency handling improves application reliability and prevents race conditions during simultaneous operations.

### 4. Graphical User Interface (GUI)

**How Applied:**
* The system uses JavaFX with FXML and custom CSS styling to build an interactive medical desktop application.
* Event-driven programming is implemented through handlers and listeners for user interactions such as uploads, calendar selection, and diagnosis review.
* Navigation includes sidebars, modal dialogs, and responsive dashboard layouts for efficient workflow management.

**Importance and Contribution:**
* JavaFX enabled the development of a modern and responsive user experience suitable for healthcare environments.
* Event-driven interaction improved usability and operational efficiency for medical professionals.

### 5. Database Connectivity

**How Applied:**
* JDBC is used to establish secure communication with the MySQL database.
* CRUD operations are implemented through DAO classes for managing users, retinal scans, reports, and diagnosis records.
* Security measures include password hashing, input validation, and controlled database access.

**Importance and Contribution:**
* Persistent storage ensures reliable management of patient and diagnostic information.
* Proper database design and security practices protect sensitive healthcare data and maintain system integrity.

### 6. Unified Modeling Language (UML)

**How Applied:**
* UML Class Diagrams and Use Case Diagrams were created to represent system structure and user interactions.
* The diagrams align closely with the implemented architecture and software modules.

**Importance and Contribution:**
* UML diagrams served as a development blueprint throughout the project lifecycle.
* They improved communication, reduced implementation inconsistencies, and maintained architectural clarity.

### 7. Design Patterns

**How Applied:**
* **Factory Pattern:** Implemented through `UserFactory`, `DoctorFactory`, and `PatientFactory` to centralize user object creation.
* **Decorator Pattern:** Applied through `ImageFilterDecorator` and specialized filters such as brightness enhancement and denoising.
* **Singleton Pattern:** Used in `DatabaseManager` to maintain a single controlled database connection instance.
* **DAO Pattern:** Abstracts database operations and separates persistence logic from business logic.
* **MVVM Pattern:** Separates presentation logic from UI components for maintainability and testing.
* **Observer Pattern:** Utilized for event-driven updates and notification handling between UI components and services.

**Importance and Contribution:**
* These patterns improved modularity, maintainability, scalability, and separation of concerns throughout the application.
* The architecture supports future extensibility while minimizing tightly coupled dependencies.

### 8. Code Quality and Documentation

**How Applied:**
* Classes follow standardized Java naming conventions and are organized into logical packages.
* Javadoc comments and inline documentation explain key modules, methods, and workflows.
* The project follows clean coding practices with readable and modular implementation.

**Importance and Contribution:**
* Consistent code quality improved readability, debugging efficiency, and maintainability.
* Documentation supports easier collaboration, evaluation, and future system enhancement.

---

## 🧩 Design Patterns & Implementation Detail

Based on the system's class diagram, dAIbetes follows strict Object-Oriented Programming (OOP) principles and enterprise design patterns:

*   **Decorator Pattern:** Used for `ImageFilterDecorator`. This allows doctors to stack image enhancements (e.g., CLAHE, Brightness, Sharpen, Denoise) dynamically without altering the original image class.
*   **Factory Pattern:** Implemented via `UserFactory`, `DoctorFactory`, and `PatientFactory` to handle secure and abstracted object creation for different user roles.
*   **DAO Pattern (Data Access Object):** Centralized data logic using `GenericDAO`, ensuring that controllers interact with data via a standardized interface (`ImageDAO`, `ReportDAO`, `MyPatientsDAO`).
*   **MVVM Pattern:** Separates the UI (View) from the Business Logic (ViewModel), making the system highly maintainable and testable.
*   **Singleton Pattern:** Ensures a single managed database connection instance throughout the application lifecycle.
*   **Observer Pattern:** Enables efficient event-driven communication between UI components and backend services.

---

## 📊 UML Class Diagram

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

## 📐 MVVM Architecture Diagram

```mermaid
flowchart LR

A[FXML Views] --> B[Controllers]
B --> C[ViewModels]
C --> D[Models]
D --> E[(MySQL Database)]
C --> F[Python FastAPI Service]
```

---

## ⚙️ Installation & Setup

### 1. Prerequisites
* Java JDK 17+
* MySQL 8.0+
* Python 3.9+
* Maven

### 2. Python AI Service Setup
The AI engine runs as a separate microservice. Configure this environment before running the Java application.

```bash
# 1. Navigate to the Python service directory
cd python-ai-service/python

# 2. Create a virtual environment
python -m venv venv

# 3. Activate the virtual environment
# Windows
venv\Scripts\activate
# Mac/Linux
source venv/bin/activate

# 4. Install dependencies
pip install -r requirements.txt

# 5. Configure Environment Variables
# Create a .env file or export the key
GEMINI_API_KEY=your_actual_gemini_api_key_here

# 6. Run the FastAPI Server
uvicorn app.main:app --reload
```

### 3. JavaFX Desktop Setup
```bash
# Clone the repository
git clone https://github.com/dalrho/dAIbetes.git

# Setup MySQL
# Import the schema from: src/main/resources/db/db_schema.sql

# Run the JavaFX application
mvn clean javafx:run
```

---

## 📦 Core Modules

### Doctor Module
* AI inference service integrated with FastAPI and Gemini AI.
* Advanced retinal image preprocessing using OpenCV.
* Dynamic image enhancement through the Decorator Pattern.
* PDF report generation and patient monitoring.
* Human-in-the-loop diagnosis approval workflow.

### Patient Module
* Secure diagnostic history management.
* Consultation and follow-up support.
* Simplified AI-generated explanations.
* Long-term eye health monitoring.

---

## 💻 Technology Stack

| Layer            | Technology           |
| ---------------- | -------------------- |
| **Frontend**     | JavaFX (FXML + MVVM) |
| **Backend**      | Java 17+             |
| **AI Service**   | FastAPI              |
| **AI Engine**    | PyTorch + ResNet50   |
| **Processing**   | TorchVision + OpenCV |
| **Database**     | MySQL 8+             |
| **Build Tool**   | Maven                |

---

## 🎯 System Objectives

* Early detection of retinal diseases.
* AI-assisted medical decision support.
* Secure patient data management.
* Scalable enterprise-level architecture.
* Human-supervised AI diagnosis workflow.

---

## 💎 Value Proposition

* **Reduces Diagnostic Delays:** Rapid AI inference provides immediate preliminary results.
* **Clinical Decision Support:** Assists healthcare professionals with data-driven analysis.
* **Enhanced Accuracy:** Combines automated AI insight with essential doctor validation.
* **Professional Workflow:** Designed specifically for real-world clinical environments.
* **Enterprise Ready:** Modular, scalable, and maintainable architecture.

---

## 👥 Development Team

* **Angela Jahziel Encabo** — Lead Developer
* **Harold Shichiya I. Amistad** — AI Engineer & Backend Developer
* **Gerald Ares** — Frontend Developer
* **Ycia Debby Magnanao** — Backend & Database
* **Jhen Aloyon** — Backend & Database

---

## 🏳️ Mission Statement

> "Early detection saves vision."

dAIbetes is committed to reducing preventable blindness through accessible, high-performance AI-powered medical technology.
