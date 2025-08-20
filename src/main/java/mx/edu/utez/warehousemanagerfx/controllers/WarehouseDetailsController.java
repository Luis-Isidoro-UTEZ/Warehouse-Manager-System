package mx.edu.utez.warehousemanagerfx.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.edu.utez.warehousemanagerfx.Main;
import mx.edu.utez.warehousemanagerfx.controllers.edits.ClientTransactionEditController;
import mx.edu.utez.warehousemanagerfx.controllers.registers.ClientRegisterController;
import mx.edu.utez.warehousemanagerfx.models.Administrator;
import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.models.dao.WarehouseDao;
import mx.edu.utez.warehousemanagerfx.utils.Alerts;
import mx.edu.utez.warehousemanagerfx.utils.Validations;
import mx.edu.utez.warehousemanagerfx.utils.routes.FXMLRoutes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
    private ImageView img; // To preview the selected image
    @FXML
    private TextField image;
    @FXML
    public Button chooseImage;
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
    private Button action;
    @FXML
    private TextArea details;

    private String selectedImagePath; // Temporary route to upload

    private final ObservableList<String> statusChoiceList =
            FXCollections.observableArrayList("Available", "Rent Only", "Sale Only");
    private boolean isRentedOrSold;
    private Warehouse w;
    // List of all input nodes that can have an "input-error" style and that have to be validated
    private List<Node> inputNodes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputNodes = List.of(name, rentalPrice, salePrice, size);
        // Add a focused property listener to each node to remove the error style
        for (Node node : inputNodes) {
            node.focusedProperty().addListener((obs, oldVal, newVal) -> {
                // Remove the error style when the node gains focus
                if (newVal) {
                    node.getStyleClass().remove("input-error");
                }
            });
        }
        // Detect changes in the edit button to edit and update.
        edit.setOnAction(e -> {
            if (edit.getText().equals("Edit")) {
                editWarehouse();
            } else {
                updateWarehouse();
            }
        });
        // Detect changes in the status choice box
        status.getSelectionModel().selectedItemProperty()
                .addListener((o, a, b) -> {
                    if(b.equals("Rented") || b.equals("Sold")) {
                        action.setText("Show Client");
                    } else {
                        action.setText("Rent/Sell");
                    }
                });

        // Detect changes in the edit button to edit and update.
        action.setOnAction(e -> {
            if (status.getValue().equals("Available")) {
                rentOrSell();
            } else {
                action.setText("Show Client");
                goToClientAssignation();
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
                System.err.println("Image not found: " + w.getImage());
            } else {
                source = new Image(path);
            }
        }

        img.setImage(source);

        if (w.getStatus().equals("Rented") || w.getStatus().equals("Sold")) {
            isRentedOrSold = true;
            status.getItems().add(w.getStatus()); // solo agrega Rented o Sold
        } else {
            status.getItems().addAll(statusChoiceList);
        }
    }

    @FXML
    public void uploadImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de imagen", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(chooseImage.getScene().getWindow());

        if (selectedFile != null) {
            // We only save the name + extension
            String fileName = selectedFile.getName();
            image.setText(fileName);
            w.setImage(fileName);
            Image selectedImageFile = new Image(selectedFile.toURI().toString());
            img.setImage(selectedImageFile);

            // Copy the image to the thumbnails directory inside resources
            Path destination = Paths.get("src/main/resources/mx/edu/utez/warehousemanagerfx/img/thumbnails/" + fileName);
            try {
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void editWarehouse(){
        name.setEditable(true);
        chooseImage.setDisable(false);
        rentalPrice.setEditable(true);
        salePrice.setEditable(true);
        size.setEditable(true);
        status.setDisable(isRentedOrSold);
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
        // 1. Validate if any fields are empty
        if (Validations.isInputFieldsEmpty(inputNodes, name)) return;
        // 2. Validate numerical fields
        if (!Validations.validateDoubleField(rentalPrice, "Rental Price")) {
            return;
        }
        if (!Validations.validateDoubleField(salePrice, "Sale Price")) {
            return;
        }
        if (!Validations.validateDoubleField(size, "Size")) {
            return;
        }
        // If all validations pass, proceed with object creation
        String nameW = name.getText();
        String imageW = image.getText();
        double rentalPriceV = Double.parseDouble(rentalPrice.getText());
        double salePriceV = Double.parseDouble(salePrice.getText());
        double sizeV = Double.parseDouble(size.getText());
        String statusV = status.getValue();

        // Put new things on the object
        w.setWarehouseName(nameW);
        w.setImage(imageW);
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
        status.setDisable(!isRentedOrSold);
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
            Alerts.showAlert(Alert.AlertType.INFORMATION, name, "Successful update!", "The warehouse was updated successfully.");
        } else {
            Alerts.showAlert(Alert.AlertType.ERROR, name, "Error!", "The warehouse could not be updated.");
        }
    }

    @FXML
    private void deleteWarehouse(ActionEvent event) {
        WarehouseDao dao = new WarehouseDao();
        if (w.getStatus().equals("Rented")) {
            Alerts.showAlert(Alert.AlertType.ERROR, name, "Error!", "The warehouse is rented, so it cannot be deleted.");
        } else {
            if (Alerts.confirmDelete(name, "warehouse")) {
                if (dao.softDeleteWarehouse(w.getIdWarehouse())) {
                    goHomeAD(event);
                    Alerts.showAlert(Alert.AlertType.INFORMATION, name, "Successful deletion!", "The warehouse was successfully deleted.");
                } else {
                    Alerts.showAlert(Alert.AlertType.ERROR, name, "Error!", "The warehouse could not be deleted.");
                }
            }
        }
    }

    private void rentOrSell() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(FXMLRoutes.CLIENT_REGISTER));
            Parent clientRegisterWindow = loader.load();
            ClientRegisterController controller = loader.getController();
            controller.setCurrentWarehouse(w);
            Stage stage = (Stage) warehouseCode.getScene().getWindow();
            Scene scene = new Scene(clientRegisterWindow);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, name, "Error!", "Could not load the client register window to rent/sell the warehouse.");
        }
    }

    private void goToClientAssignation() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(FXMLRoutes.CLIENT_ASSIGNATION));
            Parent clientTransactionEditWindow = loader.load();
            ClientTransactionEditController controller = loader.getController();
            controller.setCurrentClientAssignation(w);
            Stage stage = (Stage) warehouseCode.getScene().getWindow();
            Scene scene = new Scene(clientTransactionEditWindow);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showAlert(Alert.AlertType.ERROR, name, "Error!", "Could not load the client register window to rent/sell the warehouse.");
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
