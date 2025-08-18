package mx.edu.utez.warehousemanagerfx.utils;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class Validations {
    /**
     * Validate form fields and display a single alert if any are empty.
     *
     * @return true if any field is empty, false otherwise.
     */
    public static boolean isInputFieldsEmpty(List<Node> fieldsToValidate, TextField txt) {
        List<Node> invalidFields = new ArrayList<>();
        for (Node node : fieldsToValidate) {
            node.getStyleClass().remove("input-error");
        }
        for (Node node : fieldsToValidate) {
            boolean isValid = true;
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                if (textField.getText().isEmpty()) {
                    isValid = false;
                }
            } else if (node instanceof ChoiceBox) {
                ChoiceBox<String> choiceBox = (ChoiceBox<String>) node;
                // Check if the value is null OR the default string from FXML
                if (choiceBox.getValue() == null || "Transaction Type".equals(choiceBox.getValue())) {
                    isValid = false;
                }
            }
            if (!isValid) {
                node.getStyleClass().add("input-error");
                invalidFields.add(node);
            }
        }
        if (!invalidFields.isEmpty()) {
            String message;
            if (invalidFields.size() == 1) {
                message = "This field cannot be empty.";
            } else {
                message = "These fields cannot be empty.";
            }
            Alerts.showAlert(Alert.AlertType.WARNING, txt, "Error!", message);
            return true;
        }
        return false;
    }

    public static boolean validateDoubleField(TextField txt, String fieldName) {
        try {
            Double.parseDouble(txt.getText());
            return true; // The field contains a valid double
        } catch (NumberFormatException e) {
            // Handle invalid number input
            txt.getStyleClass().add("input-error");
            Alerts.showAlert(Alert.AlertType.ERROR, txt, "Invalid Input", fieldName + " must be a valid number (e.g., 10.5).");
            return false; // The field does not contain a valid double
        }
    }
}
