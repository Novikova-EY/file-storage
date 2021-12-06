package ru.gb.storage.server.auth;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * База данных логин - пароль
 */
public class ClientsBD {
    private static final Logger logger = LogManager.getLogger(ClientsBD.class);

    private void createTable() {
        try {
            JDBCconnection.getStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS clients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "login TEXT UNIQUE, " +
                            "password TEXT NOT NULL, " +
                            ");");
            logger.info("BD: table clients create");
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
    }

    public void createNewClient(String login, String password) {
        //TODO проверить существует ли таблица
        createTable();
        insert(login, password);
    }

    private void insert(String login, String password) {
        try (final PreparedStatement preparedStatement = JDBCconnection.getConnection().prepareStatement(
                "INSERT OR IGNORE INTO clients (login, password) " +
                        "VALUES (?, ?);")) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
    }

    public static boolean checkLogin(String login) {
        try (final PreparedStatement preparedStatement =
                     JDBCconnection.getConnection().prepareStatement(
                             "SELECT login " +
                                     "FROM clients " +
                                     "WHERE login = ?;")) {
            preparedStatement.setString(1, login);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
        return false;
    }

    public static boolean checkLoginPassword(String login, String password) {
        try (final PreparedStatement preparedStatement =
                     JDBCconnection.getConnection().prepareStatement(
                             "SELECT nickname " +
                                     "FROM clients " +
                                     "WHERE login = ? AND password = ?;")) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.throwing(Level.ERROR, e);
        }
        return false;
    }


}
