package org.example.daibetes.modules.auth.login.app;

/**
 * Launcher class for dAIbetes.
 *
 * WHY THIS EXISTS:
 * When JavaFX is on the module path, running loginApplication directly
 * from a fat-jar throws an "Application class must extend Application" error
 * because the JVM hasn't initialised the JavaFX runtime yet.
 * A plain main() in a non-Application class sidesteps this — the JVM loads
 * this class first, which then delegates to Application.launch(), giving
 * JavaFX time to initialise correctly.
 *
 * In IntelliJ: set this class as the Run Configuration's Main Class.
 */
public class loginLauncher {

    public static void main(String[] args) {
        loginApplication.launch(loginApplication.class, args);
    }
}