package mx.edu.utez.warehousemanagerfx;

import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
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
    private JFXToggleButton toggleView;
    @FXML
    private ChoiceBox<String> statusChoiceBox;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
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
            String keyword = searchTextField.getText();
            if(!keyword.isEmpty()) {
                searchAndLoadView();
            } else {
                loadWarehousesView(newVal); // true = cardView, false = listView
            }
        });

        statusChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            searchTextField.setText("");
            loadWarehousesView(toggleView.isSelected());
        });
    }

    private void loadWarehouses(List<Warehouse> data, boolean isGridView) {
        int column = 0;
        int row = 1;
        try {
            for (Warehouse warehouse : data) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                String fxmlPath = isGridView ? "WarehouseCard.fxml" : "WarehouseList.fxml";
                fxmlLoader.setLocation(getClass().getResource(fxmlPath));
                Pane pane = fxmlLoader.load();
                WarehouseController warehouseController = fxmlLoader.getController();
                warehouseController.setData(warehouse);

                if (isGridView && column == 3 || !isGridView && column == 1) {
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

    private void loadWarehousesView(boolean isGridView) {
        warehousesGrid.getChildren().clear(); // Clears the current view
        toggleView.setText(isGridView ? "Card View Mode" : "List View Mode");
        String statusSeleccionado = (String) statusChoiceBox.getValue();

        WarehouseDao dao = new WarehouseDao();
        List<Warehouse> data = new ArrayList<>();
        if (statusSeleccionado.equals("Status") || statusSeleccionado.equals("Show All")) {
            data = dao.readWarehouses();
        } else {
            data = dao.readWarehousesStatus(statusSeleccionado);
        }
        loadWarehouses(data, isGridView);
    }

    private void searchAndLoadView() {
        warehousesGrid.getChildren().clear();
        String keyword = searchTextField.getText();
        String selectedStatus = statusChoiceBox.getValue();
        boolean isGridView = toggleView.isSelected();
        toggleView.setText(isGridView ? "Card View Mode" : "List View Mode");

        Task<List<Warehouse>> searchTask = new Task<>() {
            @Override
            protected List<Warehouse> call() throws Exception {
                WarehouseDao dao = new WarehouseDao();
                if ((selectedStatus.equals("Status") || selectedStatus.equals("Show All")) && keyword.isEmpty()) {
                    return dao.readWarehouses(); // without filters
                } else if (selectedStatus.equals("Status") || selectedStatus.equals("Show All")) {
                    return dao.readWarehousesSearch(keyword); // keyword only
                } else if (keyword.isEmpty()) {
                    return dao.readWarehousesStatus(selectedStatus); // only status
                } else {
                    return dao.readWarehousesSearchAndStatus(keyword, selectedStatus); // both filters combined
                }
            }
        };

        searchTask.setOnSucceeded(e -> {
            List<Warehouse> data = searchTask.getValue();
            loadWarehouses(data, isGridView);
            spinner.setVisible(false);
            searchButton.setDisable(false);
        });

        searchTask.setOnFailed(e -> {
            spinner.setVisible(false);
            searchButton.setDisable(false);
            System.err.println("Error loading warehouses: " + searchTask.getException());
        });

        spinner.setVisible(true);
        searchButton.setDisable(true);
        new Thread(searchTask).start();
    }

    public void search(ActionEvent event) {
        searchAndLoadView();
    }
}
