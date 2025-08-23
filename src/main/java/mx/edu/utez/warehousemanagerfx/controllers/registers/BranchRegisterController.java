package mx.edu.utez.warehousemanagerfx.controllers.registers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.models.SuperAdministrator;
import mx.edu.utez.warehousemanagerfx.models.dao.AdminDao;
import mx.edu.utez.warehousemanagerfx.models.dao.BranchDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.Validations;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BranchRegisterController implements Initializable {
    @FXML
    private TextField country;
    @FXML
    private TextField state;
    @FXML
    private TextField municipality;
    @FXML
    private TextField postalCode;
    @FXML
    private TextField neighborhood;
    @FXML
    private TextField addressDetail;

    // List of all input nodes that can have an "input-error" style and that have to be validated
    private List<Node> inputNodes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the list here, after @FXML variables are injected
        inputNodes = List.of(country, state, municipality, postalCode, neighborhood, addressDetail);

        // Example of character limit:
        Validations.setMaxLength(country, 50);
        Validations.setMaxLength(state, 50);
        Validations.setMaxLength(municipality, 50);
        Validations.setMaxLength(postalCode, 5);
        Validations.setMaxLength(neighborhood, 50);
        Validations.setMaxLength(addressDetail, 150);

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
        if (Validations.isInputFieldsEmpty(inputNodes, country)) return;
        // Validate postalCode
        if (!Validations.validateIntField(postalCode, "Postal Code")) return;
        // If all validations pass, proceed with object creation
        Branch b = new Branch();
        b.setCountry(country.getText());
        b.setState(state.getText());
        b.setMunicipality(municipality.getText());
        b.setNeighborhood(neighborhood.getText());
        b.setPostalCode(Integer.parseInt(postalCode.getText()));
        b.setAddressDetail(addressDetail.getText());
        // If all validations pass, insert into DB
        BranchDao branchDao = new BranchDao();
        if (branchDao.create(b)) {
            goHome(event);
            Alerts.showAlert(Alert.AlertType.INFORMATION, country, "Registration successful!", "Branch registration was successful.");
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, country, "Error!", "Branch registration could not be performed.");
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        SuperAdministrator.goHome(country);
    }

    @FXML
    private void goInfoAccount(ActionEvent event) {
        SuperAdministrator.goInfoAccount(country);
    }
}
