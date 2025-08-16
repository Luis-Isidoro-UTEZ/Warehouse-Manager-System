package mx.edu.utez.warehousemanagerfx.controllers;

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
import javafx.scene.layout.*;
import javafx.stage.*;
import mx.edu.utez.warehousemanagerfx.models.UserAccount;
import mx.edu.utez.warehousemanagerfx.services.LoginService;
import mx.edu.utez.warehousemanagerfx.services.LoginService.AuthResult;
import mx.edu.utez.warehousemanagerfx.services.LoginService.AttemptResult;
import mx.edu.utez.warehousemanagerfx.services.LoginService.DatabaseSource;
import mx.edu.utez.warehousemanagerfx.utils.database.DatabaseConnectionFactory;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.util.Objects;

/**
 * LoginController - versión con ProgressIndicator integrado.
 */
public class LoginController {

    @FXML private TextField user;
    @FXML private PasswordField password;

    // Cambia a 0 para quitar pausas forzadas (flujo directo).
    private static final long FORCED_PAUSE_MILLIS = 5000L;

    private Stage loadingStage;
    private Label loadingIconLabel;             // icono (unicode)
    private Label loadingMessageLabel;          // texto de estado
    private ProgressIndicator loadingProgress;  // indicador de carga (estilo "gif")
    private volatile boolean isAuthenticating = false;

