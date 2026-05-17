package org.example.daibetes.modules.doctor.dashboard.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.PopupManager;
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.core.database.DoctorDashboardDAO;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.User;
/**
 * Controller for doctor-dashboard.fxml
 */
public class DoctorDashboardController implements Initializable {

    // ── FXML injected ─────────────────────────────────────────────
    @FXML private Pane   gaugePane;
    @FXML private Label  recordsStatusLabel;
    @FXML private Label  diagnosesStatusLabel;
    @FXML private Label  patientsStatusLabel;
    @FXML private Button updateDataBtn;
    @FXML private Label  totalScansLabel;
    @FXML private Label  toReviewLabel;
    @FXML private VBox   recentActivitiesContainer;
    @FXML private VBox   scheduleContainer;
    @FXML private Button newDiagnosisBtn;
    @FXML private Button viewPatientsBtn;
    @FXML private Button viewReportsBtn;
    @FXML private Button logoutBtn;
    @FXML private Button inboxBtn;
    @FXML private ImageView profileImage;
    @FXML private Label doctorFirstNameLabel;
    @FXML private Label doctorLastNameLabel;
    private int loggedInDoctorId;
    private final DoctorDashboardDAO dashboardDAO = new DoctorDashboardDAO();

    // ── Static data ───────────────────────────────────────────────

    private static final double RECORDS_PROGRESS   = 0.85;
    private static final double DIAGNOSES_PROGRESS = 0.65;
    private static final double PATIENTS_PROGRESS  = 0.42;


    private record ScheduleEntry(String title, String subtitle) {}

    private static final List<ScheduleEntry> SCHEDULE_ENTRIES = List.of(
            new ScheduleEntry("Monday – 2pm – Patient #3412", "Follow up check up"),
            new ScheduleEntry("Monday – 2pm – Patient #3412", "Follow up check up")
    );
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLoggedInDoctorName();
        loadDashboardData();

