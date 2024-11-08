package com.example.authorizationapp.controllers;

import com.example.authorizationapp.PasswordUtils;
import com.example.authorizationapp.database.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Data;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LoginController {
    private DatabaseManager databaseManager;
    public TextField loginLogin;
    public PasswordField passwordLogin;
    public Label helloLableLogin;

    public LoginController() {
        databaseManager = new DatabaseManager();
    }


    public void onLoginClick(ActionEvent actionEvent) {
        String query = "SELECT firstname, password FROM users WHERE login = ?";
        Connection connection = null;

        try {
            connection = databaseManager.getConnection();

            if (connection == null) {
                System.err.println("Не удалось установить соединение с базой данных.");
                return;
            }

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, loginLogin.getText());

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String firstname = resultSet.getString("firstname");
                    String storedPasswordHash = resultSet.getString("password");


                    if (PasswordUtils.checkPassword(passwordLogin.getText(),storedPasswordHash)) {
                        showGreeting(firstname);  // Пароль совпадает
                    } else {
                        helloLableLogin.setText("Неверный логин или пароль");  // Пароль не совпадает
                    }
                } else {
                    helloLableLogin.setText("Неверный логин или пароль");  // Пользователь не найден
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showGreeting(String firstname) {
        LocalTime localTime = LocalTime.now();
        LocalTime startMorning = LocalTime.of(4, 0);
        LocalTime endMorning = LocalTime.of(12, 0);
        LocalTime startDay = LocalTime.of(13, 0);
        LocalTime endDay = LocalTime.of(17, 0);
        LocalTime startEvening = LocalTime.of(18, 0);
        LocalTime endEvening = LocalTime.of(22, 0);
        LocalTime startNight = LocalTime.of(23, 0);
        LocalTime endNight = LocalTime.of(3, 0);

        if (localTime.isAfter(startMorning) && localTime.isBefore(endMorning)) {
            helloLableLogin.setText("Доброе утро " + firstname + "!");
        } else if (localTime.isAfter(startDay) && localTime.isBefore(endDay)) {
            helloLableLogin.setText("Добрый день " + firstname + "!");
        } else if (localTime.isAfter(startEvening) && localTime.isBefore(endEvening)) {
            helloLableLogin.setText("Добрый вечер " + firstname + "!");
        } else if (localTime.isAfter(startNight) || localTime.isBefore(endNight)) {
            helloLableLogin.setText("Доброй ночи " + firstname + "!");
        }
    }
}

