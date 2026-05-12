package Inbox;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class InboxLauncher {

    private static Stage inboxStage;

    public static void toggleInbox(Stage parentStage) {

        try {

            // Close if already open
            if (inboxStage != null && inboxStage.isShowing()) {
                inboxStage.close();
                return;
            }

            Parent root = FXMLLoader.load(
                    InboxLauncher.class.getResource("inbox-screen.fxml")
            );

            inboxStage = new Stage();

            Scene scene = new Scene(root, 420, 600);

            inboxStage.setScene(scene);

            inboxStage.initStyle(StageStyle.UNDECORATED);

            inboxStage.setResizable(false);

            inboxStage.setAlwaysOnTop(true);

            // Position RELATIVE to dashboard window
            double x = parentStage.getX()
                    + parentStage.getWidth()
                    - 430;

            double y = parentStage.getY()
                    + parentStage.getHeight()
                    - 620;

            inboxStage.setX(x);
            inboxStage.setY(y);

            inboxStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}