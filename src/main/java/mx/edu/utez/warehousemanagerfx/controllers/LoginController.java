package mx.edu.utez.warehousemanagerfx.controllers;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.util.Duration;
import mx.edu.utez.warehousemanagerfx.models.UserAccount;
import mx.edu.utez.warehousemanagerfx.utils.services.LoginService;
import mx.edu.utez.warehousemanagerfx.utils.services.LoginService.AuthResult;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.util.Objects;

public class LoginController {

    @FXML
    private TextField user;
    @FXML
    private PasswordField password;

    // En vez de Dialog, usamos Stage propio para el "Connecting..."
    private Stage loadingStage;

    @FXML
    private void login(ActionEvent event) {
        final String identifier = user.getText() == null ? "" : user.getText().trim();
        final String rawPassword = password.getText() == null ? "" : password.getText();

        if (identifier.isEmpty() || rawPassword.isEmpty()) {
            showWarningNonBlocking("Incomplete data", "Enter your username/email and password.");
            return;
        }

        setInputsDisabled(true);
        Window owner = ((Node) event.getSource()).getScene().getWindow();
        showLoadingStage(owner, "Connecting...", "Validating credentials. Please wait.");

        Task<AuthResult> task = new Task<>() {
            @Override
            protected AuthResult call() {
                LoginService service = new LoginService();
                return service.authenticate(identifier, rawPassword);
            }
        };

        // Cierre garantizado del loading ante cualquier fin de la tarea
        task.stateProperty().addListener((obs, old, state) -> {
            if (state == Worker.State.SUCCEEDED || state == Worker.State.FAILED || state == Worker.State.CANCELLED) {
                closeLoadingStageSafely();
                // pequeño respiro para que la ventana realmente desaparezca antes de seguir
                PauseTransition pt = new PauseTransition(Duration.millis(80));
                pt.setOnFinished(ev -> setInputsDisabled(false));
                pt.play();
            }
        });

        task.setOnSucceeded(e -> {
            AuthResult result = task.getValue();
            if (result != null && result.success() && result.user() != null) {
                UserAccount account = result.user();
                String role = account.getRoleType();

                // Navegamos después de cerrar visualmente el loading
                PauseTransition pt = new PauseTransition(Duration.millis(100));
                pt.setOnFinished(ev -> {
                    Platform.runLater(() -> {
                        if ("SUPERADMINISTRATOR".equalsIgnoreCase(role)) {
                            navigateTo(event, FXMLRoutes.SUPERADMIN);
                            showInfoNonBlocking("Welcome", "Welcome SuperAdmin!");
                        } else if ("ADMINISTRATOR".equalsIgnoreCase(role)) {
                            navigateTo(event, FXMLRoutes.ADMIN);
                            showInfoNonBlocking("Welcome", "Welcome Admin!");
                        } else {
                            showWarningNonBlocking("Access denied", "Your role does not have access.");
                        }
                    });
                });
                pt.play();
            } else {
                PauseTransition pt = new PauseTransition(Duration.millis(100));
                pt.setOnFinished(ev -> Platform.runLater(() -> {
                    if (result == null) {
                        showErrorNonBlocking("Error", "Authentication returned null result.");
                        return;
                    }
                    switch (result.errorType()) {
                        case NONE -> showWarningNonBlocking("Invalid credentials", "Incorrect username or password.");
                        case CLOUD_NO_INTERNET -> showErrorNonBlocking(
                                "No Internet connection",
                                "There's no internet for the cloud database. An attempt was made to reconnect to the local database."
                        );
                        case CLOUD_UNAVAILABLE -> showErrorNonBlocking(
                                "Cloud database unavailable",
                                "Could not connect to the cloud database. An attempt was made to reconnect to the local database."
                        );
                        case LOCAL_UNAVAILABLE -> showErrorNonBlocking(
                                "Local BD not available",
                                "Could not connect to the local database. Please try again later or contact your administrator."
                        );
                        case BOTH_UNAVAILABLE -> showErrorNonBlocking(
                                "No database service",
                                "It was not possible to connect to either the cloud or local database."
                        );
                        default -> showErrorNonBlocking("Error", "An error occurred while authenticating. Please try again.");
                    }
                }));
                pt.play();
            }
        });

        task.setOnFailed(e -> {
            PauseTransition pt = new PauseTransition(Duration.millis(100));
            pt.setOnFinished(ev -> Platform.runLater(() ->
                    showErrorNonBlocking("Unexpected error", "An error occurred while authenticating. Please try again.")
            ));
            pt.play();
        });

        Thread th = new Thread(task, "login-auth-task");
        th.setDaemon(true);
        th.start();
    }

    private void navigateTo(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(fxmlPath))
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            showErrorNonBlocking("Navigation error", "An error occurred while loading the window.");
        }
    }

    private void setInputsDisabled(boolean disabled) {
        user.setDisable(disabled);
        password.setDisable(disabled);
    }

    /* ========== Loading como Stage propio (más confiable que Dialog) ========== */

    private void showLoadingStage(Window owner, String title, String message) {
        if (loadingStage != null && loadingStage.isShowing()) return;

        Label header = new Label(message);
        header.setWrapText(true);

        ProgressIndicator pi = new ProgressIndicator();
        pi.setPrefSize(80, 80);

        Label lbl = new Label("Connecting to the database...");
        lbl.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, pi, spacer, new VBox(4, header, lbl));
        row.setPadding(new Insets(12));

        Scene scene = new Scene(new VBox(row));
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(scene);

        loadingStage = stage;
        // Mostrar no bloqueante y siempre en el FX thread
        Platform.runLater(() -> {
            if (!loadingStage.isShowing()) loadingStage.show();
        });
    }

    private void closeLoadingStageSafely() {
        if (Platform.isFxApplicationThread()) {
            closeLoadingStage();
        } else {
            Platform.runLater(this::closeLoadingStage);
        }
    }

    private void closeLoadingStage() {
        if (loadingStage != null) {
            try {
                if (loadingStage.isShowing()) {
                    loadingStage.hide();
                }
            } finally {
                loadingStage = null;
            }
        }
    }

    /* ========== Alertas no bloqueantes (evitan deadlocks visuales) ========== */

    private void showInfoNonBlocking(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    private void showWarningNonBlocking(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    private void showErrorNonBlocking(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
