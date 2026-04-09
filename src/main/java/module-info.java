module org.example.daibetes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

//requires org.controlsfx.controls;
//requires org.kordamp.bootstrapfx.core;

    opens org.example.daibetes to javafx.fxml;
    exports org.example.daibetes;

    opens results to javafx.fxml;
    exports results;

    opens doctorDashboard to javafx.fxml;
    exports doctorDashboard;
    opens splashscreen to javafx.fxml;
    exports splashscreen;

    opens login to javafx.fxml;
    exports login;

    opens patientsdashboard to javafx.fxml;
    exports patientsdashboard;
}