package mx.edu.utez.warehousemanagerfx.controllers.register;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;

import javax.swing.*;

public class WarehouseRegister {
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
            JOptionPane.showMessageDialog(null, "Platillo creado.");
        }
    }
}
