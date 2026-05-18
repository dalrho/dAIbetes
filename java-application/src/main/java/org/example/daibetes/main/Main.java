package org.example.daibetes.main;

import javafx.application.Application;
import org.example.daibetes.modules.splash.app.splashApplication;

/**
 * Unified Entry Point and bootloader of the dAIbetes JavaFX Application.
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(splashApplication.class, args);
    }
}
