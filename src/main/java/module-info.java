module mx.edu.utez.warehousemanagerfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires ucp;
    requires com.jfoenix;
    requires org.controlsfx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires jakarta.mail;

    opens mx.edu.utez.warehousemanagerfx to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx;
    opens mx.edu.utez.warehousemanagerfx.controllers to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.controllers;
    opens mx.edu.utez.warehousemanagerfx.controllers.account to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.controllers.account;
    opens mx.edu.utez.warehousemanagerfx.controllers.edits to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.controllers.edits;
    opens mx.edu.utez.warehousemanagerfx.controllers.registers to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.controllers.registers;
    opens mx.edu.utez.warehousemanagerfx.controllers.subviews to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.controllers.subviews;
    opens mx.edu.utez.warehousemanagerfx.models to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.models;
    opens mx.edu.utez.warehousemanagerfx.models.dao to javafx.fxml;
    exports mx.edu.utez.warehousemanagerfx.models.dao;
}