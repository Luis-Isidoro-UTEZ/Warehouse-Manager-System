package mx.edu.utez.warehousemanagerfx.controllers.registers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mx.edu.utez.warehousemanagerfx.models.*;
import mx.edu.utez.warehousemanagerfx.models.dao.AdminDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.Validations;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminRegisterController implements Initializable {
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
    private TextField username;

    // List of all input nodes that can have an "input-error" style and that have to be validated
    private List<Node> inputNodes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the list here, after @FXML variables are injected
        inputNodes = List.of(firstName, lastName, email, phone, username);

        // Example of character limit:
        Validations.setMaxLength(firstName, 50);
        Validations.setMaxLength(middleName, 50);
        Validations.setMaxLength(lastName, 50);
        Validations.setMaxLength(secondLastName, 50);
        Validations.setMaxLength(email, 100);
        Validations.setMaxLength(phone, 15);
        Validations.setMaxLength(username, 50);

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

    @FXML
    public void registerAdmin(ActionEvent event) {
        // Validate if any fields are empty
        if (Validations.isInputFieldsEmpty(inputNodes, firstName)) return;
        // Validate phone
        if (!Validations.validatePhoneField(phone)) return;
        // Validate duplicates before inserting
        if (Validations.checkDuplicateAdmin(email, phone, username, null)) return;
        // If all validations pass, proceed with object creation
        Administrator a = new Administrator();
        a.setFirstName(firstName.getText());
        a.setMiddleName(middleName.getText());
        a.setLastName(lastName.getText());
        a.setSecondLastName(secondLastName.getText());
        a.setEmail(email.getText());
        a.setPhone(phone.getText());
        a.setUsername(username.getText());
        a.setPasswordKey(a.getUsername());
        // If all validations pass, insert into DB
        AdminDao adminDao = new AdminDao();
        if (adminDao.create(a)) {
            goHome(event);
            Alerts.showAlert(Alert.AlertType.INFORMATION, firstName, "Registration successful!", "Administrator registration was successful.");
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, firstName, "Error!", "Administrator registration could not be performed.");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        SuperAdministrator.goHome(event, firstName);
    }

    @FXML
    private void goInfoAccount(ActionEvent event) {
        SuperAdministrator.goInfoAccount(event, firstName);
    }
}
