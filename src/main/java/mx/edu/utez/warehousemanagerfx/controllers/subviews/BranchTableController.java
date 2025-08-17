package mx.edu.utez.warehousemanagerfx.controllers.subviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.models.dao.BranchDao;

import java.net.URL;
import java.util.List;
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
                            // TODO: handle branch management action using b
                            System.out.println("Manage branch: " + (b != null ? b.toString() : "<null>"));
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
