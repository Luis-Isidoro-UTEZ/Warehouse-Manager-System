module mx.edu.utez.warehousemanagerfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires ucp;


    opens mx.edu.utez.warehousemanagerfx to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx;
    opens mx.edu.utez.warehousemanagerfx.models to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.models;
}