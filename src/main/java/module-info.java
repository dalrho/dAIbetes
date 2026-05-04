module org.example.daibetes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens login to javafx.fxml;
    opens register to javafx.fxml;

    opens results to javafx.fxml;
    opens records to javafx.fxml;
    opens doctorDashboard to javafx.fxml;
    opens splashscreen to javafx.fxml;
    opens patientsdashboard to javafx.fxml;

    exports login;
    exports register;
    exports results;
    exports records;
    exports doctorDashboard;
    exports splashscreen;
    exports patientsdashboard;

    opens styles to javafx.graphics;
    opens images to javafx.graphics;
}