package mx.edu.utez.warehousemanagerfx.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import mx.edu.utez.warehousemanagerfx.models.UserAccount;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.services.LoginService;
import mx.edu.utez.warehousemanagerfx.utils.services.LoginService.AuthResult;
import mx.edu.utez.warehousemanagerfx.utils.services.LoginService.AttemptResult;
import mx.edu.utez.warehousemanagerfx.utils.services.LoginService.DatabaseSource;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;
import mx.edu.utez.warehousemanagerfx.utils.services.SessionManager;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Final LoginController:
 * - TRY_DURATION_MS > FAIL_DURATION_MS
 * - Progress Indicator visible only when trying (info)
 * - "No internet detected" is displayed instead of "Trying cloud" when applicable
 * - Centered, more aesthetically pleasing layout
 */
public class LoginController implements Initializable {

    @FXML private TextField user;
    @FXML private PasswordField password;

    // Duraciones (millis)
    private static final long TRY_DURATION_MS = 0L;   // trying -> more noticeable // 1500 L
    private static final long FAIL_DURATION_MS = 0L;  // failed -> faster // 1200 L
    // Change any to 0 so there is no pause.

    private Stage loadingStage;
    private Label iconLabel;
    private Label messageLabel;
    private ProgressIndicator progressIndicator;
    private volatile boolean isAuthenticating = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        user.focusedProperty().addListener((obs, oldVal, newVal) -> {
            user.getStyleClass().remove("input-error");
        });
        password.focusedProperty().addListener((obs, oldVal, newVal) -> {
            password.getStyleClass().remove("input-error");
        });
    }

    @FXML
    private void login(ActionEvent event) {
        if (isAuthenticating) return;
        isAuthenticating = true;

        final String identifier = (user.getText() == null) ? "" : user.getText().trim();
        final String rawPassword = (password.getText() == null) ? "" : password.getText();

        if (identifier.isEmpty() || rawPassword.isEmpty()) {
            if (identifier.isEmpty()) {
                user.getStyleClass().add("input-error");
            }
            if (rawPassword.isEmpty()) {
                password.getStyleClass().add("input-error");
            }
            Alerts.showAlert(Alert.AlertType.WARNING, user, "Incomplete data", "Enter your username/email and password.");
            isAuthenticating = false;
            return;
        }

        setInputsDisabled(true);
        Window owner = ((Node) event.getSource()).getScene().getWindow();

        createLoadingStage(owner, "Connecting...", "Initializing authentication...");
        LoginService service = new LoginService();

        Task<AuthResult> authTask = new Task<>() {
            @Override
            protected AuthResult call() {
                LoginService.AuthProgressListener listener = (type, arg) -> {
                    if ("update".equals(type) && arg instanceof String) {
                        String msg = (String) arg;
                        Platform.runLater(() -> {
                            messageLabel.setText(msg);
                            setIconInfo();             // info look
                            progressIndicator.setVisible(false); // when it's "No internet detected", don't show progress
                        });
                        // display 'update' message as short fail or skip -> use FAIL_DURATION_MS
                        try { if (FAIL_DURATION_MS > 0) Thread.sleep(FAIL_DURATION_MS); } catch (InterruptedException ignored) {}
                    } else if ("before".equals(type) && arg instanceof DatabaseSource) {
                        DatabaseSource ds = (DatabaseSource) arg;
                        // Determine: if it's cloud, check the internet (NOTE: authenticate already does that check and can issue update/after on its own)
                        Platform.runLater(() -> {
                            messageLabel.setText("Connecting to database (" + prettyName(ds) + ")...");
                            setIconInfo();
                            progressIndicator.setVisible(true); // only in trying
                        });
                        // Attempts should be displayed longer:
                        try { if (TRY_DURATION_MS > 0) Thread.sleep(TRY_DURATION_MS); } catch (InterruptedException ignored) {}
                    } else if ("after".equals(type) && arg instanceof AttemptResult) {
                        AttemptResult ar = (AttemptResult) arg;
                        if (ar.success) {
                            Platform.runLater(() -> {
                                messageLabel.setText("Connection to " + prettyName(ar.source) + " successful.");
                                setIconSuccess();
                                progressIndicator.setVisible(false); // hide progress after success
                            });
                            // leave message visible for a bit (TRY_DURATION_MS/2)
                            try { if (TRY_DURATION_MS > 0) Thread.sleep(TRY_DURATION_MS/2); } catch (InterruptedException ignored) {}
                        } else {
                            Platform.runLater(() -> {
                                // If it was no-internet or unavailable, show fail briefly
                                if (ar.errorType == LoginService.ErrorType.CLOUD_NO_INTERNET) {
                                    messageLabel.setText("No internet detected for cloud. Skipping cloud.");
                                } else {
                                    messageLabel.setText("Connection to " + prettyName(ar.source) + " failed: " + ar.errorType + ".");
                                }
                                setIconError();
                                progressIndicator.setVisible(false); // do not show progress on failures
                            });
                            // shorter failures
                            try { if (FAIL_DURATION_MS > 0) Thread.sleep(FAIL_DURATION_MS); } catch (InterruptedException ignored) {}
                        }
                    }
                };

                return service.authenticate(identifier, rawPassword, listener);
            }
        };

        // Close loading when finished (state)
        authTask.stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED || newState == Worker.State.FAILED || newState == Worker.State.CANCELLED) {
                Platform.runLater(this::closeLoadingStageSafely);
            }
        });

        authTask.setOnSucceeded(e -> {
            AuthResult result = authTask.getValue();
            setInputsDisabled(false);
            isAuthenticating = false;

            if (result != null && result.success() && result.user() != null) {
                UserAccount account = result.user();

                // 🔹 Save the user in session
                SessionManager.setCurrentUser(account);  // <- The user is saved here

                DatabaseSource used = result.used();
                // Set global mode BEFORE loading the next screen
                if (used == DatabaseSource.CLOUD) DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.CLOUD);
                else if (used == DatabaseSource.LOCAL) DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.LOCAL);

                boolean navigated = navigateAccordingToRoleAndThenShowWelcome(event, result.user(), used);
                if (!navigated) {
                    Alerts.showAlert(Alert.AlertType.ERROR, user, "Navigation error", "Could not open the requested screen after login.");
                }
            } else {
                isAuthenticating = false;
                setInputsDisabled(false);

                String reason;
                if (result == null) reason = "Authentication returned null result.";
                else reason = switch (result.errorType()) {
                    case CLOUD_NO_INTERNET -> "No internet for the cloud database. An attempt was made to reconnect to the local database.";
                    case CLOUD_UNAVAILABLE -> "Cloud database unavailable. An attempt was made to reconnect to the local database.";
                    case LOCAL_UNAVAILABLE -> "Local database unavailable. Please contact your administrator.";
                    case BOTH_UNAVAILABLE -> "It was not possible to connect to either the cloud or local database.";
                    case LoginService.ErrorType.INVALID_CREDENTIALS -> "Incorrect username or password.";
                    default -> "An error occurred while authenticating.";
                };

                Alerts.showAlert(Alert.AlertType.ERROR, user, "Connection result", reason);
            }
        });

        authTask.setOnFailed(e -> {
            isAuthenticating = false;
            setInputsDisabled(false);
            closeLoadingStageSafely();
            Platform.runLater(() -> Alerts.showAlert(Alert.AlertType.ERROR, user, "Unexpected error", "An unexpected error occurred while authenticating. Please try again."));
        });

        Thread th = new Thread(authTask, "login-auth-task");
        th.setDaemon(true);
        th.start();
    }

    /* ========== Navigation and Welcome (same as before) ========== */

    private boolean navigateAccordingToRoleAndThenShowWelcome(ActionEvent event, UserAccount account, DatabaseSource used) {
        String role = account.getRoleType();
        String fxmlPath = null;
        if ("SUPERADMINISTRATOR".equalsIgnoreCase(role)) fxmlPath = FXMLRoutes.SUPERADMIN;
        else if ("ADMINISTRATOR".equalsIgnoreCase(role)) fxmlPath = FXMLRoutes.ADMIN;

        if (fxmlPath == null) {
            Platform.runLater(() -> Alerts.showAlert(Alert.AlertType.WARNING, user, "Access denied", "Your role does not have access."));
            return false;
        }

        try {
            // Set global mode (done before, but we left it for safety)
            if (used == DatabaseSource.CLOUD) DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.CLOUD);
            else if (used == DatabaseSource.LOCAL) DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.LOCAL);

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            // Elegant and centered welcome modal
            String usedText = (used == DatabaseSource.CLOUD) ? "Cloud" : (used == DatabaseSource.LOCAL ? "Local" : "Unknown");

            Label main = new Label("Welcome " + account.getRoleType() + "!");
            main.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");
            main.setWrapText(true);
            main.setAlignment(Pos.CENTER);

            Label sub = new Label("Connected via: " + usedText);
            sub.setStyle("-fx-font-style: italic; -fx-font-size: 12px; -fx-text-fill: #555;");
            sub.setWrapText(true);
            sub.setAlignment(Pos.CENTER);

            VBox content = new VBox(10, main, sub);
            content.setPadding(new Insets(12));
            content.setAlignment(Pos.CENTER);

            DialogPane dp = new DialogPane();
            dp.setContent(content);
            dp.getStylesheets().clear();

            Alert welcome = new Alert(Alert.AlertType.INFORMATION);
            welcome.initOwner(stage);
            welcome.initModality(Modality.WINDOW_MODAL);
            welcome.setTitle("Welcome");
            welcome.setHeaderText(null);
            welcome.getDialogPane().setContent(content);
            welcome.getDialogPane().setMinWidth(360);

            welcome.showAndWait();
            return true;
        } catch (Exception ex) {
            Platform.runLater(() -> Alerts.showAlert(Alert.AlertType.ERROR, user, "Navigation error", "An error occurred while loading the window: " + ex.getMessage()));
            return false;
        }
    }

    /* ========== UI loading stage: centered layout and progress to the right of the text ========== */

    private void createLoadingStage(Window owner, String title, String initialMessage) {
        iconLabel = new Label("\u2139"); // ℹ
        iconLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #2b6cb0;");

        messageLabel = new Label(initialMessage);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #222;");
        messageLabel.setMaxWidth(200);

        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(36, 36);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressIndicator.setVisible(false); // will appear only in 'trying'

        // HBox: [icon] [text VBox] [spacer] [progress]
        VBox textBox = new VBox(messageLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        HBox content = new HBox(14, iconLabel, textBox, progressIndicator);
        content.setPadding(new Insets(16));
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-background-color: #fff; -fx-border-color: #e6e6e6; -fx-border-radius: 6; -fx-background-radius: 6;");

        Scene scene = new Scene(content);
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(scene);

        stage.setMinWidth(350);
        stage.setMinHeight(140);

        loadingStage = stage;
        Platform.runLater(stage::show);
    }

    private void closeLoadingStageSafely() {
        if (Platform.isFxApplicationThread()) closeLoadingStage();
        else Platform.runLater(this::closeLoadingStage);
    }
    private void closeLoadingStage() {
        if (loadingStage != null) {
            try { if (loadingStage.isShowing()) loadingStage.hide(); } finally { loadingStage = null; }
        }
    }

    /* ========== icon helpers ========== */
    private void setIconInfo() {
        if (iconLabel == null) return;
        Platform.runLater(() -> iconLabel.setText("\u2139")); // ℹ
    }
    private void setIconError() {
        if (iconLabel == null) return;
        Platform.runLater(() -> iconLabel.setText("\u26A0")); // ⚠
    }
    private void setIconSuccess() {
        if (iconLabel == null) return;
        Platform.runLater(() -> iconLabel.setText("\u2714")); // ✔
    }

    private static String prettyName(DatabaseSource ds) {
        if (ds == DatabaseSource.CLOUD) return "cloud";
        if (ds == DatabaseSource.LOCAL) return "local";
        return "unknown";
    }

    private void setInputsDisabled(boolean disabled) {
        user.setDisable(disabled);
        password.setDisable(disabled);
    }
}