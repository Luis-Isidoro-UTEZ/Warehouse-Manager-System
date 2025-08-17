package mx.edu.utez.warehousemanagerfx.controllers;

import com.jfoenix.controls.JFXToggleButton;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import mx.edu.utez.warehousemanagerfx.utils.RangeSliderAnimator;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;
import org.controlsfx.control.RangeSlider;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private GridPane warehousesGrid;
    @FXML
    private JFXToggleButton toggleView;
    @FXML
    private ChoiceBox<String> statusChoiceBox;
    @FXML
    private ChoiceBox<String> orderByChoiceBox;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearFiltersButton;
    @FXML
    private ToggleButton priceToggle;
    @FXML
    private BorderPane rootPane;
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

    private final String[] statusChoiceList = {"Show All", "Available", "Rented", "Sold", "Rent Only", "Sale Only"};
    private final String[] orderByChoiceList = {"Size: High to Low", "Size: Low to High", "Rental Price: High to Low", "Rental Price: Low to High", "Sale Price: High to Low", "Sale Price: Low to High"};
    private double sizeMinValue = 0, sizeMaxValue = 100;
    private double priceMinValue = 0, priceMaxValue = 100;

    /**
     * In the listeners:
     * o: Observable Value;
     * a: Old Value;
     * b: New Value;
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure the initial Status Choice Box
        statusChoiceBox.getItems().addAll(statusChoiceList);
        statusChoiceBox.setValue("Status"); // Default Value

        // Configure the initial Order By Choice Box
        orderByChoiceBox.getItems().addAll(orderByChoiceList);
        orderByChoiceBox.setValue("Order By"); // Default Value

        // Configure Size Range Slider and Price Range Slider controllers
        new RangeSliderAnimator(sizeRangeSlider, sizeLowLabel, sizeHighLabel, rootPane);
        new RangeSliderAnimator(priceRangeSlider, priceLowLabel, priceHighLabel, rootPane);
        setupSizeSlider();
        setupPriceSlider(true); // Starts with Rental Price Mode

        // Detect changes in the toggle view
        toggleView.selectedProperty()
                .addListener((o, a, b) -> {
                    toggleView.setText(b ? "Card View" : "List View");
                    loadFilteredViewSync();
                });

        // Detect changes in the status choice box
        statusChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((o, a, b) -> { searchTextField.clear(); loadFilteredViewSync(); });

        // Detect changes in the status choice box
        orderByChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((o, a, b) -> loadFilteredViewSync());

        // Detect changes in the Price Toggle controller to switch between Rental Price and Sale Price
        priceToggle.selectedProperty().addListener((o, a, b) -> {
            priceToggle.setText(b ? "Sale Price" : "Rental Price");
            setupPriceSlider(!b); // true -> sale, false -> rental
            loadFilteredViewSync();
        });

        // Detect changes in the Size Range Slider and Price Range Slider controllers
        setupSliderReleaseEvents(sizeRangeSlider);
        setupSliderReleaseEvents(priceRangeSlider);

        clearFiltersButton.setOnAction(e -> {
            statusChoiceBox.setValue("Status");
            orderByChoiceBox.setValue("Order By");
            searchTextField.clear();
            setupSizeSlider();
            setupPriceSlider(!priceToggle.isSelected());
            loadFilteredViewSync();
        });

        // Search Button: ONLY here we execute the Task if there is text
        searchButton.setOnAction(e -> {
            if (searchTextField.getText().isEmpty()) {
                loadFilteredViewSync();
            } else {
                loadFilteredViewAsync();
            }
        });

        // Load initial view
        loadFilteredViewSync();
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent loginWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.LOGIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(loginWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
            JOptionPane.showMessageDialog(null, "See you soon, Admin!");
        } catch (Exception e) {
            System.out.println("Error! Could not return to the login screen.");
        }
    }

    @FXML
    private void goInfAccount(MouseEvent event) {
        try {
            Parent infAdwindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.INF_ACCOUNT_ADMIN)));
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

    @FXML
    private void addWarehouse(ActionEvent event) {
        try {
            Parent rwWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.WAREHOUSE_REGISTER)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(rwWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error! Could not load the add warehouse screen.");
        }
    }

    private void loadWarehouses(List<Warehouse> data, boolean isGridView) {
        warehousesGrid.getChildren().clear(); // Clears the current view
        int column = 0;
        int row = 1;
        try {
            for (Warehouse warehouse : data) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                String fxmlPath = isGridView ? FXMLRoutes.WAREHOUSE_CARD : FXMLRoutes.WAREHOUSE_LIST;
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

    private List<Warehouse> fetchFilteredWarehouses() {
        WarehouseDao dao = new WarehouseDao();

        String keyword        = searchTextField.getText();
        String status         = statusChoiceBox.getValue();
        String orderBy        = orderByChoiceBox.getValue();
        boolean isPriceMode   = !priceToggle.isSelected(); //  true = salePrice, false = rentalPrice
        boolean statusAll     = status.equals("Show All") || status.equals("Status"); // Status: Default Value
        double minSize        = sizeRangeSlider.getLowValue();
        double maxSize        = sizeRangeSlider.getHighValue();
        double minPrice       = priceRangeSlider.getLowValue();
        double maxPrice       = priceRangeSlider.getHighValue();

        String orderColumn, orderDir;
        switch (orderBy) {
            case "Size: High to Low":         orderColumn="Size_Sq_Meters"; orderDir="DESC"; break;
            case "Size: Low to High":         orderColumn="Size_Sq_Meters"; orderDir="ASC";  break;
            case "Rental Price: High to Low": orderColumn="Rental_Price";   orderDir="DESC"; break;
            case "Rental Price: Low to High": orderColumn="Rental_Price";   orderDir="ASC";  break;
            case "Sale Price: High to Low":   orderColumn="Sale_Price";     orderDir="DESC"; break;
            case "Sale Price: Low to High":   orderColumn="Sale_Price";     orderDir="ASC";  break;
            default:                          orderColumn="Id_Warehouse";   orderDir="ASC";
        }

        // Flags for the DAO
        boolean useKeyword = !keyword.isEmpty();
        boolean useStatus  = !statusAll;
        boolean useSlider  = isRangeSliderModified();

        return dao.readWarehouses(
                keyword,
                status,
                minPrice, maxPrice,
                minSize,  maxSize,
                isPriceMode,
                orderColumn, orderDir,
                useKeyword, useStatus, useSlider
        );
    }

    private void loadFilteredViewSync() {
        List<Warehouse> data = fetchFilteredWarehouses();
        loadWarehouses(data, toggleView.isSelected());
    }

    private void loadFilteredViewAsync() {
        spinner.setVisible(true);
        searchButton.setDisable(true);

        Task<List<Warehouse>> task = new Task<>() {
            @Override
            protected List<Warehouse> call() {
                return fetchFilteredWarehouses();
            }
        };
        task.setOnSucceeded(e -> {
            loadWarehouses(task.getValue(), toggleView.isSelected());
            spinner.setVisible(false);
            searchButton.setDisable(false);
        });
        task.setOnFailed(e -> {
            spinner.setVisible(false);
            searchButton.setDisable(false);
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private boolean isRangeSliderModified() {
        return sizeRangeSlider.getLowValue() > sizeMinValue
                || sizeRangeSlider.getHighValue() < sizeMaxValue
                || priceRangeSlider.getLowValue() > priceMinValue
                || priceRangeSlider.getHighValue() < priceMaxValue;
    }

    // Function to record "drag out" behavior
    private void setupSliderReleaseEvents(RangeSlider slider) {
        slider.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            loadFilteredViewSync();
        });
    }

    private void setupSizeSlider() {
        WarehouseDao dao = new WarehouseDao();
        try {
            double[] minMax = dao.getMinMax("Size_Sq_Meters");
            sizeMinValue = minMax[0];
            sizeMaxValue = minMax[1];
            sizeRangeSlider.setMin(sizeMinValue);
            sizeRangeSlider.setMax(sizeMaxValue);
            sizeRangeSlider.setBlockIncrement((sizeMaxValue - sizeMinValue) / 25);
            sizeRangeSlider.setMajorTickUnit((sizeMaxValue - sizeMinValue) / 2);
            sizeRangeSlider.setMinorTickCount(3);
            sizeRangeSlider.setLowValue(sizeMinValue);
            sizeRangeSlider.setHighValue(sizeMaxValue);
        } catch (Exception e) {
            System.err.println("Error setting up size slider: " + e.getMessage());
            sizeRangeSlider.setLowValue(0);
            sizeRangeSlider.setHighValue(100);
        }
    }

    private void setupPriceSlider(boolean isMode) {
        WarehouseDao dao = new WarehouseDao();
        String priceType = isMode ? "Rental_Price" : "Sale_Price";
        try {
            double[] minMax = dao.getMinMax(priceType);
            priceMinValue = minMax[0];
            priceMaxValue = minMax[1];
            priceRangeSlider.setMin(priceMinValue);
            priceRangeSlider.setMax(priceMaxValue);
            priceRangeSlider.setBlockIncrement((priceMaxValue - priceMinValue) / 25);
            priceRangeSlider.setMajorTickUnit((priceMaxValue - priceMinValue) / 2);
            priceRangeSlider.setMinorTickCount(1);
            priceRangeSlider.setLowValue(priceMinValue);
            priceRangeSlider.setHighValue(priceMaxValue);
        } catch (Exception e) {
            System.err.println("Error setting up price slider: " + e.getMessage());
            priceRangeSlider.setLowValue(0);
            priceRangeSlider.setHighValue(100);
        }
    }
}
