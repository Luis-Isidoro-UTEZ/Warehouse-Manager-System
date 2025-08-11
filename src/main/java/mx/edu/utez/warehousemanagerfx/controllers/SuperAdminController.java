package mx.edu.utez.warehousemanagerfx.controllers;

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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.models.dao.BranchDao;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class SuperAdminController implements Initializable {
    @FXML
    private ChoiceBox<String> orderByChoiceBox;
    @FXML
    private Button btnRegisterAdmin;
    @FXML
    private Button btnRegisterBranch;
    @FXML
    private StackPane tableContainer;
    @FXML
    private TableView<Branch> tblBranches;
    @FXML
    private TableColumn<Branch, Integer> colBranchId;
    @FXML
    private TableColumn<Branch, String> colCode;
    @FXML
    private TableColumn<Branch, Integer> colAdminId;
    @FXML
    private TableColumn<Branch, Integer> colRented;
    @FXML
    private TableColumn<Branch, Integer> colSold;
    @FXML
    private TableColumn<Branch, Integer> colAvailable;
    @FXML
    private TableColumn<Branch, Void> colAction;

    private final String[] orderByChoiceList = {"Available: High to Low", "Available: Low to High", "Rented: High to Low", "Rented: Low to High", "Sold: High to Low", "Sold: Low to High"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure the initial Order By Choice Box
        orderByChoiceBox.getItems().addAll(orderByChoiceList);
        orderByChoiceBox.setValue("Order By"); // Default Value

        // Detect changes in the status choice box
        orderByChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((o, a, b) -> loadTableView());

        // 1) Load data
        List<Branch> branches = new ArrayList<>();
        BranchDao dao = new BranchDao();
        branches = dao.readBranches();

        // 2) Configure value factories
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("idBranch"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("branchCode"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("")); // TODO: bind real adminId property if available
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
                            System.out.println("Manage branch: " + (b != null ? b.getBranchCode() : "<null>"));
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

        // 3) Bind data
        ObservableList<Branch> observableBranches = FXCollections.observableList(branches);
        tblBranches.setItems(observableBranches);

        tblBranches.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (tblBranches == null) return; // defensive check

            if (isNowFocused) {
                if (!tblBranches.getStyleClass().contains("focused")) {
                    tblBranches.getStyleClass().add("focused");
                }
            } else {
                tblBranches.getStyleClass().remove("focused");
            }
        });

        // Also handle clicks on the TableView's header or other internal nodes that may not give the TableView the focus
        // (rare, but safe): when user presses inside the container, request focus to the table so the visual toggles.
        tblBranches.setOnMouseClicked(e -> {
            if (tblBranches != null && !tblBranches.isFocused()) {
                tblBranches.requestFocus();
            }
        });
    }

    private List<Branch> fetchFilteredBranches() {
        BranchDao dao = new BranchDao();
        String orderColumn, orderDir;
        String orderBy = orderByChoiceBox.getValue();
        switch (orderBy) {
            case "Available: High to Low": orderColumn="Available_Count"; orderDir="DESC"; break;
            case "Available: Low to High": orderColumn="Available_Count"; orderDir="ASC";  break;
            case "Rented: High to Low":    orderColumn="Rented_Count";    orderDir="DESC"; break;
            case "Rented: Low to High":    orderColumn="Rented_Count";    orderDir="ASC";  break;
            case "Sold: High to Low":      orderColumn="Sold_Count";      orderDir="DESC"; break;
            case "Sold: Low to High":      orderColumn="Sold_Count";      orderDir="ASC";  break;
            default:                       orderColumn="Id_Branch";       orderDir="ASC";
        }
        return dao.readFilteredBranches(orderColumn, orderDir);
    }

    private void loadTableView() {
        BranchDao dao = new BranchDao();
        List<Branch> branches = new ArrayList<>();
        branches = fetchFilteredBranches();
        ObservableList<Branch> observableBranches = FXCollections.observableList(branches);
        tblBranches.setItems(observableBranches);
        tblBranches.refresh();
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
            JOptionPane.showMessageDialog(null, "¡Hasta Pronto SuperAdmin!");
        } catch (Exception e) {
            System.out.println("Ocurrió un Error al cargar la escena del LoginWindow");
        }
    }
}
