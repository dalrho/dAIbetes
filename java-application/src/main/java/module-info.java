module org.example.daibetes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;
    requires javafx.base;
    requires java.net.http;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
//    requires org.example.daibetes;
//    requires org.example.daibetes;
//    requires org.example.daibetes;
    requires jbcrypt;
    requires webcam.capture;
    requires javafx.swing;
    requires com.fasterxml.jackson.databind;
    //requires org.example.daibetes;
    //requires org.example.daibetes;
    //requires org.example.daibetes;
    exports popdiagnosis;
    exports imageUpload;
    opens imageUpload to javafx.fxml;
    opens popdiagnosis to javafx.fxml;

    opens register to javafx.fxml;
    exports register;

    exports org.example.daibetes.modules.auth.ui;
    opens org.example.daibetes.modules.auth.ui to javafx.fxml;
    opens org.example.daibetes.modules.doctor.ui to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui;
    opens org.example.daibetes to javafx.fxml;
    exports org.example.daibetes;

    opens results to javafx.fxml;
    exports results;

    opens records to javafx.fxml;
    exports records;


    opens doctorDashboard to javafx.fxml;
    exports doctorDashboard;

    opens splashscreen to javafx.fxml;
    exports splashscreen;

    opens login to javafx.fxml;
    exports login;

    opens patientsdashboard to javafx.fxml;
    exports patientsdashboard;

    opens org.example.daibetes.modules.detection.ui to javafx.fxml;
    exports org.example.daibetes.modules.detection.ui;
    exports org.example.daibetes.modules.auth.viewmodel;
    opens org.example.daibetes.modules.auth.viewmodel to javafx.fxml;
}