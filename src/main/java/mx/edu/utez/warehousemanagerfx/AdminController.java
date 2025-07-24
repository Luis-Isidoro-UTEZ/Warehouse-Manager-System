package mx.edu.utez.warehousemanagerfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
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
        warehouses = new ArrayList<>(getWarehouses());
        int column = 0;
        int row = 1;
        try {
            for (Warehouse warehouse: warehouses) {
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

    private List<Warehouse> getWarehouses() {
        List<Warehouse> ls = new ArrayList<>();

        Warehouse warehouse = new Warehouse();
        warehouse.setName("CDMX\nLos Santos");
        warehouse.setImgSrc("/mx/edu/utez/warehousemanagerfx/img/Warehouse.jpg");
        warehouse.setPrice(2500);
        warehouse.setSize(240);
        warehouse.setStatus("Available");
        ls.add(warehouse);

        warehouse = new Warehouse();
        warehouse.setName("CDMX");
        warehouse.setImgSrc("/mx/edu/utez/warehousemanagerfx/img/Warehouse.jpg");
        warehouse.setPrice(2500);
        warehouse.setSize(240);
        warehouse.setStatus("Available");
        ls.add(warehouse);

        warehouse = new Warehouse();
        warehouse.setName("CDMX");
        warehouse.setImgSrc("/mx/edu/utez/warehousemanagerfx/img/Warehouse.jpg");
        warehouse.setPrice(2500);
        warehouse.setSize(240);
        warehouse.setStatus("Available");
        ls.add(warehouse);

        warehouse = new Warehouse();
        warehouse.setName("CDMX");
        warehouse.setImgSrc("/mx/edu/utez/warehousemanagerfx/img/Warehouse.jpg");
        warehouse.setPrice(2500);
        warehouse.setSize(240);
        warehouse.setStatus("Available");
        ls.add(warehouse);

        warehouse = new Warehouse();
        warehouse.setName("CDMX");
        warehouse.setImgSrc("/mx/edu/utez/warehousemanagerfx/img/Warehouse.jpg");
        warehouse.setPrice(2500);
        warehouse.setSize(240);
        warehouse.setStatus("Available");
        ls.add(warehouse);

        warehouse = new Warehouse();
        warehouse.setName("CDMX");
        warehouse.setImgSrc("/mx/edu/utez/warehousemanagerfx/img/Warehouse.jpg");
        warehouse.setPrice(2500);
        warehouse.setSize(240);
        warehouse.setStatus("Available");
        ls.add(warehouse);

        warehouse = new Warehouse();
        warehouse.setName("CDMX");
        warehouse.setImgSrc("/mx/edu/utez/warehousemanagerfx/img/Warehouse.jpg");
        warehouse.setPrice(2500);
        warehouse.setSize(240);
        warehouse.setStatus("Available");
        ls.add(warehouse);

        warehouse = new Warehouse();
        warehouse.setName("CDMX");
        warehouse.setImgSrc("/mx/edu/utez/warehousemanagerfx/img/Warehouse.jpg");
        warehouse.setPrice(2500);
        warehouse.setSize(240);
        warehouse.setStatus("Available");
        ls.add(warehouse);

        return ls;
    }
}
