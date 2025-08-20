package mx.edu.utez.warehousemanagerfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.Main;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;
import mx.edu.utez.warehousemanagerfx.utils.routes.ImageRoutes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class WarehouseController {

    public Button moreButton;
    @FXML
    private ImageView img;

    @FXML
    private Rectangle imgBorder;

    @FXML
    private Label name;

    @FXML
    private Label price;

    @FXML
    private Label size;

    @FXML
    private Label status;

    private Warehouse w;

    public void setData(Warehouse warehouse) {
        // Load and set the image
        w = warehouse;

        Image source;

        if (w.getImage() == null || w.getImage().isEmpty()) {
            // Imagen por defecto
            source = new Image(Objects.requireNonNull(
                    Main.class.getResourceAsStream(ImageRoutes.THUMBNAILS_BASE + "ImageNotAvailable.jpg")
            ));
        } else {
            // 1. Buscar en carpeta externa
            File externalImage = new File("images/thumbnails/" + w.getImage());
            if (externalImage.exists()) {
                source = new Image(externalImage.toURI().toString());
            } else {
                // 2. Buscar en resources (placeholders)
                InputStream resourceImage = Main.class.getResourceAsStream(ImageRoutes.THUMBNAILS_BASE + w.getImage());
                if (resourceImage != null) {
                    source = new Image(resourceImage);
                } else {
                    // 3. Imagen por defecto
                    source = new Image(Objects.requireNonNull(
                            Main.class.getResourceAsStream(ImageRoutes.THUMBNAILS_BASE + "ImageNotAvailable.jpg")
                    ));
                    System.err.println("Image not found in external folder or resources: " + w.getImage());
                }
            }
        }

        img.setImage(source);
        imgBorder.setHeight(img.getFitHeight());
        imgBorder.setWidth(img.getFitWidth());
        // Format numbers with commas as a thousand separators
        DecimalFormat formatter = new DecimalFormat("#,###.00", DecimalFormatSymbols.getInstance(Locale.US));
        // Set warehouse information in the labels
        name.setText("Name: " + warehouse.getWarehouseName());
        price.setText("Price: $" + formatter.format(warehouse.getSalePrice()) + " / $" + formatter.format(warehouse.getRentalPrice()) + " * Month");
        size.setText("Size: " + formatter.format(warehouse.getSizeSqMeters()) + " m²");
        status.setText(warehouse.getStatus());
    }

    @FXML
    private void showDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(FXMLRoutes.WAREHOUSE_DETAILS));
            Parent warehouseDetailsWindow = loader.load();
            WarehouseDetailsController controller = loader.getController();
            controller.setWarehouse(w);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(warehouseDetailsWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, null, "Error!", "Could not load the warehouse details window.");
        }
    }
}
