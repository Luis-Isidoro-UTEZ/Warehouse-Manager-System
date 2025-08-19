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

public class Administrator extends UserAccount {
    private int idAdmin;
    private Integer idBranch;
    private boolean isDeleted;

    public Administrator() {}

    public Administrator(int idUser, String firstName, String middleName, String lastName, String secondLastName,
                         String email, String phone, String username, String passwordKey, String roleType,
                         Integer idBranch, boolean isDeleted) {
        super(idUser, firstName, middleName, lastName, secondLastName, email, phone, username, passwordKey, roleType);
        this.idAdmin = idUser;
        this.idBranch = idBranch;
        this.isDeleted = isDeleted;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public Integer getIdBranch() {
        return idBranch;
    }

    public void setIdBranch(Integer idBranch) {
        this.idBranch = idBranch;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "userData=" + super.toString() +
                "idAdmin=" + idAdmin +
                ", idBranch=" + idBranch +
                ", isDeleted=" + isDeleted +
                '}';
    }

    @FXML
    public static void goHome(ActionEvent event, TextField txt) {
        try {
            Parent loginWindow = FXMLLoader.load(Objects.requireNonNull(Administrator.class.getResource(FXMLRoutes.ADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loginWindow);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, txt, "Error!", "Could not return to the Admin Window.");
        }
    }

    @FXML
    public static void goInfoAccount(ActionEvent event, TextField txt) {
        try {
            Parent infoAdwindow = FXMLLoader.load(Objects.requireNonNull(Administrator.class.getResource(FXMLRoutes.INFO_ACCOUNT_ADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(infoAdwindow);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, txt, "Error!", "Could not show the Information Account Screen.");
        }
    }
}