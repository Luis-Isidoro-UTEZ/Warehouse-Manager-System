package mx.edu.utez.warehousemanagerfx.utils;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.controlsfx.control.RangeSlider;

public class RangeSliderAnimator {

    private final RangeSlider slider;
    private final Label lowLabel, highLabel;
    private final BorderPane parent;
    private final FadeTransition fadeInLow, fadeOutLow, fadeInHigh, fadeOutHigh;

    public RangeSliderAnimator(RangeSlider slider, Label lowLabel, Label highLabel, BorderPane parent) {
        this.slider = slider;
        this.lowLabel = lowLabel;
        this.highLabel = highLabel;
        this.parent = parent;

        fadeInLow = createFadeTransition(lowLabel, 0, 1);
        fadeOutLow = createFadeTransition(lowLabel, 1, 0);
        fadeInHigh = createFadeTransition(highLabel, 0, 1);
        fadeOutHigh = createFadeTransition(highLabel, 1, 0);

        initialize();
    }

    private FadeTransition createFadeTransition(Label label, double from, double to) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), label);
        fade.setFromValue(from);
        fade.setToValue(to);
        return fade;
    }

    private void initialize() {
        // Inicializa etiquetas
        lowLabel.setOpacity(0);
        highLabel.setOpacity(0);
        lowLabel.setVisible(true);
        highLabel.setVisible(true);
        lowLabel.setPrefWidth(40);
        highLabel.setPrefWidth(40);

        // Listeners para valores
        slider.lowValueProperty().addListener((obs, oldVal, newVal) -> updateLabels());
        slider.highValueProperty().addListener((obs, oldVal, newVal) -> updateLabels());

        // Setup de eventos
        Platform.runLater(this::setupThumbEvents);

        // Inicial
        updateLabels();
    }

    private void setupThumbEvents() {
        StackPane lowThumb = (StackPane) slider.lookup(".low-thumb");
        StackPane highThumb = (StackPane) slider.lookup(".high-thumb");

        if (lowThumb != null) {
            lowThumb.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                updateLabels();
                fadeOutLow.stop();
                fadeInLow.playFromStart();
            });
            lowThumb.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                fadeInLow.stop();
                fadeOutLow.playFromStart();
            });
            lowThumb.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> updateLabels());
        }

        if (highThumb != null) {
            highThumb.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                updateLabels();
                fadeOutHigh.stop();
                fadeInHigh.playFromStart();
            });
            highThumb.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
                fadeInHigh.stop();
                fadeOutHigh.playFromStart();
            });
            highThumb.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> updateLabels());
        }
    }

    private void updateLabels() {
        Node lowThumb = slider.lookup(".low-thumb");
        Node highThumb = slider.lookup(".high-thumb");

        if (lowThumb == null || highThumb == null) return;

        Bounds lowBounds = lowThumb.localToScene(lowThumb.getBoundsInLocal());
        Bounds highBounds = highThumb.localToScene(highThumb.getBoundsInLocal());

        Point2D lowPos = parent.sceneToLocal(
                lowBounds.getMinX() + lowBounds.getWidth() / 2,
                lowBounds.getMinY()
        );

        Point2D highPos = parent.sceneToLocal(
                highBounds.getMinX() + highBounds.getWidth() / 2,
                highBounds.getMinY()
        );

        lowLabel.setText(String.format("%.0f", slider.getLowValue()));
        highLabel.setText(String.format("%.0f", slider.getHighValue()));

        lowLabel.setLayoutX(lowPos.getX() - lowLabel.getWidth() / 2);
        highLabel.setLayoutX(highPos.getX() - highLabel.getWidth() / 2);

        lowLabel.setLayoutY(lowPos.getY() - lowLabel.getHeight() - 5);
        highLabel.setLayoutY(highPos.getY() - highLabel.getHeight() - 5);
    }
}
