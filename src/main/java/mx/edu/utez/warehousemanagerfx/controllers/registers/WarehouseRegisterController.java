package mx.edu.utez.warehousemanagerfx.controllers.registers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import javax.swing.*;
import java.util.Objects;

public class WarehouseRegisterController {
    @FXML
    public TextField name;
    @FXML
    public TextField price;
    @FXML
    public TextField size;
    @FXML
    public TextField image;

    @FXML
    public void registerWarehouse(ActionEvent event) {
        Warehouse w = new Warehouse();
        w.setWarehouseName(name.getText());
        w.setRentalPrice(Double.parseDouble(price.getText()));
        w.setSizeSqMeters(Double.parseDouble(size.getText()));
        w.setImage(image.getText());

        WarehouseDao dao = new WarehouseDao();

        if (dao.createWarehouse(w)) {
            JOptionPane.showMessageDialog(null, "Warehouse created.");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        try {
            Parent loginWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.ADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(loginWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.out.println("Error! Could not return to the admin window.");
        }
    }

    @FXML
    private void goInfAccount(MouseEvent event) {
        try {
            Parent infAdwindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.INFO_ACCOUNT_ADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(infAdwindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.out.println("Error! Could not show the information account screen.");
        }
    }
}
