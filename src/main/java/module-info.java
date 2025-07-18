module mx.edu.utez.warehousemanagerfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens mx.edu.utez.warehousemanagerfx to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx;
}