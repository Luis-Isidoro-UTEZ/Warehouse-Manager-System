package mx.edu.utez.warehousemanagerfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.utils.FontLoader; // Importa el FontLoader

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /*// 1. Cargar fuentes ANTES de crear la escena
        FontLoader.loadAlbertSansFonts();

        // 2. Verificar en consola que se cargaron
        FontLoader.listAvailableFonts(); // Debugging*/

        // 3. Cargar el FXML y la escena
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Warehouse Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}