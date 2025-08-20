package mx.edu.utez.warehousemanagerfx.controllers.edits;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.Main;
import mx.edu.utez.warehousemanagerfx.controllers.subviews.AdminTableController;
import mx.edu.utez.warehousemanagerfx.controllers.subviews.BranchTableController;
import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Branch;
import mx.edu.utez.warehousemanagerfx.models.SuperAdministrator;
import mx.edu.utez.warehousemanagerfx.models.dao.AdminDao;
import mx.edu.utez.warehousemanagerfx.models.dao.BranchDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.Validations;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminEditController implements Initializable {
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
    @FXML
    private ChoiceBox<String> assignedBranch;
    @FXML
    private Button edit;

    // List of all input nodes that can have an "input-error" style and that have to be validated
    private List<Node> inputNodes;
    private Administrator a;
    private AdminTableController parentController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        // Detect changes in the edit button to edit and update.
        edit.setOnAction(e -> {
            if (edit.getText().equals("Edit")) {
                editAdmin();
            } else {
                updateAdmin();
            }
        });
    }

    public void setAdmin(Administrator a) {
        this.a = a;
        firstName.setText(a.getFirstName());
        middleName.setText(a.getMiddleName());
        lastName.setText(a.getLastName());
        secondLastName.setText(a.getSecondLastName());
        email.setText(a.getEmail());
        phone.setText(a.getPhone());
        username.setText(a.getUsername());

        BranchDao branchDao = new BranchDao();
        List<Branch> availableBranches = branchDao.readBranchesWithoutAdmin();

        // Clear previous items
        assignedBranch.getItems().clear();

        // Add option to unassign
        assignedBranch.getItems().add("Unassigned");

        // If already assigned, add it first
        if (a.getIdBranch() != null) {
            Optional<Branch> b = branchDao.readById(a.getIdBranch());
            b.ifPresent(branch -> {
                assignedBranch.getItems().add(branch.getBranchCode());
                assignedBranch.setValue(branch.getBranchCode());
            });
        } else {
            assignedBranch.setValue("Unassigned");
        }

        // Add all available without admin
        for (Branch branch : availableBranches) {
            if (a.getIdBranch() == null || branch.getIdBranch() != a.getIdBranch()) {
                assignedBranch.getItems().add(branch.getBranchCode());
            }
        }
    }

    public void setParentController(AdminTableController parentController) {
        this.parentController = parentController;
    }

    public void editAdmin(){
        firstName.setEditable(true);
        middleName.setEditable(true);
        lastName.setEditable(true);
        secondLastName.setEditable(true);
        username.setEditable(true);
        phone.setEditable(true);
        email.setDisable(true);
        assignedBranch.setDisable(false);
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
        if (Validations.isInputFieldsEmpty(inputNodes, firstName)) return;
        // Validate phone
        if (!Validations.validatePhoneField(phone)) return;
        // Validate duplicates before inserting
        if (Validations.checkDuplicateAdmin(email, phone, username, a.getIdAdmin())) return;
        // If all validations pass, proceed with object creation
        String firstNameA = firstName.getText();
        String middleNameA = middleName.getText();
        String lastNameA = lastName.getText();
        String secondLastNameA = secondLastName.getText();
        String usernameA = username.getText();
        String phoneA = phone.getText();
        String emailA = email.getText();

        // Put new things on the object
        AdminDao dao = new AdminDao();
        a.setFirstName(firstNameA);
        a.setMiddleName(middleNameA);
        a.setLastName(lastNameA);
        a.setSecondLastName(secondLastNameA);
        a.setUsername(usernameA);
        a.setPhone(phoneA);
        a.setEmail(emailA);

        String selectedBranchCode = assignedBranch.getValue();
        if (selectedBranchCode != null && !selectedBranchCode.equals("Unassigned")) {
            BranchDao branchDao = new BranchDao();
            List<Branch> branches = branchDao.readFilteredBranches(null, null);
            for (Branch b : branches) {
                if (b.getBranchCode().equals(selectedBranchCode)) {
                    a.setIdBranch(b.getIdBranch()); // assign branch
                    break;
                }
            }
        } else {
            a.setIdBranch(null); // unassign branch
        }

        firstName.setEditable(false);
        middleName.setEditable(false);
        lastName.setEditable(false);
        secondLastName.setEditable(false);
        username.setEditable(false);
        phone.setEditable(false);
        email.setEditable(false);
        assignedBranch.setDisable(true);
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
        if(dao.update(a)){
            Alerts.showAlert(Alert.AlertType.INFORMATION, firstName, "Successful update!", "The branch was updated successfully.");
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, firstName, "Error!", "The branch could not be updated.");
        }
    }

    @FXML
    private void deleteAdmin(ActionEvent event) {
        AdminDao dao = new AdminDao();
        if (Alerts.confirmDelete(firstName, "admin")) {
            if (dao.softDelete(a.getIdAdmin())) {
                parentController.removeDeletedAdmin(a);
                Stage stage = (Stage) firstName.getScene().getWindow();
                stage.close();
                Alerts.showAlert(Alert.AlertType.INFORMATION, firstName, "Successful deletion!", "The admin was successfully deleted.");
            } else {
                Alerts.showAlert(Alert.AlertType.ERROR, firstName, "Error!", "The admin could not be deleted.");
            }
        }
    }
}
