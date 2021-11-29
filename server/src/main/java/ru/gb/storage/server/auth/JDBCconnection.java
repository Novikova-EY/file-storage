package ru.gb.storage.server.auth;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Работа с БД логин-пароль */
public class JDBCconnection {

    private static final Logger logger = LogManager.getLogger(JDBCconnection.class);

    private static Connection connection;
    private static Statement statement;

    // создание соединения с БД логин-пароль
    public JDBCconnection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:auth_db.db");
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
    }

    // создание statement соединения с БД логин-пароль
    public static void createStatement() {
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
    }

    // разрыв соединения с БД логин-пароль
    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
            logger.info("SERVER: Connection to DB: close");
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Statement getStatement() {
        return statement;
    }
}
