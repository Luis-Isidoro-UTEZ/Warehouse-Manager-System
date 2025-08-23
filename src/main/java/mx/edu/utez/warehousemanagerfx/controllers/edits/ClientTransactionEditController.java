package mx.edu.utez.warehousemanagerfx.controllers.edits;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.Main;
import mx.edu.utez.warehousemanagerfx.controllers.WarehouseDetailsController;
import mx.edu.utez.warehousemanagerfx.models.*;
import mx.edu.utez.warehousemanagerfx.models.dao.AdminDao;
import mx.edu.utez.warehousemanagerfx.models.dao.ClientDao;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseTransactionDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.Validations;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;
import mx.edu.utez.warehousemanagerfx.utils.services.SessionManager;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientTransactionEditController implements Initializable {
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
    @FXML
    private Button edit;

    // List of all input nodes that can have an "input-error" style and that have to be validated
    private List<Node> inputNodes;
    private final String[] transactions = {"Rent", "Sale"};
    private boolean isFirstRent;
    private boolean newSale;
    private Warehouse w;
    private WarehouseTransaction wt;

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
        // Detect changes in the edit button to edit and update.
        edit.setOnAction(e -> {
            if (edit.getText().equals("Edit")) {
                editClientAssignation();
            } else {
                updateClientAssignation();
            }
        });
    }

    public void setCurrentClientAssignation(Warehouse w) {
        this.w = w;
        if (w == null) {
            Alerts.showAlert(Alert.AlertType.ERROR, firstName, "Error!", "No warehouse selected for this client.");
            return;
        }
        int idClient = w.getIdClient();
        ClientDao clientDao = new ClientDao();
        Optional<Client> c = clientDao.readById(idClient);
        c.ifPresent(client -> {
            firstName.setText(client.getFirstName());
            middleName.setText(client.getMiddleName());
            lastName.setText(client.getLastName());
            secondLastName.setText(client.getSecondLastName());
            email.setText(client.getEmail());
            phone.setText(client.getPhone());
        });
        WarehouseTransactionDao wtDao = new WarehouseTransactionDao();
        wt = wtDao.readTransactionById(idClient, w.getIdWarehouse());
        if (w.getStatus().equals("Sold")) {
            transactionType.setValue("Sale");
            transactionType.setDisable(true);
        } else if (w.getStatus().equals("Rented")) {
            isFirstRent = true;
            transactionType.setValue("Rent");
            transactionType.setDisable(true);
            expirationDateControls.setVisible(true);
            paymentExpirationDate.setDisable(true);
            paymentExpirationDate.setValue(wt.getPaymentExpirationDate());
        }
    }

    public void editClientAssignation(){
        firstName.setEditable(true);
        middleName.setEditable(true);
        lastName.setEditable(true);
        secondLastName.setEditable(true);
        email.setEditable(true);
        phone.setEditable(true);
        if (w.getStatus().equals("Rented")) {
            transactionType.setDisable(false);
            paymentExpirationDate.setDisable(false);
        }
        edit.setText("Update");
        edit.setStyle(
                "-fx-background-color: darkgreen;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-family: 'Albert Sans';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;"
        );
    }

    private void updateClientAssignation() {
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
        String firstNameC = firstName.getText();
        String middleNameC = middleName.getText();
        String lastNameC = lastName.getText();
        String secondLastNameC = secondLastName.getText();
        String phoneC = phone.getText();
        String emailC = email.getText();

        // Put new things on the object
        c.setIdClient(w.getIdClient());
        c.setFirstName(firstNameC);
        c.setMiddleName(middleNameC);
        c.setLastName(lastNameC);
        c.setSecondLastName(secondLastNameC);
        c.setPhone(phoneC);
        c.setEmail(emailC);

        Administrator a = (Administrator) SessionManager.getCurrentUser();
        wt.setIdAdmin(a.getIdUser());
        wt.setTransactionType(transactionType.getValue());
        WarehouseTransaction newWt = new WarehouseTransaction();

        if (wt.getTransactionType().equals("Rent")) {
            wt.setPaymentExpirationDate(paymentExpirationDate.getValue());
        } else if (isFirstRent && wt.getTransactionType().equals("Sale")) {
            isFirstRent = false;
            newSale = true;
            newWt.setTransactionType(transactionType.getValue());
            newWt.setIdWarehouse(w.getIdWarehouse());
            newWt.setIdClient(c.getIdClient());
            newWt.setIdAdmin(a.getIdUser());
            w.setIdClient(c.getIdClient());
            w.setStatus("Sold");
        } else if (wt.getTransactionType().equals("Sale")) {
            wt.setPaymentExpirationDate(null);
        }

        firstName.setEditable(false);
        middleName.setEditable(false);
        lastName.setEditable(false);
        secondLastName.setEditable(false);
        email.setEditable(false);
        phone.setEditable(false);
        if (w.getStatus().equals("Rented")) {
            transactionType.setDisable(true);
            paymentExpirationDate.setDisable(true);
        }
        edit.setText("Edit");
        edit.setStyle(
                "-fx-background-color: lightgreen;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-family: 'Albert Sans';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;"
        );
        // Update the DB
        ClientDao clientDao = new ClientDao();
        WarehouseDao warehouseDao = new WarehouseDao();
        WarehouseTransactionDao warehouseTransactionDao = new WarehouseTransactionDao();
        if (clientDao.updateClient(c) && (newSale ? warehouseTransactionDao.createTransaction(newWt) : warehouseTransactionDao.updateTransaction(wt))) {
            warehouseDao.updateWarehouse(w);
            Administrator.goHome(firstName);
            Alerts.showAlert(Alert.AlertType.INFORMATION, firstName, "Successful update!", "The client/transaction was updated successfully.");
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, firstName, "Error!", "The client/transaction could not be updated.");
        }
    }

    @FXML
    private void unassignClient(ActionEvent event) {
        WarehouseDao dao = new WarehouseDao();
        if (Alerts.confirmUnassign(firstName, "client")) {
            w.setIdClient(0);
            w.setStatus("Available");
            if (dao.updateWarehouse(w)) {
                goHome(event);
                Alerts.showAlert(Alert.AlertType.INFORMATION, null, "Successful Unassign!", "The client was successfully unassigned.");
            } else {
                Alerts.showAlert(Alert.AlertType.ERROR, null, "Error!", "The client could not be unassigned.");
            }
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
