package mx.edu.utez.warehousemanagerfx.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

public class Alerts {
    public static void showAlert(Alert.AlertType type, TextField txt, String title, String content) {
        Alert alert = new Alert(type);
        try { alert.initOwner(txt.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content); alert.show();
    }
}
