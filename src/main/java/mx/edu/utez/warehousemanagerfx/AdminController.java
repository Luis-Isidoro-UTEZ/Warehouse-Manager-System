package mx.edu.utez.warehousemanagerfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import org.controlsfx.control.RangeSlider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private GridPane warehousesGrid;
    @FXML
    private RangeSlider sizeRangeSlider;
    @FXML
    private Label sizeLowLabel;
    @FXML
    private Label sizeHighLabel;
    @FXML
    private RangeSlider priceRangeSlider;
    @FXML
    private Label priceLowLabel;
    @FXML
    private Label priceHighLabel;

    private List<Warehouse> warehouses;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        WarehouseDao dao = new WarehouseDao();
        List<Warehouse> datos = dao.readWarehouses();
        int column = 0;
        int row = 1;
        try {
            for (Warehouse warehouse: datos) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("WarehouseCard.fxml"));
                Pane pane = fxmlLoader.load();
                WarehouseController warehouseController = fxmlLoader.getController();
                warehouseController.setData(warehouse);

                if (column == 3) {
                    column = 0;
                    ++row;
                }
                warehousesGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