        gaugePane.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.getWidth() > 0 && newVal.getHeight() > 0) {
                drawGauge();
            }
        });

        URL imgUrl = getClass().getResource("/org/example/daibetes/images/serato.jpg");

        if (imgUrl == null) {
            System.out.println("Image NOT FOUND in resources!");
        } else {
            Image img = new Image(imgUrl.toExternalForm());
            profileImage.setImage(img);
        }

        populateSchedule();
    }


    private void loadLoggedInDoctorName() {
        User currentUser = AppContext.getInstance().getCurrentUser();

        if (currentUser instanceof Doctor doctor) {
            loggedInDoctorId = doctor.getDId();

            doctorFirstNameLabel.setText("Doc. " + doctor.getFirstname());
            doctorLastNameLabel.setText(doctor.getLastname());
        } else {
            loggedInDoctorId = 0;

            doctorFirstNameLabel.setText("Doc.");
            doctorLastNameLabel.setText("");
        }
    }
    private void loadDashboardData() {
        if (loggedInDoctorId == 0) {
            totalScansLabel.setText("0");
            toReviewLabel.setText("0");
            recentActivitiesContainer.getChildren().clear();
            recentActivitiesContainer.getChildren().add(new Label("No logged-in doctor found."));
            return;
        }

        int totalReports = dashboardDAO.getTotalReportsByDoctor(loggedInDoctorId);

        totalScansLabel.setText(String.valueOf(totalReports));
        toReviewLabel.setText("0");

        populateRecentActivities();
    }

    // ── Gauge ─────────────────────────────────────────────────────
    /**
     * Draws three concentric rainbow arcs.
     *
     * JavaFX Arc angles:
     *   0°   = 3-o'clock (right)
     *   90°  = 12-o'clock (top)
     *   180° = 9-o'clock (left)
     *   positive length = counter-clockwise
     *
     * We want arcs that sweep from 9-o'clock (left) up and over to
     * 3-o'clock (right) through the top — i.e. startAngle=0, length=+180.
     *
     * The centre (cx, cy) is placed near the BOTTOM of the pane so the
     * arcs form a tall rainbow arch visible within the pane height.
     */
    private void drawGauge() {
        gaugePane.getChildren().clear();

        double W  = gaugePane.getWidth();   // 200
        double H  = gaugePane.getHeight();  // 190

        double cx     = W / 2.0;   // 100 — horizontal centre
        double cy     = H - 15;    // 175 — near the bottom for a tall arch

        double stroke = 22;        // ring thickness
        double gap    = 28;        // radial gap between rings

        // Outer → inner:  red (records), blue (diagnoses), orange (patients)
        double[] radii      = { 88,  88 - gap,  88 - gap * 2 };
        double[] progresses = { RECORDS_PROGRESS, DIAGNOSES_PROGRESS, PATIENTS_PROGRESS };

        Color[] fillColors  = {
                Color.web("#E74C3C"),
                Color.web("#3498DB"),
                Color.web("#F39C12")
        };
        Color[] trackColors = {
                Color.web("#F5BCBC"),
                Color.web("#AED6F1"),
                Color.web("#FAD7A0")
        };

        for (int i = 0; i < 3; i++) {
            double r    = radii[i];
            double prog = progresses[i];

            // Background track — full 180°
            Arc track = makeArc(cx, cy, r, 0, 180, stroke, trackColors[i]);
            gaugePane.getChildren().add(track);

            // Progress fill
            Arc filled = makeArc(cx, cy, r, 0, prog * 180, stroke, fillColors[i]);
            gaugePane.getChildren().add(filled);
        }

        // Tip dots on outer two rings
        addTipDot(cx, cy, radii[0], RECORDS_PROGRESS * 180, Color.web("#C0392B"), 10);
        addTipDot(cx, cy, radii[1], DIAGNOSES_PROGRESS * 180, Color.web("#EAC040"), 9);
    }

    /**
     * Creates a stroked open Arc (no fill).
     * startAngle=0 means right; positive length sweeps counter-clockwise.
     */
    private Arc makeArc(double cx, double cy, double r,
                        double startAngle, double length,
                        double strokeWidth, Color color) {
        Arc arc = new Arc(cx, cy, r, r, startAngle, length);
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(color);
        arc.setStrokeWidth(strokeWidth);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        return arc;
    }

    /**
     * Places a small circle at the leading tip of a progress arc.
     *
     * @param angleDeg arc sweep in degrees (counter-clockwise from 0°/right)
     */
    private void addTipDot(double cx, double cy, double r,
                           double angleDeg, Color color, double radius) {
        double rad = Math.toRadians(angleDeg);
        double x   = cx + r * Math.cos(rad);
        double y   = cy - r * Math.sin(rad);   // y-axis flipped in JavaFX

        Circle dot = new Circle(x, y, radius, color);
        dot.setStroke(Color.WHITE);
        dot.setStrokeWidth(2.5);
        gaugePane.getChildren().add(dot);
    }

    // ── List population ───────────────────────────────────────────

    private void populateRecentActivities() {
        recentActivitiesContainer.getChildren().clear();

        if (loggedInDoctorId == 0) {
            recentActivitiesContainer.getChildren().add(new Label("No recent activities."));
            return;
        }

        List<String> activities = dashboardDAO.getRecentActivitiesByDoctor(loggedInDoctorId);

        if (activities.isEmpty()) {
            Label emptyLabel = new Label("No recent activities yet.");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #777777;");
            recentActivitiesContainer.getChildren().add(emptyLabel);
            return;
        }

        for (String activity : activities) {
            Label lbl = new Label(activity);
            lbl.setWrapText(true);
            lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #444444;");
            recentActivitiesContainer.getChildren().add(lbl);
        }
    }

    private void populateSchedule() {
        scheduleContainer.getChildren().clear();
        for (ScheduleEntry entry : SCHEDULE_ENTRIES) {
            VBox item = new VBox(3);

            HBox titleRow = new HBox(8);
            titleRow.setAlignment(Pos.CENTER_LEFT);

            Circle dot = new Circle(7, Color.web("#F39C12"));

            Label titleLbl = new Label(entry.title());
            titleLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");
            titleLbl.setWrapText(true);

            titleRow.getChildren().addAll(dot, titleLbl);

            Label subLbl = new Label(entry.subtitle());
            subLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #777777;");
            VBox.setMargin(subLbl, new Insets(0, 0, 0, 22));

            item.getChildren().addAll(titleRow, subLbl);
            scheduleContainer.getChildren().add(item);
        }
    }

    // ── Public API ────────────────────────────────────────────────

    public void setTotalScans(int count)  { totalScansLabel.setText(String.valueOf(count)); }
    public void setToReview(int count)    { toReviewLabel.setText(String.valueOf(count)); }

    public void refreshDashboard() {
        loadDashboardData();
        drawGauge();
        populateSchedule();
    }

    // ── Button handlers ───────────────────────────────────────────
    // ── Button handlers ───────────────────────────────────────────
    @FXML
    private void handleInbox() {
        Stage stage = (Stage) inboxBtn.getScene().getWindow();
    }

    @FXML
    private void onNewDiagnosisBtn() {

//        PopupManager.open(
//                "org/example/daibetes/modules/doctor/ui/popup",
//                "popdiagnosis-screen.fxml",
//                null,
//                "New Diagnosis"
//        );

        PopupManager.open(
                "org/example/daibetes/modules/doctor/ui/newdiagnosis",
                "new-diagnosis-popup.fxml",
                "/org/example/daibetes/styles/new-diagnosis.css",
                "New Diagnosis"
        );
    }

    @FXML private void onViewPatients(ActionEvent event) {
        System.out.println("[Dashboard] View Patients");
        // TODO: navigate
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                SceneLoader.load(
                        "org/example/daibetes/modules/doctor/ui/patients",
                        "my-patients-view.fxml",
                        "/org/example/daibetes/styles/my-patient.css"
                )
        );
    }

    @FXML private void onViewConsultations(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                SceneLoader.load(
                        "org/example/daibetes/modules/doctor/ui/calendar",
                        "doctor-calendar.fxml",
                        "/org/example/daibetes/styles/doctor-calendar.css"
                )
        );
    }

    @FXML private void onUpdateData() {
        System.out.println("[Dashboard] Update Data");
        refreshDashboard();
    }

    @FXML
    private void onLogout(ActionEvent event) {
        System.out.println("[Dashboard] Log out");

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                SceneLoader.load(
                        "org/example/daibetes/modules/splash/controller",
                        "splash-screen.fxml",
                        "/org/example/daibetes/styles/splash.css"
                )
        );
    }

    @FXML
    private void onInbox(ActionEvent event) {

        Button button = (Button) event.getSource();

        Stage stage = (Stage) button.getScene().getWindow();

    }

//    public void initData(Doctor doctor) {
//    }
}