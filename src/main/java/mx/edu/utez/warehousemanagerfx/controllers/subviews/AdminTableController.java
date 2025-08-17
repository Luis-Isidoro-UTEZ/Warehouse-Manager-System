package mx.edu.utez.warehousemanagerfx.controllers.subviews;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.dao.AdminDao;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminTableController implements Initializable {

    @FXML private TableView<Administrator> tblAdmins;
    @FXML private TableColumn<Administrator, Integer> colAdminId;
    @FXML private TableColumn<Administrator, Integer> colBranchId;
    @FXML private TableColumn<Administrator, String> colName;
    @FXML private TableColumn<Administrator, String> colEmail;
    @FXML private TableColumn<Administrator, String> colPhone;
    @FXML private TableColumn<Administrator, String> colUsername;
    @FXML private TableColumn<Administrator, Void> colAction;

    private String currentOrder = "Id_Admin ASC"; // default

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind columns
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("idBranch"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        // We alternate col-a / col-b to create vertical zebra columns.
        applyZebraCellFactory(colAdminId, "col-a");
        applyZebraCellFactory(colBranchId, "col-b");
        applyZebraCellFactory(colName, "col-a");
        applyZebraCellFactory(colEmail, "col-b");
        applyZebraCellFactory(colPhone, "col-a");
        applyZebraCellFactory(colUsername, "col-b");
        // Action button cell factory (chip style)
        Callback<TableColumn<Administrator, Void>, TableCell<Administrator, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Administrator, Void> call(final TableColumn<Administrator, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Manage Admin");

                    {
                        // Style in CSS
                        btn.getStyleClass().add("chip");
                        btn.setTooltip(new Tooltip("Manage Admin"));
                        btn.setFocusTraversable(false);
                        setAlignment(Pos.CENTER);
                        btn.setOnAction(event -> {
                            Administrator a = getTableView().getItems().get(getIndex());
                            // TODO: handle branch management action using a
                            System.out.println("Manage admin: " + (a != null ? a.toString() : "<null>"));
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
        // Map UI label to DAO order
        switch (orderLabel) {
            case "Name: A to Z":            currentOrder = "Name ASC"; break;
            case "Name: Z to A":            currentOrder = "Name DESC"; break;
            case "Status: Active first":    currentOrder = "Status DESC"; break; // adjust as needed
            case "Status: Inactive first":  currentOrder = "Status ASC";  break;
            default:                        currentOrder = "Id_Admin ASC";
        }
        reloadData();
    }

    private void reloadData() {
        // Split order to column/dir and query DAO (implement AdminDao accordingly)
        String[] parts = currentOrder.split(" ");
        String column = parts[0];
        String dir = parts.length > 1 ? parts[1] : "ASC";
        List<Administrator> rows = new AdminDao().readFilteredAdmins(column, dir);
        ObservableList<Administrator> data = FXCollections.observableArrayList(rows);
        tblAdmins.setItems(data);
    }

    /**
     * Applies a cell factory that sets the provided CSS class to each cell.
     * This ensures vertical zebra coloring per column.
     */
    // Keep column zebra on both filled and empty cells.
    private <T> void applyZebraCellFactory(TableColumn<Administrator, T> column, String cssClass) {
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
