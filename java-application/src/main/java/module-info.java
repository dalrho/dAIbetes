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
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    requires kernel;
    requires io;
    requires layout;

    requires jbcrypt;
    requires webcam.capture;
    requires javafx.swing;
    requires com.fasterxml.jackson.databind;


    // --- Core & App ---
    opens org.example.daibetes to javafx.fxml;
    exports org.example.daibetes;


    // --- Auth Module ---
    opens org.example.daibetes.modules.auth.login.controller to javafx.fxml;
    exports org.example.daibetes.modules.auth.login.controller;
    opens org.example.daibetes.modules.auth.login.app to javafx.fxml;
    exports org.example.daibetes.modules.auth.login.app;

    opens org.example.daibetes.modules.auth.register.controller to javafx.fxml;
    exports org.example.daibetes.modules.auth.register.controller;
    
    exports org.example.daibetes.modules.auth.viewmodel;
    opens org.example.daibetes.modules.auth.viewmodel to javafx.fxml;
    exports org.example.daibetes.modules.auth.service;

    // --- Doctor Modules ---
    opens org.example.daibetes.modules.doctor.dashboard.controller to javafx.fxml;
    exports org.example.daibetes.modules.doctor.dashboard.controller;
    opens org.example.daibetes.modules.doctor.dashboard.app to javafx.fxml;
    exports org.example.daibetes.modules.doctor.dashboard.app;

    opens org.example.daibetes.modules.doctor.ui.calendar to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.calendar;
    opens org.example.daibetes.modules.doctor.ui.patients to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.patients;
    opens org.example.daibetes.modules.doctor.ui.newdiagnosis to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.newdiagnosis;
    
    opens org.example.daibetes.modules.doctor.ui.popup.controller to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.popup.controller;
    opens org.example.daibetes.modules.doctor.ui.popup.app to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.popup.app;

    opens org.example.daibetes.modules.doctor.ui.review.controller to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.review.controller;
    opens org.example.daibetes.modules.doctor.ui.review.app to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.review.app;
    exports org.example.daibetes.modules.doctor.ui.review.model;

    opens org.example.daibetes.modules.doctor.ui.report.controller to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.report.controller;
    opens org.example.daibetes.modules.doctor.ui.report.app to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.report.app;

    // --- Patient Modules ---
    opens org.example.daibetes.modules.patient.dashboard.controller to javafx.fxml;
    exports org.example.daibetes.modules.patient.dashboard.controller;
    opens org.example.daibetes.modules.patient.dashboard.app to javafx.fxml;
    exports org.example.daibetes.modules.patient.dashboard.app;
    exports org.example.daibetes.modules.patient.dashboard.model;

    opens org.example.daibetes.modules.patient.ui.calendar to javafx.fxml;
    exports org.example.daibetes.modules.patient.ui.calendar;
    
    opens org.example.daibetes.modules.patient.ui.upload.controller to javafx.fxml;
    exports org.example.daibetes.modules.patient.ui.upload.controller;
    opens org.example.daibetes.modules.patient.ui.upload.app to javafx.fxml;
    exports org.example.daibetes.modules.patient.ui.upload.app;

    // --- Records Module ---
    opens org.example.daibetes.modules.records.controller to javafx.fxml;
    exports org.example.daibetes.modules.records.controller;
    exports org.example.daibetes.modules.records.model;

    // --- Detection & AI ---
    opens org.example.daibetes.modules.detection.ui to javafx.fxml;
    exports org.example.daibetes.modules.detection.ui;
    opens org.example.daibetes.modules.detection.camera.ui to javafx.fxml;
    exports org.example.daibetes.modules.detection.camera.ui;
    opens org.example.daibetes.modules.ai.dto to com.fasterxml.jackson.databind;

    // --- Doctor UI Diagnosis ---
    opens org.example.daibetes.modules.doctor.ui.diagnosis to javafx.fxml;
    exports org.example.daibetes.modules.doctor.ui.diagnosis;

    // --- Splash ---
    opens org.example.daibetes.modules.splash.controller to javafx.fxml;
    exports org.example.daibetes.modules.splash.controller;
    opens org.example.daibetes.modules.splash.app to javafx.fxml;
    exports org.example.daibetes.modules.splash.app;
    exports org.example.daibetes.shared.ui;
    opens org.example.daibetes.shared.ui to javafx.fxml;
}