    @FXML
    private void login(ActionEvent event) {
        if (isAuthenticating) return;
        isAuthenticating = true;

        final String identifier = (user.getText() == null) ? "" : user.getText().trim();
        final String rawPassword = (password.getText() == null) ? "" : password.getText();

        if (identifier.isEmpty() || rawPassword.isEmpty()) {
            showWarningNonBlocking("Incomplete data", "Enter your username/email and password.");
            isAuthenticating = false;
            return;
        }

        setInputsDisabled(true);
        Window owner = ((Node) event.getSource()).getScene().getWindow();

        // Crear y mostrar loading modal grande (con progress indicator)
        createLoadingStage(owner, "Connecting...", "Initializing authentication...");

        LoginService service = new LoginService();

        Task<AuthResult> authTask = new Task<>() {
            @Override
            protected AuthResult call() {
                LoginService.AuthProgressListener listener = (type, arg) -> {
                    if ("update".equals(type) && arg instanceof String) {
                        String msg = (String) arg;
                        Platform.runLater(() -> {
                            loadingMessageLabel.setText(msg);
                            setIconInfo();
                            loadingProgress.setVisible(true);
                        });
                    } else if ("before".equals(type) && arg instanceof DatabaseSource) {
                        DatabaseSource ds = (DatabaseSource) arg;
                        Platform.runLater(() -> {
                            loadingMessageLabel.setText("Connecting to database (" + prettyName(ds) + ")...");
                            setIconInfo();
                            loadingProgress.setVisible(true);
                        });
                        // 'after' realizará la pausa para mostrar resultado
                    } else if ("after".equals(type) && arg instanceof AttemptResult) {
                        AttemptResult ar = (AttemptResult) arg;
                        Platform.runLater(() -> {
                            if (ar.success) {
                                loadingMessageLabel.setText("Connection to " + prettyName(ar.source) + " successful.");
                                setIconSuccess();
                                loadingProgress.setVisible(false); // ocultar progress cuando hay éxito
                            } else {
                                loadingMessageLabel.setText("Connection to " + prettyName(ar.source) + " failed: " + ar.errorType + ".");
                                setIconError();
                                // mantener el progress visible mientras se intenta la siguiente (UX)
                                loadingProgress.setVisible(false);
                            }
                        });

                        // FORZAR la pausa para que el usuario vea el paso (esto corre en el worker thread)
                        try {
                            if (FORCED_PAUSE_MILLIS > 0) Thread.sleep(FORCED_PAUSE_MILLIS);
                        } catch (InterruptedException ignored) {}
                    }
                };

                // Llamada principal: authenticate con listener
                return service.authenticate(identifier, rawPassword, listener);
            }
        };

        // cerrar loading cuando termine (éxito/fracaso/cancel)
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
                DatabaseSource used = result.used();
                if (used == DatabaseSource.CLOUD) DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.CLOUD);
                else if (used == DatabaseSource.LOCAL) DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.LOCAL);

                // Navegar primero, luego mostrar Welcome modal sobre la nueva pantalla.
                boolean navigated = navigateAccordingToRoleAndThenShowWelcome(event, result.user(), used);
                if (!navigated) {
                    showErrorBlocking("Navigation error", "Could not open the requested screen after login.");
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

                showErrorBlocking("Connection result", reason);
            }
        });

        authTask.setOnFailed(e -> {
            isAuthenticating = false;
            setInputsDisabled(false);
            closeLoadingStageSafely();
            Platform.runLater(() -> showErrorBlocking("Unexpected error", "An unexpected error occurred while authenticating. Please try again."));
        });

        Thread th = new Thread(authTask, "login-auth-task");
        th.setDaemon(true);
        th.start();
    }

    private boolean navigateAccordingToRoleAndThenShowWelcome(ActionEvent event, UserAccount account, DatabaseSource used) {
        String role = account.getRoleType();
        String fxmlPath = null;
        if ("SUPERADMINISTRATOR".equalsIgnoreCase(role)) fxmlPath = FXMLRoutes.SUPERADMIN;
        else if ("ADMINISTRATOR".equalsIgnoreCase(role)) fxmlPath = FXMLRoutes.ADMIN;

        if (fxmlPath == null) {
            Platform.runLater(() -> showWarningNonBlocking("Access denied", "Your role does not have access."));
            return false;
        }

        try {
            // Asegurar modo global antes de cargar FXML
            if (used == DatabaseSource.CLOUD) DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.CLOUD);
            else if (used == DatabaseSource.LOCAL) DatabaseConnectionFactory.setMode(DatabaseConnectionFactory.Mode.LOCAL);

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));

            // **Usar la variante correcta que tú mismo ajustaste:**
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            // Ahora mostrar welcome modal sobre el nuevo stage
            String usedText = (used == DatabaseSource.CLOUD) ? "Cloud" : (used == DatabaseSource.LOCAL ? "Local" : "Unknown");

            Label main = new Label("Welcome " + account.getRoleType() + "!");
            main.setWrapText(true);
            main.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            Label sub = new Label("Connected via: " + usedText);
            sub.setWrapText(true);
            sub.setStyle("-fx-font-style: italic; -fx-font-size: 11px; -fx-text-fill: #333333;");

            VBox content = new VBox(8, main, sub);
            content.setPadding(new Insets(6));

            Alert welcome = new Alert(Alert.AlertType.INFORMATION);
            welcome.initOwner(stage);
            welcome.initModality(Modality.WINDOW_MODAL);
            welcome.setTitle("Welcome");
            welcome.setHeaderText(null);
            welcome.getDialogPane().setContent(content);

            welcome.showAndWait();
            return true;
        } catch (Exception ex) {
            Platform.runLater(() -> showErrorBlocking("Navigation error", "An error occurred while loading the window: " + ex.getMessage()));
            return false;
        }
    }

    private void setInputsDisabled(boolean disabled) {
        user.setDisable(disabled);
        password.setDisable(disabled);
    }

    /* ===== Loading Stage: más ancho y con icono + ProgressIndicator ===== */
    private void createLoadingStage(Window owner, String title, String initialMessage) {
        loadingIconLabel = new Label("\u2139"); // ℹ
        loadingIconLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: #2b6cb0;");

        loadingMessageLabel = new Label(initialMessage);
        loadingMessageLabel.setWrapText(true);
        loadingMessageLabel.setMaxWidth(180);
        loadingMessageLabel.setMinHeight(Region.USE_PREF_SIZE);

        // ProgressIndicator (indeterminado) - se mostrará mientras haya intento en curso
        loadingProgress = new ProgressIndicator();
        loadingProgress.setPrefSize(36, 36);
        loadingProgress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        loadingProgress.setVisible(true);

        VBox textBox = new VBox(4, loadingMessageLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox content = new HBox(12, loadingIconLabel, textBox, loadingProgress);
        content.setPadding(new Insets(16));
        content.setPrefWidth(380);

        Scene scene = new Scene(content);
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(scene);

        stage.setMinWidth(400);
        stage.setMinHeight(120);

        loadingStage = stage;
        Platform.runLater(stage::show);
    }

    private void closeLoadingStageSafely() {
        if (Platform.isFxApplicationThread()) closeLoadingStage();
        else Platform.runLater(this::closeLoadingStage);
    }
    private void closeLoadingStage() {
        if (loadingStage != null) {
            try { if (loadingStage.isShowing()) loadingStage.hide(); }
            finally { loadingStage = null; }
        }
    }

    /* ===== Icon helpers ===== */
    private void setIconInfo() {
        if (loadingIconLabel == null) return;
        Platform.runLater(() -> loadingIconLabel.setText("\u2139")); // ℹ
    }
    private void setIconError() {
        if (loadingIconLabel == null) return;
        Platform.runLater(() -> loadingIconLabel.setText("\u26A0")); // ⚠
    }
    private void setIconSuccess() {
        if (loadingIconLabel == null) return;
        Platform.runLater(() -> loadingIconLabel.setText("\u2714")); // ✔
    }

    /* ===== Alerts blocking/no-blocking ===== */
    private void showErrorBlocking(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        try { Stage stg = (Stage) user.getScene().getWindow(); alert.initOwner(stg); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.showAndWait();
    }

    private void showWarningNonBlocking(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        try { alert.initOwner((Stage) user.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.show();
    }

    private void showErrorNonBlocking(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        try { alert.initOwner((Stage) user.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.show();
    }

    private static String prettyName(DatabaseSource ds) {
        if (ds == DatabaseSource.CLOUD) return "cloud";
        if (ds == DatabaseSource.LOCAL) return "local";
        return "unknown";
    }
}
