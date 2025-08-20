package mx.edu.utez.warehousemanagerfx.controllers.subviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import mx.edu.utez.warehousemanagerfx.Main;
import mx.edu.utez.warehousemanagerfx.controllers.edits.BranchEditController;
import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.models.dao.BranchDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;
import mx.edu.utez.warehousemanagerfx.utils.routes.ImageRoutes;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class BranchTableController implements Initializable {

    @FXML private TableView<Branch> tblBranches;
    @FXML private TableColumn<Branch, Integer> colBranchId;
    @FXML private TableColumn<Branch, String> colCode;
    @FXML private TableColumn<Branch, Integer> colAdminId;
    @FXML private TableColumn<Branch, Integer> colRented;
    @FXML private TableColumn<Branch, Integer> colSold;
    @FXML private TableColumn<Branch, Integer> colAvailable;
    @FXML private TableColumn<Branch, Void> colAction;

    private String currentOrder = "Id_Branch ASC"; // default

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind columns
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("idBranch"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("branchCode"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("idAdmin")); // adjust to your model
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("availableCount"));
        colRented.setCellValueFactory(new PropertyValueFactory<>("rentedCount"));
        colSold.setCellValueFactory(new PropertyValueFactory<>("soldCount"));
        // We alternate col-a / col-b to create vertical zebra columns.
        applyZebraCellFactory(colBranchId, "col-a");
        applyZebraCellFactory(colCode, "col-b");
        applyZebraCellFactory(colAdminId, "col-a");
        applyZebraCellFactory(colAvailable, "col-b");
        applyZebraCellFactory(colRented, "col-a");
        applyZebraCellFactory(colSold, "col-b");
        // Action button cell factory (chip style)
        Callback<TableColumn<Branch, Void>, TableCell<Branch, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Branch, Void> call(final TableColumn<Branch, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Manage Branch");

                    {
                        // Style in CSS
                        btn.getStyleClass().add("chip");
                        btn.setTooltip(new Tooltip("Manage Branch"));
                        btn.setFocusTraversable(false);
                        setAlignment(Pos.CENTER);
                        btn.setOnAction(event -> {
                            Branch b = getTableView().getItems().get(getIndex());
                            openManagementWindow(b);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        setGraphic(empty ? null : btn);
                        // Apply column zebra style for the action column as well
                        getStyleClass().removeAll("col-a", "col-b", "is-empty");
                        getStyleClass().add("col-a");
                    }

                };
            }
        };
        colAction.setCellFactory(cellFactory);
        // Initial load
        reloadData();
    }

    public void applyOrder(String orderLabel) {
        // Map UI label to SQL order; adjust to your DAO
        switch (orderLabel) {
            case "Available: High to Low": currentOrder = "Available_Count DESC"; break;
            case "Available: Low to High": currentOrder = "Available_Count ASC";  break;
            case "Rented: High to Low":    currentOrder = "Rented_Count DESC";    break;
            case "Rented: Low to High":    currentOrder = "Rented_Count ASC";     break;
            case "Sold: High to Low":      currentOrder = "Sold_Count DESC";      break;
            case "Sold: Low to High":      currentOrder = "Sold_Count ASC";       break;
            default:                       currentOrder = "Id_Branch ASC";
        }
        reloadData();
    }

    private void reloadData() {
        // Query DAO using the mapped order
        String[] parts = currentOrder.split(" ");
        String column = parts[0];
        String dir = parts.length > 1 ? parts[1] : "ASC";
        List<Branch> rows = new BranchDao().readFilteredBranches(column, dir);
        ObservableList<Branch> data = FXCollections.observableArrayList(rows);
        tblBranches.setItems(data);
    }

    public void removeDeletedBranch(Branch b) {
        tblBranches.getItems().remove(b);
    }

    private void openManagementWindow(Branch b) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(FXMLRoutes.BRANCH_EDIT));
            Parent branchEditWindow = loader.load();
            // 1. Pass the branch to the controller
            BranchEditController controller = loader.getController();
            controller.setBranch(b);
            controller.setParentController(this);
            // 2. Get a new stage (window/scene/stage)
            Stage stage = new Stage();
            // 3. Prepare the new scene.
            Scene escena = new Scene(branchEditWindow);
            // 4. Place the new scene on the stage.
            stage.setScene(escena);
            stage.setTitle("Manage Branch");
            // Change the stage icon
            Image icon = new Image(Objects.requireNonNull(getClass().getResource(ImageRoutes.MAIN_APP_ICON)).toString());
            stage.getIcons().add(icon);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.centerOnScreen();
            // 5. Make sure it's visible.
            stage.showAndWait();
            tblBranches.refresh(); //<--- Refresh is only for data in Java memory and not DB.
        }catch(IOException e){
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, null, "Error!", "Could not load the Branch window.");
        }
    }

    /**
     * Applies a cell factory that sets the provided CSS class to each cell.
     * This ensures vertical zebra coloring per column.
     */
    // Keep column zebra on both filled and empty cells.
    private <T> void applyZebraCellFactory(TableColumn<Branch, T> column, String cssClass) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                // Content
                setText(empty ? null : (item == null ? "" : String.valueOf(item)));
                setGraphic(null);

                // Reset and re-apply zebra classes
                getStyleClass().removeAll("col-a", "col-b", "is-empty");

                // Always keep the zebra class so the column color persists
                getStyleClass().add(cssClass);

                // Mark empty cells to use a paler variant
                if (empty || item == null) {
                    getStyleClass().add("is-empty");
                }
            }
        });
    }
}
