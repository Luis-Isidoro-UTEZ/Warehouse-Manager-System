package mx.edu.utez.warehousemanagerfx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.Main;
import mx.edu.utez.warehousemanagerfx.controllers.registers.ClientRegisterController;
import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import javax.swing.*;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class WarehouseDetailsController implements Initializable {
    @FXML
    private TextField warehouseCode;
    @FXML
    private TextField registrationDate;
    @FXML
    private TextField name;
    @FXML
    private ImageView img;
    @FXML
    private TextField image;
    @FXML
    private TextField rentalPrice;
    @FXML
    private TextField salePrice;
    @FXML
    private TextField size;
    @FXML
    private ChoiceBox<String> status;
    @FXML
    private Button edit;
    @FXML
    private TextArea details;

    private final String[] statusChoiceList = {"Available", "Rented", "Sold", "Rent Only", "Sale Only"};
    private Warehouse w;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure the initial Status Choice Box
        status.getItems().addAll(statusChoiceList);

        // Detect changes in the edit button to edit and update.
        edit.setOnAction(e -> {
            if (edit.getText().equals("Edit")) {
                editWarehouse();
            } else {
                updateWarehouse();
            }
        });
    }

    public void setWarehouse(Warehouse w) {
        this.w = w;

        //1: obtener lo del form
        warehouseCode.setText(w.getWarehouseCode());
        registrationDate.setText(String.valueOf(w.getRegistrationDate()));
        name.setText(w.getWarehouseName());
        image.setText(w.getImage());
        rentalPrice.setText(String.valueOf(w.getRentalPrice()));
        salePrice.setText(String.valueOf(w.getSalePrice()));
        size.setText(String.valueOf(w.getSizeSqMeters()));
        status.setValue(w.getStatus());

        Image source;

        if (w.getImage() == null || w.getImage().isEmpty()) {
            source = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("img/thumbnails/ImageNotAvailable.jpg")));
        } else {
            InputStream path = Main.class.getResourceAsStream("img/thumbnails/" + w.getImage());
            if (path == null) {
                // Image not found, use default image
                source = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("img/thumbnails/ImageNotAvailable.jpg")));
                System.err.println("Imagen no encontrada: " + w.getImage());
            } else {
                source = new Image(path);
            }
        }

        img.setImage(source);
    }

    public void editWarehouse(){
        name.setEditable(true);
        image.setEditable(true);
        rentalPrice.setEditable(true);
        salePrice.setEditable(true);
        size.setEditable(true);
        status.setDisable(false);
        details.setEditable(true);
        edit.setText("Update");
        edit.setStyle(
                "-fx-background-color: darkgreen;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-family: 'Albert Sans';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;"
        );
    }

    private void updateWarehouse() {
        String nameW = name.getText();
        String imageV = image.getText();
        double rentalPriceV = Double.parseDouble(rentalPrice.getText());
        double salePriceV = Double.parseDouble(salePrice.getText());
        double sizeV = Double.parseDouble(size.getText());
        String statusV = status.getValue();

        // Put new things on the object
        w.setWarehouseName(nameW);
        w.setImage(imageV);
        w.setRentalPrice(rentalPriceV);
        w.setSalePrice(salePriceV);
        w.setSizeSqMeters(sizeV);
        w.setStatus(statusV);
        WarehouseDao dao = new WarehouseDao();

        name.setEditable(false);
        image.setEditable(false);
        rentalPrice.setEditable(false);
        salePrice.setEditable(false);
        size.setEditable(false);
        status.setDisable(true);
        details.setEditable(false);
        edit.setText("Edit");
        edit.setStyle(
                "-fx-background-color: lightgreen;" +
                        "-fx-text-fill: black;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-family: 'Albert Sans';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;"
        );

        // Update the DB
        if(dao.updateWarehouse(w)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            try { alert.initOwner(name.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
            alert.setTitle("Successful update!"); alert.setHeaderText(null); alert.setContentText("The warehouse was updated successfully."); alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            try { alert.initOwner(name.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
            alert.setTitle("Error!"); alert.setHeaderText(null); alert.setContentText("The warehouse was not updated."); alert.show();
        }
    }

    @FXML
    private void deleteWarehouse(ActionEvent event) {
        WarehouseDao dao = new WarehouseDao();
        if (confirmDelete()) {
            if (dao.softDeleteWarehouse(w.getIdWarehouse())) {
                goHomeAD(event);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                try { alert.initOwner(name.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
                alert.setTitle("Successful removal!"); alert.setHeaderText(null); alert.setContentText("The warehouse was successfully deleted."); alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                try { alert.initOwner(name.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
                alert.setTitle("Error!"); alert.setHeaderText(null); alert.setContentText("The warehouse was not deleted."); alert.show();
            }
        }
    }

    private boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        try { alert.initOwner(name.getScene().getWindow()); alert.initModality(Modality.WINDOW_MODAL); } catch (Exception ignore) {}
        alert.setTitle("Confirm deletion"); alert.setHeaderText("Hello"); alert.setContentText("Are you sure you want to delete that record?");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    @FXML
    private void rentOrSell(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(FXMLRoutes.CLIENT_REGISTER));
            Parent clientRegisterWindow = loader.load();
            ClientRegisterController controller = loader.getController();
            controller.setCurrentWarehouse(w);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene escena = new Scene(clientRegisterWindow);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, null, "Error!", "Could not load the client register window to rent/sell the warehouse.");
        }
    }

    @FXML
    private void goHomeAD(ActionEvent event) {
        Administrator.goHome(event, name);
    }

    @FXML
    private void goAccount(ActionEvent event) {
        Administrator.goInfoAccount(event, name);
    }
}
