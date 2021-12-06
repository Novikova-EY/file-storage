package ru.gb.storage.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.gb.storage.commons.message.request.auth.RegistrationMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController extends FxController implements Initializable {

    @FXML
    private TextField registrationLoginField;

    @FXML
    private PasswordField registrationPasswordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    // отправка логина-пароля для регистрации
//    public RegistrationMessage onButtonRegistration() {
//        final RegistrationMessage registrationMessage = new RegistrationMessage();
//        registrationMessage.setLogin(registrationLoginField.getCharacters().toString());
//        registrationMessage.setPassword(registrationPasswordField.getCharacters().toString());
//        registrationLoginField.clear();
//        registrationPasswordField.clear();
//        return registrationMessage;
//    }
}
