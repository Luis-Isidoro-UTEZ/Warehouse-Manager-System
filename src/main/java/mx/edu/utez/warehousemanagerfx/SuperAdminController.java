package mx.edu.utez.warehousemanagerfx;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class SuperAdminController {
    @FXML
    private TableView<Branch> tblBranches;
    @FXML private TableColumn<Branch, Integer> colBranchId;
    @FXML private TableColumn<Branch, Integer> colAdminId;
    @FXML private TableColumn<Branch, Boolean> colRent;
    @FXML private TableColumn<Branch, Boolean> colSell;
    @FXML private TableColumn<Branch, Boolean> colAvailable;
    @FXML private TableColumn<Branch, Boolean> colExpired;
    @FXML private TableColumn<Branch, Void> colMoreInfo;

    @FXML private Button btnRegisterAdmin;
    @FXML private Button btnRegisterBranch;
    @FXML private Button btnAccount;

    private ObservableList<Branch> branches = FXCollections.observableArrayList();
    private BranchDao branchDao = new BranchDao();

    @FXML
    public void initialize() {
        configureTable();
        loadBranches();
        setupButtons();
    }

    private void configureTable() {
        colBranchId.setCellValueFactory(new PropertyValueFactory<>("branchId"));
        colAdminId.setCellValueFactory(new PropertyValueFactory<>("adminId"));
        colRent.setCellValueFactory(new PropertyValueFactory<>("rent"));
        colSell.setCellValueFactory(new PropertyValueFactory<>("sell"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        colExpired.setCellValueFactory(new PropertyValueFactory<>("expired"));


        colRent.setCellFactory(column -> new TableCell<Branch, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? "Yes" : "No");
            }
        });

        colSell.setCellFactory(column -> new TableCell<Branch, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? "Yes" : "No");
            }
        });

        colAvailable.setCellFactory(column -> new TableCell<Branch, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? "Yes" : "No");
            }
        });

        colExpired.setCellFactory(column -> new TableCell<Branch, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item ? "Yes" : "No");
            }
        });


        colMoreInfo.setCellFactory(param -> new TableCell<>() {
            private final Button btnDetails = new Button("Details");
            {
                btnDetails.setOnAction(event -> {
                    Branch branch = getTableView().getItems().get(getIndex());
                    showBranchDetails(branch);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btnDetails);
            }
        });
    }

    private void loadBranches() {
        try {
            branches.setAll(branchDao.findAll());
            tblBranches.setItems(branches);
        } catch (SQLException e) {
            showError("Database Error", "Error loading branches: " + e.getMessage());
        }
    }

    private void showBranchDetails(Branch branch) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Branch Details");
        alert.setHeaderText("Branch ID: " + branch.getBranchId());
        alert.setContentText("Admin ID: " + branch.getAdminId() + "\n"
                + "Rent: " + (branch.isRent() ? "Yes" : "No") + "\n"
                + "Sell: " + (branch.isSell() ? "Yes" : "No") + "\n"
                + "Available: " + (branch.isAvailable() ? "Yes" : "No") + "\n"
                + "Expired: " + (branch.isExpired() ? "Yes" : "No"));
        alert.showAndWait();
    }

    private void setupButtons() {
        btnRegisterBranch.setOnAction(event -> openBranchRegistration());
        btnRegisterAdmin.setOnAction(event -> openAdminRegistration());
    }

    private void openBranchRegistration() {

    }

    private void openAdminRegistration() {

    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
