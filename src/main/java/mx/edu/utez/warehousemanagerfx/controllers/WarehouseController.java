package mx.edu.utez.warehousemanagerfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;
import mx.edu.utez.warehousemanagerfx.utils.routes.ImageRoutes;

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

    public void setData(Warehouse warehouse) {
        // Load and set the image
        String fullPath = Objects.requireNonNull(getClass().getResource(ImageRoutes.THUMBNAILS_BASE + warehouse.getImage())).toExternalForm();
        Image image = new Image(fullPath, true);
        img.setImage(image);
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
            Parent showData = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLRoutes.WAREHOUSE_DETAILS)));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(showData);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error! Could not load the warehouse details screen.");
        }
    }

}
