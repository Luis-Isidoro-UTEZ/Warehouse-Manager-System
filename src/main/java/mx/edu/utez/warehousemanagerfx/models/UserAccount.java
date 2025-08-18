package mx.edu.utez.warehousemanagerfx.models;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import javax.swing.*;
import java.util.Objects;

public class UserAccount {
    private int idUser;
    private String fullName;
    private String email;
    private String phone;
    private String username;
    private String passwordKey; // Hashed password
    private String roleType; // "SUPERADMINISTRATOR" o "ADMINISTRATOR"

    public UserAccount() {
    }

    public UserAccount(int idUser, String fullName, String email, String phone,
                       String username, String passwordKey, String roleType) {
        this.idUser = idUser;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.passwordKey = passwordKey;
        this.roleType = roleType;
    }

    // Getters y setters
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordKey() { return passwordKey; }
    public void setPasswordKey(String passwordKey) { this.passwordKey = passwordKey; }
    public String getRoleType() { return roleType; }
    public void setRoleType(String roleType) { this.roleType = roleType; }

    @Override
    public String toString() {
        return "UserAccount{" +
                "idUser=" + idUser +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", username='" + username + '\'' +
                ", roleType='" + roleType + '\'' +
                '}';
    }

    @FXML
    public static void logout(ActionEvent event) {
        try {
            Parent loginWindow = FXMLLoader.load(Objects.requireNonNull(UserAccount.class.getResource(FXMLRoutes.LOGIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(loginWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            Label main = new Label("See you soon, Admin!");
            main.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");
            main.setWrapText(true);
            main.setAlignment(Pos.CENTER);

            VBox content = new VBox(10, main);
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
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Could not return to the login screen.");
            alert.show();
        }
    }
}
