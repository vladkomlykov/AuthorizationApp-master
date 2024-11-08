package com.example.authorizationapp.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@Setter
@AllArgsConstructor
public class DatabaseManager {
    private String url;
    private String user;
    private String password;

    // Конструктор по умолчанию с параметрами подключения
    public DatabaseManager() {
        this.url = "jdbc:postgresql://localhost:5432/AuthorizationDb"; // Замените на ваше имя базы данных
        this.user = "postgres"; // Замените на ваше имя пользователя
        this.password = "123"; // Замените на ваш пароль
    }

    // Метод для получения соединения
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к базе данных: " + e.getMessage());
            throw e; // Передаем исключение выше
        }
    }

    // Метод для закрытия соединения
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}
