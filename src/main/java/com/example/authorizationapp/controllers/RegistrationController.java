package com.example.authorizationapp.controllers;

import com.example.authorizationapp.PasswordUtils;
import com.example.authorizationapp.SalaryGeneration;
import com.example.authorizationapp.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class RegistrationController {
    public TextField lastnameRegistration;
    public TextField firstnameRegistration;
    public TextField patronymicRegistration;
    public PasswordField passwordRegistration;
    private DatabaseManager databaseManager;
    private String userId;

    public RegistrationController() {
        databaseManager = new DatabaseManager();
    }

    private String generateUserId(Connection connection, String role) throws SQLException {
        String prefix;
        switch (role) {
            case "Менеджер":
                prefix = "M-";
                break;
            case "Администратор":
                prefix = "A-";
                break;
            case "Инженер":
                prefix = "E-";
                break;
            default:
                prefix = "U"; // Префикс для пользователей
                break;
        }
        String query = "SELECT COUNT(*) FROM users WHERE user_id LIKE ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, prefix + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int nextId = resultSet.getInt(1) + 1; // Увеличиваем количество
                    return String.format("%s%06d", prefix, nextId); // Форматируем ID как A000001, U000001 и т.д.
                }
            }
        }
        return String.format("%s%06d", prefix, 1); // Если пользователей нет, возвращаем префикс с 1
    }

    private double setSalary(String role){

        SalaryGeneration salaryGeneration = new SalaryGeneration();
        double salary = 0;
        switch (role) {
            case "Менеджер":
                salary = salaryGeneration.managerSalary();
                break;
            case "Администратор":
                salary = salaryGeneration.administratorSalary();
                break;
            case "Инженер":
                salary = salaryGeneration.engineerSalary();
                break;
        }
        return salary;
    }

    private String setLogin(String lastname, String firstname, String patronymic){

        String[] rus = {".", "а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я"};
        String[] eng = {".", "a", "b", "v", "g", "d", "e", "yo", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "c", "ch", "sh", "", "", "", "e", "yu", "ya"};

        var username = (lastname + "." + firstname.substring(0,1) + "." + patronymic.substring(0,1)).toLowerCase();
        var correctUsername = new StringBuilder();

        for (var letter : username.split("")){
            for (var i = 0; i < rus.length - 1; i++){
                if (letter.equals(rus[i])){
                    correctUsername.append(eng[i]);
                }
            }
        }
        return correctUsername.toString();
    }

    public void onRegistrationClick(ActionEvent actionEvent) {
        String lastname = lastnameRegistration.getText();
        String firstname = firstnameRegistration.getText();
        String patronymic = patronymicRegistration.getText();
        String password = passwordRegistration.getText();
        String role = roleChoiceBox.getValue();
        double salary = setSalary(role);
        String login = setLogin(lastname,firstname,patronymic);

        registerUser(lastname, firstname, patronymic, password, role, salary, login);
    }

    public void registerUser(String lastname, String firstname, String patronymic, String password, String role, double salary, String login) {
        String sql = "INSERT INTO users (user_id, lastname, firstname, patronymic, password, role, salary, login) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;

        try {
            connection = databaseManager.getConnection();

            if (connection == null) {
                System.err.println("Не удалось установить соединение с базой данных.");
                return; // Прекращаем выполнение метода, если соединение не установлено
            }
            String userId = generateUserId(connection, role);
            connection = databaseManager.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql);
            String hashedPassword = PasswordUtils.hashPassword(password);

            pstmt.setString(1, userId);
            pstmt.setString(2, lastname);
            pstmt.setString(3, firstname);
            pstmt.setString(4, patronymic);
            pstmt.setString(5, hashedPassword);
            pstmt.setString(6, role);
            pstmt.setDouble(7,salary);
            pstmt.setString(8,login);


            pstmt.executeUpdate();
            System.out.println("Пользователь добавлен успешно с ID: " + userId);


        } catch (SQLException e) {
            System.err.println("Ошибка регистрации пользователя: " + e.getMessage());
        } finally {
            databaseManager.closeConnection(connection); // Закрытие соединения в блоке finally
        }
    }

    @FXML
    private ChoiceBox<String> roleChoiceBox;

    @FXML
    public void initialize() {
        ObservableList<String> roles = FXCollections.observableArrayList("Администратор", "Инженер", "Менеджер");
        roleChoiceBox.setItems(roles);
    }
}
