package mx.edu.utez.warehousemanagerfx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;

public class WarehouseController {

    @FXML
    private ImageView img;

    @FXML
    private Label name;

    @FXML
    private Label price;

    @FXML
    private Label size;

    @FXML
    private Button moreButton;

    public void setData(Warehouse warehouse) {
        Image image = new Image(getClass().getResourceAsStream(warehouse.getImgSrc()));
        img.setImage(image);
        name.setText("Name: " + warehouse.getName());
        price.setText("Price: $" + warehouse.getPrice());
        size.setText("Size: " + warehouse.getSize() + "m2");
    }

}
