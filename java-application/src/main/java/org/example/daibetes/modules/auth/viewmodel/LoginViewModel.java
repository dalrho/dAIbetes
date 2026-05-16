package org.example.daibetes.modules.auth.viewmodel;

import javafx.beans.property.*;
import org.example.daibetes.core.database.RetrieveData;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.Patient;
import org.example.daibetes.shared.models.User;
import org.example.daibetes.modules.auth.service.Authenticate;

/**
 * ViewModel for login-screen.fxml (MVVM).
 *
 * Role is NO LONGER selected by the user on the login screen.
 * It is resolved automatically from the DB after credentials are verified:
 *   1. Authenticate.login()          → verifies email + password exist in tblUser
 *   2. RetrieveData.getUserByEmail() → returns typed Doctor or Patient object
 *
 * The controller reads isDoctor() / isPatient() to decide which dashboard to load.
 */
public class LoginViewModel {

    // --- Bindable input properties ---
    private final StringProperty  email    = new SimpleStringProperty("");
    private final StringProperty  password = new SimpleStringProperty("");

    // --- Bindable output properties ---
    private final StringProperty  errorMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading    = new SimpleBooleanProperty(false);
    private final BooleanProperty loginSuccess = new SimpleBooleanProperty(false);

    private User authenticatedUser;

    private final Authenticate authenticate = new Authenticate();
    private final RetrieveData retrieveData = new RetrieveData();

    // =========================================================================
    // Login action
    // =========================================================================

    public void login() {
        errorMessage.set("");
        loginSuccess.set(false);

        String emailVal    = email.get().trim();
        String passwordVal = password.get().trim();

        // Client-side validation
        if (emailVal.isEmpty() || passwordVal.isEmpty()) {
            errorMessage.set("Email and password are required.");
            return;
        }

        if (!emailVal.contains("@")) {
            errorMessage.set("Please enter a valid email address.");
            return;
        }

        isLoading.set(true);

        // Step 1: verify credentials
        boolean credentialsValid = authenticate.login(emailVal, passwordVal);

        if (!credentialsValid) {
            isLoading.set(false);
            errorMessage.set("Invalid email or password.");
            return;
        }

        // Step 2: fetch typed User object — Doctor or Patient resolved from DB
        User user = retrieveData.getUserByEmail(emailVal);

        isLoading.set(false);

        if (user == null) {
            errorMessage.set("Account not found. Please contact support.");
            return;
        }

        authenticatedUser = user;
        loginSuccess.set(true);
    }

    // =========================================================================
    // Role helpers — read by controller after loginSuccess fires
    // =========================================================================

    public boolean isDoctor()  { return authenticatedUser instanceof Doctor; }
    public boolean isPatient() { return authenticatedUser instanceof Patient; }

    public Doctor  getAuthenticatedDoctor()  { return (Doctor)  authenticatedUser; }
    public Patient getAuthenticatedPatient() { return (Patient) authenticatedUser; }
    public User    getAuthenticatedUser()    { return authenticatedUser; }

    // =========================================================================
    // Property accessors
    // =========================================================================

    public StringProperty  emailProperty()        { return email; }
    public StringProperty  passwordProperty()     { return password; }
    public StringProperty  errorMessageProperty() { return errorMessage; }
    public BooleanProperty isLoadingProperty()    { return isLoading; }
    public BooleanProperty loginSuccessProperty() { return loginSuccess; }
}