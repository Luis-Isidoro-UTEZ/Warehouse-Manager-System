package mx.edu.utez.warehousemanagerfx.models;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.util.Objects;

public class SuperAdministrator extends UserAccount {
    private int idSuperAdmin;

    public SuperAdministrator() {}

    public SuperAdministrator(int idUser, String firstName, String middleName, String lastName, String secondLastName,
                              String email, String phone, String username, String passwordKey, String roleType) {
        super(idUser, firstName, middleName, lastName, secondLastName, email, phone, username, passwordKey, roleType);
        this.idSuperAdmin = idUser;
    }

    public int getIdSuperAdmin() {
        return idSuperAdmin;
    }

    public void setIdSuperAdmin(int idSuperAdmin) {
        this.idSuperAdmin = idSuperAdmin;
    }

    @Override
    public String toString() {
        return "SuperAdministrator{" +
                "userData=" + super.toString() +
                "idSuperAdmin=" + idSuperAdmin +
                '}';
    }

    @FXML
    public static void goHome(ActionEvent event, TextField txt) {
        try {
            Parent loginWindow = FXMLLoader.load(Objects.requireNonNull(SuperAdministrator.class.getResource(FXMLRoutes.SUPERADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(loginWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, txt, "Error!", "Could not return to the SuperAdmin Window.");
        }
    }

    @FXML
    public static void goInfoAccount(ActionEvent event, TextField txt) {
        try {
            Parent AccountSA = FXMLLoader.load(Objects.requireNonNull(SuperAdministrator.class.getResource(FXMLRoutes.INFO_ACCOUNT_SUPERADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(AccountSA);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, txt, "Error!", "Could not show the Information Account Screen.");
        }
    }
}