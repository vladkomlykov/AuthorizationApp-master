module com.example.authorizationapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires jbcrypt;


    opens com.example.authorizationapp to javafx.fxml;
    exports com.example.authorizationapp;
    exports com.example.authorizationapp.controllers;
    opens com.example.authorizationapp.controllers to javafx.fxml;
}