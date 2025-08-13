module mx.edu.utez.warehousemanagerfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires ucp;
    requires com.jfoenix;
    requires org.controlsfx.controls;
    requires javafx.base;


    opens mx.edu.utez.warehousemanagerfx to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx;
    opens mx.edu.utez.warehousemanagerfx.controllers to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.controllers;
    opens mx.edu.utez.warehousemanagerfx.modelos to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.modelos;
    opens mx.edu.utez.warehousemanagerfx.modelos.dao to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.modelos.dao;
}