package mx.edu.utez.warehousemanagerfx.utils;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import mx.edu.utez.warehousemanagerfx.models.dao.AdminDao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

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

    public static boolean validateIntField(TextField txt, String fieldName) {
        try {
            Integer.parseInt(txt.getText());
            return true; // The field contains a valid double
        } catch (NumberFormatException e) {
            // Handle invalid number input
            txt.getStyleClass().add("input-error");
            Alerts.showAlert(Alert.AlertType.ERROR, txt, "Invalid Input", fieldName + " must be a valid integer number (e.g., 63589).");
            return false; // The field does not contain a valid double
        }
    }

    /**
     * Validates that the phone field only contains numbers, hyphens, and spaces.
     * Valid examples: "55-1234-5678", "5512345678", "55 1234 5678"
     *
     * @param txt TextField of the phone
     * @return true if the format is correct, false if it is incorrect
     */
    public static boolean validatePhoneField(TextField txt) {
        // Regex: only digits, spaces or hyphens
        String phonePattern = "^[0-9\\-\\s]+$";

        if (!txt.getText().matches(phonePattern)) {
            txt.getStyleClass().add("input-error");
            Alerts.showAlert(javafx.scene.control.Alert.AlertType.ERROR, txt,
                    "Invalid phone number", "The phone number can only contain numbers, hyphens, and spaces.");
            return false;
        }

        return true;
    }

    /**
     * Limits the input of a TextField to a maximum number of characters.
     *
     * @param textField  the TextField to limit
     * @param maxLength  maximum number of characters allowed
     */
    public static void setMaxLength(TextField textField, int maxLength) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.length() <= maxLength) {
                return change;
            } else {
                return null; // Reject change if it exceeds max length
            }
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    // Method to validate duplicate email, phone and user
    public static boolean checkDuplicateAdmin(TextField emailField, TextField phoneField, TextField usernameField) {
        AdminDao adminDao = new AdminDao();
        var dup = adminDao.checkDuplicate(emailField.getText(), phoneField.getText(), usernameField.getText());

        boolean hasDuplicates = false;
        StringBuilder errorMsg = new StringBuilder("The following data already exists in the system:\n");

        // Clear previous styles
        emailField.getStyleClass().remove("input-error");
        phoneField.getStyleClass().remove("input-error");
        usernameField.getStyleClass().remove("input-error");

        if (dup.isEmailExists()) {
            errorMsg.append("- Email\n");
            emailField.getStyleClass().add("input-error");
            hasDuplicates = true;
        }
        if (dup.isPhoneExists()) {
            errorMsg.append("- Phone\n");
            phoneField.getStyleClass().add("input-error");
            hasDuplicates = true;
        }
        if (dup.isUsernameExists()) {
            errorMsg.append("- User\n");
            usernameField.getStyleClass().add("input-error");
            hasDuplicates = true;
        }

        if (hasDuplicates) {
            Alerts.showAlert(javafx.scene.control.Alert.AlertType.ERROR, emailField, "Duplicate error", errorMsg.toString());
        }

        return hasDuplicates;
    }
}
