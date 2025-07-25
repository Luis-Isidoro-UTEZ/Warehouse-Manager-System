package mx.edu.utez.warehousemanagerfx;

import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import org.controlsfx.control.RangeSlider;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private GridPane warehousesGrid;
    @FXML
    private JFXToggleButton toggleView;
    @FXML
    private ChoiceBox<String> statusChoiceBox;
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
    private String[] statusChoiceList = {"Show All", "Available", "Rented", "Sold", "Rent Only", "Sale Only"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusChoiceBox.getItems().addAll(statusChoiceList);
        statusChoiceBox.setValue("Status"); // Default Value


        // Load initial view
        loadWarehousesView(true); // true = cardView

        // Detect changes in the toggle
        toggleView.selectedProperty().addListener((obs, oldVal, newVal) -> {
            loadWarehousesView(newVal); // true = cardView, false = listView
        });

        statusChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadWarehousesView(toggleView.isSelected());
        });
    }

    private void loadWarehousesView(boolean isGridView) {
        warehousesGrid.getChildren().clear(); // Clears the current view
        toggleView.setText(isGridView ? "Card View Mode" : "List View Mode");
        String statusSeleccionado = (String) statusChoiceBox.getValue();

        WarehouseDao dao = new WarehouseDao();
        List<Warehouse> datos = new ArrayList<>();
        if (statusSeleccionado.equals("Status") || statusSeleccionado.equals("Show All")) {
            datos = dao.readWarehouses();
        } else {
            datos = dao.readWarehousesStatus(statusSeleccionado);
        }
        int column = 0;
        int row = 1;
        try {
            for (Warehouse warehouse : datos) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                String fxmlPath = isGridView ? "WarehouseCard.fxml" : "WarehouseList.fxml";
                fxmlLoader.setLocation(getClass().getResource(fxmlPath));
                Pane pane = fxmlLoader.load();
                WarehouseController warehouseController = fxmlLoader.getController();
                warehouseController.setData(warehouse);

                if (isGridView) {
                    if (column == 3) {
                        column = 0;
                        ++row;
                    }
                } else {
                    if (column == 1) {
                        column = 0;
                        ++row;
                    }
                }
                warehousesGrid.add(pane, column++, row);
                GridPane.setMargin(pane, new Insets(5));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
