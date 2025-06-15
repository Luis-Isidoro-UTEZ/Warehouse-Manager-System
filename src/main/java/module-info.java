module mx.edu.utez.warehousemanagerfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens mx.edu.utez.warehousemanagerfx to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx;
}