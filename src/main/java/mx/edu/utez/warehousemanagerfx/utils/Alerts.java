package mx.edu.utez.warehousemanagerfx.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.util.Optional;

public class Alerts {
    public static void showAlert(Alert.AlertType type, TextField txt, String title, String content) {
        Alert alert = new Alert(type);
        try { alert.initOwner(txt.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.show();
    }

    public static boolean confirmDelete(TextField txt, String entity) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        try { alert.initOwner(txt.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
        alert.setTitle("Confirm deletion"); alert.setHeaderText("Hello"); alert.setContentText("Are you sure you want to delete this " + entity + "?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean confirmUnassign(TextField txt, String entity) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        try { alert.initOwner(txt.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
        alert.setTitle("Confirm Unassign"); alert.setHeaderText("Hello"); alert.setContentText("Are you sure you want to unassign this " + entity + "?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
