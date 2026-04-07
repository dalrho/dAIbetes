# dAIbetes: Retinal Image Diagnostic Support System for Diabetic Retinopathy and Glaucoma

## Group Members
- Angela Jahziel Encabo - Team Lead
- Harold Shichiya I. Amistad - Developer
- Gerald Ares - Developer
- Ycia Debby Magnanao - Developer
- Jhen Aloyon - Developer

## Project Description
**dAIbetes** is a Java-based medical decision-support tool designed to assist healthcare practitioners in the early detection of eye diseases, specifically diabetic retinopathy and glaucoma.

The system addresses the challenge of identifying subtle abnormalities in retinal scans by providing advanced Digital Image Processing (DIP) and AI-assisted detection. By offering a dual-sided platform, it streamlines the workflow from image enhancement and professional diagnosis to the secure delivery of results to the patient.

### Proposed Features
**Dual-Portal System:** Dedicated dashboards for Doctors (analysis/approval) and Patients (result viewing).

**Image Enhancement Suite:** Chained filters (CLAHE, Gaussian Blur, Grayscale) to improve the visibility of retinal vessels and lesions.

**Abnormality Detection:** Rule-based and AI-assisted detection of hemorrhages, exudates, and optic disc irregularities.

**Visual Overlays:** Structured results with coordinate-based overlays highlighting detected regions on the scan.

**Secure Result Delivery:** A formal approval workflow where doctors verify AI detections before sending them to the patient’s portal.

## Planned Technologies
**Language:** Java (JDK 17+)

**UI Framework:** JavaFX (utilizing FXML and IntelliJ IDEA Community Edition 2025.2.6.1)

**Build Tool:** Maven

**Database:** MySQL (Integrated via JDBC)

**Processing:** Python Integration (for OpenCV and AI/CNN models)

**Architecture:** MVVM (Model-View-ViewModel)

## Evaluation Criteria Mapping (Initial)
**Object-Oriented Programming (OOP)**
The system is built on a strong foundation of inheritance and encapsulation.

**Inheritance:** A base User class is extended by Doctor and Patient to manage specific permissions and attributes.

**Modularity:** Logic is separated into RetinalScan, DetectionResult, and Region classes to ensure high cohesion and low coupling.

### Graphical User Interface (GUI)
The project utilizes JavaFX with FXML for a clean separation of layout and logic.

**Reactive UI:** Property binding in the ViewModel layer ensures the UI updates automatically when image processing is complete.

**Side-by-Side Comparison:** Implementation of dual-pane views for original vs. enhanced scans.

### Unified Modeling Language (UML)
**Class Diagram:** Mapping the relationships between the User hierarchy, the ImageProcessing pipeline, and the Database services.

**Use Case Diagram:** Defining interactions such as "Upload Scan" (Doctor), "Approve Result" (Doctor), and "View Diagnosis" (Patient).

### Design Patterns
To ensure the system is extensible and maintainable, the following patterns are planned:

**Decorator Pattern:** To allow doctors to dynamically chain multiple image filters together.

**Strategy Pattern:** To swap between different detection algorithms (e.g., Hemorrhage vs. Exudate detection) at runtime.

**Singleton Pattern:** To manage a single, consistent connection to the MySQL database via JDBC.

**MVVM:** To decouple the complex image processing logic from the user interface.
