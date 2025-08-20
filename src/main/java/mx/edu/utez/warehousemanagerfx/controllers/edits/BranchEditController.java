package mx.edu.utez.warehousemanagerfx.controllers.edits;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.Main;
import mx.edu.utez.warehousemanagerfx.controllers.subviews.BranchTableController;
import mx.edu.utez.warehousemanagerfx.models.*;
import mx.edu.utez.warehousemanagerfx.models.dao.*;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.Validations;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BranchEditController implements Initializable {
    @FXML
    private TextField branchCode;
    @FXML
    private TextField registrationDate;
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
    @FXML
    private ChoiceBox<String> assignedAdmin;
    @FXML
    private Button edit;

    // List of all input nodes that can have an "input-error" style and that have to be validated
    private List<Node> inputNodes;
    private Branch b;
    private BranchTableController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        // Detect changes in the edit button to edit and update.
        edit.setOnAction(e -> {
            if (edit.getText().equals("Edit")) {
                editAdmin();
            } else {
                updateAdmin();
            }
        });

        // 🔹 Fill assignedAdmin ChoiceBox with active admins
        loadAdmins();
    }

    public void setBranch(Branch b) {
        this.b = b;
        branchCode.setText(b.getBranchCode());
        registrationDate.setText(String.valueOf(b.getRegistrationDate()));
        country.setText(b.getCountry());
        state.setText(b.getState());
        municipality.setText(b.getMunicipality());
        postalCode.setText(String.valueOf(b.getPostalCode()));
        neighborhood.setText(b.getNeighborhood());
        addressDetail.setText(b.getAddressDetail());

        // 🔹 Reload valid admins (free + the one from this branch)
        loadAdmins();

        // 🔹 Select the current branch admin
        if (b.getIdAdmin() != null) {
            AdminDao adminDao = new AdminDao();
            Optional<Administrator> a = adminDao.readById(b.getIdAdmin());
            a.ifPresent(admin -> {
                String fullName = admin.getFirstName() + " " +
                        (admin.getMiddleName() != null ? admin.getMiddleName() + " " : "") +
                        admin.getLastName() + " " +
                        (admin.getSecondLastName() != null ? admin.getSecondLastName() : "");
                assignedAdmin.setValue(fullName.trim());
            });
        } else {
            assignedAdmin.setValue("Unassigned");
        }
    }

    public void setParentController(BranchTableController parentController) {
        this.parentController = parentController;
    }

    private void loadAdmins() {
        AdminDao adminDao = new AdminDao();
        List<Administrator> admins = adminDao.readAssignableAdmins(b != null ? b.getIdBranch() : null);

        // Add Unassigned option
        assignedAdmin.getItems().clear();
        assignedAdmin.getItems().add("Unassigned");

        // Add admins with full name
        for (Administrator a : admins) {
            String fullName = a.getFirstName() + " " +
                    (a.getMiddleName() != null ? a.getMiddleName() + " " : "") +
                    a.getLastName() + " " +
                    (a.getSecondLastName() != null ? a.getSecondLastName() : "");
            assignedAdmin.getItems().add(fullName.trim());
        }
    }

    public void editAdmin(){
        country.setEditable(true);
        state.setEditable(true);
        municipality.setEditable(true);
        postalCode.setEditable(true);
        neighborhood.setEditable(true);
        addressDetail.setEditable(true);
        assignedAdmin.setDisable(false);
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

    private void updateAdmin() {
        // Validate if any fields are empty
        if (Validations.isInputFieldsEmpty(inputNodes, branchCode)) return;
        // Validate postalCode
        if (!Validations.validateIntField(postalCode, "Postal Code")) return;
        // If all validations pass, proceed with object creation
        String countryB = country.getText();
        String stateB = state.getText();
        String municipalityB = municipality.getText();
        String neighborhoodb = neighborhood.getText();
        int postalCodeB = Integer.parseInt(postalCode.getText());
        String addressDetailB = addressDetail.getText();

        // Put new things on the object
        BranchDao dao = new BranchDao();
        b.setCountry(countryB);
        b.setState(stateB);
        b.setMunicipality(municipalityB);
        b.setNeighborhood(neighborhoodb);
        b.setPostalCode(postalCodeB);
        b.setAddressDetail(addressDetailB);

        // 🔹 Update assigned Admin
        AdminDao adminDao = new AdminDao();
        String selected = (String) assignedAdmin.getValue();
        if (selected != null) {
            if (selected.equals("Unassigned")) {
                // If deallocated → id_branch = null
                if (b.getIdAdmin() != null) {
                    adminDao.readById(b.getIdAdmin()).ifPresent(admin -> {
                        admin.setIdBranch(null);
                        adminDao.update(admin);
                    });
                }
                b.setIdAdmin(null);
            } else {
                // Find admin corresponding to the selected name
                List<Administrator> admins = adminDao.readFilteredAdmins("Name", "ASC");
                for (Administrator admin : admins) {
                    String fullName = admin.getFirstName() + " " +
                            (admin.getMiddleName() != null ? admin.getMiddleName() + " " : "") +
                            admin.getLastName() + " " +
                            (admin.getSecondLastName() != null ? admin.getSecondLastName() : "");
                    if (fullName.trim().equals(selected)) {
                        admin.setIdBranch(b.getIdBranch()); // assign to the current branch
                        adminDao.update(admin);
                        b.setIdAdmin(admin.getIdAdmin());
                        break;
                    }
                }
            }
        }

        country.setEditable(false);
        state.setEditable(false);
        municipality.setEditable(false);
        neighborhood.setEditable(false);
        postalCode.setEditable(false);
        addressDetail.setEditable(false);
        assignedAdmin.setDisable(true);
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
        if(dao.update(b)){
            Alerts.showAlert(Alert.AlertType.INFORMATION, branchCode, "Successful update!", "The branch was updated successfully.");
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, branchCode, "Error!", "The branch could not be updated.");
        }
    }

    @FXML
    private void deleteBranch(ActionEvent event) {
        BranchDao dao = new BranchDao();
        if (Alerts.confirmDelete(country, "branch")) {
            if (dao.softDelete(b.getIdBranch())) {
                parentController.removeDeletedBranch(b);
                Stage stage = (Stage) branchCode.getScene().getWindow();
                stage.close();
                Alerts.showAlert(Alert.AlertType.INFORMATION, branchCode, "Successful deletion!", "The branch was successfully deleted.");
            } else {
                Alerts.showAlert(Alert.AlertType.ERROR, branchCode, "Error!", "The branch could not be deleted.");
            }
        }
    }
}
