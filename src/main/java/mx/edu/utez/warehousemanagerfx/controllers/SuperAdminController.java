package mx.edu.utez.warehousemanagerfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.controllers.subviews.AdminTableController;
import mx.edu.utez.warehousemanagerfx.controllers.subviews.BranchTableController;
import mx.edu.utez.warehousemanagerfx.models.UserAccount;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class SuperAdminController implements Initializable {

    @FXML
    private ChoiceBox<String> selectorView;
    @FXML
    private ChoiceBox<String> orderByChoiceBox;
    @FXML
    private StackPane tableContainer;

    // Order lists per subview
    private static final List<String> ORDER_BRANCHES = Arrays.asList(
            "Available: High to Low", "Available: Low to High",
            "Rented: High to Low", "Rented: Low to High",
            "Sold: High to Low", "Sold: Low to High"
    );
    private static final List<String> ORDER_ADMINS = Arrays.asList(
            "Name: A to Z", "Name: Z to A",
            "Status: Active first", "Status: Inactive first"
    );

    // Keep reference to the current sub-controller to forward ordering changes
    private Object currentSubController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the selector options
        selectorView.getItems().setAll("Branches", "Admins");
        selectorView.setValue("Branches"); // default

        // Load default subview
        loadSubview("Branches");

        // Listen to selector changes
        selectorView.getSelectionModel().selectedItemProperty()
                .addListener((o, oldVal, newVal) -> loadSubview(newVal));

        // Listen to order changes and forward them to the subview controller
        orderByChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((o, oldVal, newVal) -> notifyOrderingChanged(newVal));
    }

    @FXML
    private void goInfAccountSA(MouseEvent event) {
        try {
            Parent infSAwindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.INFO_ACCOUNT_SUPERADMIN)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(infSAwindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.out.println("Error! Could not show the information account screen.");
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        UserAccount.logout(event);
    }

    @FXML
    private void registerAdmin(ActionEvent event) {
        try {
            Parent rgAdWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.ADMIN_REGISTER)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(rgAdWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error! Could not show the register admin screen.");
        }
    }

    @FXML
    private void registerBranch(ActionEvent event) {
        try {
            Parent rgAdWindow = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.BRANCH_REGISTER)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(rgAdWindow);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error! Could not show the register branch screen.");
        }
    }

    /**
     * Loads the selected subview FXML into the StackPane and configures the "Order by" menu accordingly.
     */
    private void loadSubview(String selection) {
        try {
            String fxmlView;
            if ("Admins".equalsIgnoreCase(selection)) {
                fxmlView = FXMLRoutes.ADMIN_TABLE;
                setupOrderByForAdmins();
            } else {
                fxmlView = FXMLRoutes.BRANCH_TABLE;
                setupOrderByForBranches();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlView));
            Node content = loader.load();
            currentSubController = loader.getController();

            // Swap content
            tableContainer.getChildren().setAll(content);

            // Notify initial order (if any)
            String order = orderByChoiceBox.getSelectionModel().getSelectedItem();
            notifyOrderingChanged(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates "Order by" options for the Branches subview and resets selection.
     */
    private void setupOrderByForBranches() {
        orderByChoiceBox.getItems().setAll(ORDER_BRANCHES);
        orderByChoiceBox.setValue("Order by");
    }

    /**
     * Updates "Order by" options for the Admins subview and resets selection.
     */
    private void setupOrderByForAdmins() {
        orderByChoiceBox.getItems().setAll(ORDER_ADMINS);
        orderByChoiceBox.setValue("Order by");
    }

    /**
     * Forwards the ordering change to the active subview controller, if it supports it.
     * Each subview controller should implement a method like applyOrder(String).
     */
    private void notifyOrderingChanged(String order) {
        if (currentSubController == null || order == null || "Order by".equals(order)) return;

        // Forward to Branch subview
        if (currentSubController instanceof BranchTableController) {
            ((BranchTableController) currentSubController).applyOrder(order);
        }
        // Forward to the Admin subview
        if (currentSubController instanceof AdminTableController) {
            ((AdminTableController) currentSubController).applyOrder(order);
        }
    }
}
