package mx.edu.utez.warehousemanagerfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.util.Objects;

public class LoginController {
    // Define los elementos de la ventana como ATT
    @FXML
    private TextField user;
    @FXML
    private PasswordField password;

    @FXML
    private void login(ActionEvent event){
        // Verificar que exista el usuario en la BD.
        if (user.getText().equals("SuperAdmin") && password.getText().equals("root")){
            // Que el usuario si existe en la BD.
            // Voy a pasar a la ventana principal.
            try {
                // 1. Necesito cargar mi nueva ventana.
                Parent superAdminIndex = FXMLLoader.load(
                        Objects.requireNonNull(getClass().getResource("SuperAdminWindow.fxml"))
                );

                // 2. Obtener el stage que ya existía previamente.
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // 3. Preparar la nueva escena.
                Scene escena = new Scene(superAdminIndex);

                // 4. Poner en el escenario la nueva escena.
                stage.setScene(escena);

                // 5. Asegurarnos de que se vea.
                stage.show();
                JOptionPane.showMessageDialog(null, "¡Bienvenido al Sistema SuperAdmin!");
            } catch (Exception e) {
                System.out.println("Ocurrió un Error al cargar la escena del Index del Super Admin");
            }
        } else if (user.getText().equals("admin") && password.getText().equals("root")) {
            // Cambiar de pantalla
            try {
                // 1. Necesito cargar mi nueva ventana.
                Parent adminIndex = FXMLLoader.load(
                        Objects.requireNonNull(getClass().getResource("AdminWindow.fxml"))
                );

                // 2. Obtener el stage que ya existía previamente.
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // 3. Preparar la nueva escena.
                Scene escena = new Scene(adminIndex);

                // 4. Poner en el escenario la nueva escena.
                stage.setScene(escena);

                // 5. Asegurarnos de que se vea.
                stage.show();
                JOptionPane.showMessageDialog(null, "¡Bienvenido al Sistema Admin!");
            } catch (Exception e) {
                System.out.println("Ocurrió un Error al cargar la escena del Index del Admin");
            }
        }
    }
}
