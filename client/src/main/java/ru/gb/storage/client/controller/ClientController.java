package ru.gb.storage.client.controller;

//* Основное диалоговое окно */

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.gb.storage.commons.message.request.auth.AuthMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController extends FxController implements Initializable {

    private AuthMessage authMessage = new AuthMessage();

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

    }

    // getter кнопки "Регистрация нового пользователя"
    public Button getButtonRegistration() {
        return buttonRegistration;
    }


    // действие при нажатии на кнопку "Ок" при заполнении полей "Логин" и "Пароль" существующего пользователя
//    public void getButtonAuthorization(ActionEvent actionEvent) {
//        authMessage.setLogin(loginField.getText());
//        authMessage.setPassword(passwordField.getText());
//        System.out.println("authMessage.getPassword() = " + authMessage.getPassword());
//        System.out.println("authMessage.getLogin() = " + authMessage.getLogin());
//        loginField.clear();
//        passwordField.clear();
//    }

    public Button getButtonAuthorization() {
        return buttonAuthication;
    }

    public AuthMessage getAuthMessage() {
        return authMessage;
    }

    public TextField getLoginField() {
        return loginField;
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }
}
