package doctorDashboard;

import javafx.event.ActionEvent;

public class doctorDashboardController {
    public void handleLogout(ActionEvent actionEvent) {
        System.out.println("Logout");
    }

    public void handleUploadImage(ActionEvent actionEvent) {
        System.out.println("Opening new diagnosis tab");
    }

    public void handleViewRecords(ActionEvent actionEvent) {
        System.out.println("View patients tab");
    }

    public void handleReports(ActionEvent actionEvent) {
        System.out.println("View reports tab");
    }
}
