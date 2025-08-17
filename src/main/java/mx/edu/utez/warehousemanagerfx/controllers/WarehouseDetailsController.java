package mx.edu.utez.warehousemanagerfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.util.Objects;

public class WarehouseDetailsController {

    @FXML
    private void goHomeAD(ActionEvent event) {
        try {
            Parent loginWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.ADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(loginWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error! Could not return to the admin window.");
        }
    }

    @FXML
    private void goAccount(ActionEvent event) {
        try {
            Parent infAccWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.INF_ACCOUNT_ADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(infAccWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error! Could not return to the account information window.");
        }
    }
}
