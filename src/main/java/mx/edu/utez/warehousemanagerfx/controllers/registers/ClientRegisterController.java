package mx.edu.utez.warehousemanagerfx.controllers.registers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.Main;
import mx.edu.utez.warehousemanagerfx.controllers.WarehouseDetailsController;
import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Client;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.WarehouseTransaction;
import mx.edu.utez.warehousemanagerfx.models.dao.ClientDao;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseTransactionDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.Validations;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;
import mx.edu.utez.warehousemanagerfx.utils.services.SessionManager;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ClientRegisterController implements Initializable {
    @FXML
    private TextField firstName;
    @FXML
    private TextField middleName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField secondLastName;
    @FXML
    private TextField email;
    @FXML
    private TextField phone;
    @FXML
    private ChoiceBox<String> transactionType;
    @FXML
    private DatePicker paymentExpirationDate;
    @FXML
    private VBox expirationDateControls;

    // List of all input nodes that can have an "input-error" style and that have to be validated
    private List<Node> inputNodes;
    private final String[] transactions = {"Rent", "Sale"};
    private Warehouse w;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the list here, after @FXML variables are injected
        inputNodes = List.of(firstName, lastName, email, phone, transactionType, paymentExpirationDate);
        // Configure the initial transactionType Choice Box
        transactionType.getItems().addAll(transactions);
        // Detect changes in the transactionType choice box
        transactionType.getSelectionModel().selectedItemProperty()
                .addListener((o, a, b) -> {
                    if (b.equals("Rent")) {
                        expirationDateControls.setVisible(true);
                    } else {
                        expirationDateControls.setVisible(false);
                    }
                });
        // Add a focused property listener to each node to remove the error style
        for (Node node : inputNodes) {
            node.focusedProperty().addListener((obs, oldVal, newVal) -> {
                // Remove the error style when the node gains focus
                if (newVal) {
                    node.getStyleClass().remove("input-error");
                }
            });
        }
    }

    public void setCurrentWarehouse(Warehouse w) {
        this.w = w;
    }

    @FXML
    public void registerClient(ActionEvent event) {
        if (w == null) {
            Alerts.showAlert(Alert.AlertType.ERROR, firstName, "Error!", "No warehouse selected for this client.");
            return;
        }
        // Validate if any fields are empty
        if (Validations.isInputFieldsEmpty(inputNodes, firstName)) return;
        // Validate phone
        if (!Validations.validatePhoneField(phone)) return;
        // If all validations pass, proceed with object creation
        Client c = new Client();
        c.setFirstName(firstName.getText());
        c.setMiddleName(middleName.getText());
        c.setLastName(lastName.getText());
        c.setSecondLastName(secondLastName.getText());
        c.setEmail(email.getText());
        c.setPhone(phone.getText());

        String transaction = "";
        if (transactionType.getValue().equals("Rent")) {
            w.setStatus("Rented");
            transaction = "rental";
        } else if (transactionType.getValue().equals("Sale")) {
            w.setStatus("Sold");
            transaction = "sale";
        }

        ClientDao clientDao = new ClientDao();
        if (clientDao.createClient(c)) {
            WarehouseTransaction wt = new WarehouseTransaction();
            wt.setTransactionType(transactionType.getValue());
            wt.setPaymentExpirationDate(paymentExpirationDate.getValue());
            wt.setIdWarehouse(w.getIdWarehouse());
            wt.setIdClient(c.getIdClient());
            w.setIdClient(c.getIdClient());
            Administrator a = (Administrator) SessionManager.getCurrentUser();
            wt.setIdAdmin(a.getIdUser());
            WarehouseTransactionDao warehouseTransactionDao = new WarehouseTransactionDao();
            if (warehouseTransactionDao.createTransaction(wt)) {
                WarehouseDao warehouseDao = new WarehouseDao();
                warehouseDao.updateWarehouse(w);
                goHome(event);
                Alerts.showAlert(Alert.AlertType.INFORMATION, firstName, "Registration successful!", "The " + transaction + " was successful.");
            }
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, firstName, "Error!", "The " + transaction + " could not be made.");
        }
    }

    @FXML
    private void returnWarehouseDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(FXMLRoutes.WAREHOUSE_DETAILS));
            Parent warehouseDetailsWindow = loader.load();
            WarehouseDetailsController controller = loader.getController();
            controller.setWarehouse (w);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(warehouseDetailsWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, firstName, "Error!", "Could not load the warehouse details window.");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        Administrator.goHome(firstName);
    }
}
