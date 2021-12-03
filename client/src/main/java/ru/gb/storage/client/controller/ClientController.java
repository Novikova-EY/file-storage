package ru.gb.storage.client.controller;

//** Основное диалоговое окно */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.client.ConnectionToServer;
import ru.gb.storage.commons.message.request.auth.AuthMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController extends FxController implements Initializable {

    private static final Logger logger = LogManager.getLogger(ClientController.class);
    private ConnectionToServer connectionToServer = new ConnectionToServer();

    @FXML
    private Button buttonRegistration;

    @FXML
    private Button buttonAuthication;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            connectionToServer.run();
            authorization();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void authorization() {
        getButtonAuthorization().setOnMouseClicked(event -> {
            AuthMessage authMessage = new AuthMessage();
            authMessage.setLogin(loginField.getText());
            authMessage.setPassword(passwordField.getText());
            System.out.println("authMessage.getPassword() = " + authMessage.getPassword());
            System.out.println("authMessage.getLogin() = " + authMessage.getLogin());
            loginField.clear();
            passwordField.clear();
        });
    }

    // getter кнопки "Регистрация нового пользователя"
    public Button getButtonRegistration() {
        return buttonRegistration;
    }


    // действие при нажатии на кнопку "Ок" при заполнении полей "Логин" и "Пароль" существующего пользователя
    public Button getButtonAuthorization() {
        return buttonAuthication;

    }

    public TextField getLoginField() {
        return loginField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

}
