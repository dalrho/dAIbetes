package splashscreen;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class splashController {
    public VBox logo;

    public void initialize(){
        FadeTransition fade = new FadeTransition();
        fade.setNode(logo);
        fade.setDuration(Duration.millis(3000));
        fade.setInterpolator(Interpolator.LINEAR);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}
