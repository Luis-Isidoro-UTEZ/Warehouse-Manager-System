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

public class BranchRegister {
    @FXML
    private void registerBranch(ActionEvent event) {
        try {
            Parent regBrWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.REGISTER_BRANCH)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(regBrWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.out.println("Error! Could not show the register branch window.");
        }
    }
}
