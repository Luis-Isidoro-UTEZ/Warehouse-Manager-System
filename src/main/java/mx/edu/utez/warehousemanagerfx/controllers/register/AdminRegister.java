package mx.edu.utez.warehousemanagerfx.controllers.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.util.Objects;

public class AdminRegister {
    @FXML
    private void registerAdmin(ActionEvent event) {
        try {
            Parent regAdWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.REGISTER_ADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(regAdWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.out.println("Error! Could not show the register admin window.");
        }
    }
}
