package mx.edu.utez.warehousemanagerfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;
import mx.edu.utez.warehousemanagerfx.utils.routes.ImageRoutes;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        /*// 1. Load fonts BEFORE creating the scene
        FontLoader.loadAlbertSansFonts();

        // 2. Check in console that the fonts were loaded successfully
        FontLoader.listAvailableFonts(); // Debugging*/

        // 3. Load the FXML and scene
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(FXMLRoutes.LOGIN));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Warehouse Manager");

        // Change the stage icon
        Image icon = new Image(Objects.requireNonNull(getClass().getResource(ImageRoutes.MAIN_APP_ICON)).toString());
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}