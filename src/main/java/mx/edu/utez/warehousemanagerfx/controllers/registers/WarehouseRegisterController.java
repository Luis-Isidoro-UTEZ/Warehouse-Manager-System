package mx.edu.utez.warehousemanagerfx.controllers.registers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.Validations;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class WarehouseRegisterController implements Initializable {
    @FXML
    public TextField name;
    @FXML
    public TextField rentalPrice;
    @FXML
    public TextField salePrice;
    @FXML
    public TextField size;
    @FXML
    public TextField image;

    // List of all input nodes that can have an "input-error" style and that have to be validated
    private List<Node> inputNodes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputNodes = List.of(name, rentalPrice, salePrice, size);
        // Add a focused property listener to each node to remove the error style
        for (Node node : inputNodes) {
            node.focusedProperty().addListener((obs, oldVal, newVal) -> {
                // Remove the error style when the node gains focus
                if (newVal) {
                    node.getStyleClass().remove("input-error");
                }
            });
        }
    }

    @FXML
    public void registerWarehouse(ActionEvent event) {
        // 1. Validate if any fields are empty
        if (Validations.isInputFieldsEmpty(inputNodes, name)) return;
        // 2. Validate numerical fields
        if (!Validations.validateDoubleField(rentalPrice, "Rental Price")) {
            return;
        }
        if (!Validations.validateDoubleField(salePrice, "Sale Price")) {
            return;
        }
        if (!Validations.validateDoubleField(size, "Size")) {
            return;
        }
        // If all validations pass, proceed with object creation
        Warehouse w = new Warehouse();
        w.setWarehouseName(name.getText());
        w.setRentalPrice(Double.parseDouble(rentalPrice.getText()));
        w.setSalePrice(Double.parseDouble(salePrice.getText()));
        w.setSizeSqMeters(Double.parseDouble(size.getText()));
        w.setImage(image.getText());

        WarehouseDao dao = new WarehouseDao();

        if (dao.createWarehouse(w)) {
            goHome(event);
            Alerts.showAlert(Alert.AlertType.INFORMATION, name, "Warehouse created!", "The warehouse was successfully created.");
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, name, "Error!", "The warehouse could not be created.");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        Administrator.goHome(event, name);
    }

    @FXML
    private void goInfoAccount(ActionEvent event) {
        Administrator.goInfoAccount(event, name);
    }
}
