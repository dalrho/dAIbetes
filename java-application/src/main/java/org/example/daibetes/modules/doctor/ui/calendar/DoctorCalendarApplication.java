package org.example.daibetes.modules.doctor.ui.calendar;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DoctorCalendarApplication extends Application {

    /** Window title shown in the OS title-bar. */
    private static final String WINDOW_TITLE = "dAIbetes — Schedule Follow-up";

    @Override
    public void start(Stage primaryStage) throws IOException {

        // ── Load FXML ──────────────────────────────────────────────────────────
        // FXMLLoader reads the FXML file, instantiates HelloController, and
        // injects all @FXML-annotated fields automatically.
        FXMLLoader loader = new FXMLLoader(
//                getClass().getResource("/src/main/resources/doctorcalendar/doctor-calendar.fxml")
                getClass().getResource("/doctorcalendar/doctor-calendar.fxml")
        );
        // Uncomment the line below if you ever need to access the controller
        // programmatically after loading:
        // HelloController controller = loader.getController();

        // ── Build Scene ────────────────────────────────────────────────────────
        // The root node is already styled via the stylesheets attribute in the
        // FXML, so no extra CSS is needed here.
        Scene scene = new Scene(loader.load());

        // ── Configure Stage ────────────────────────────────────────────────────
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Called by Launcher.main().
     * Keeping this here (rather than in Launcher) means you can still run
     * HelloApplication directly inside an IDE that handles JavaFX modules.
     */
    public static void main(String[] args) {
        launch(args);
    }
}