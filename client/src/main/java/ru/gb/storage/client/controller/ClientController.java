package ru.gb.storage.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController extends FxController implements Initializable {

    @FXML
    private Button buttonRegistration;

    @FXML
    private TextField login;

    @FXML
    private TextField password;

    @FXML
    private TextField currentAuthorizedClient;

    @FXML
    private Button buttonAuthorization;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    /**
     * FXML loader invoke method "initialize" by reflection if it exists after read full context
     */


    public Button setButtonRegistration(ActionEvent event) {
        return buttonRegistration;
    }

    public String loginInput(ActionEvent actionEvent) {
        return login.getCharacters().toString();
    }

    public String passwordInput(ActionEvent actionEvent) {
        return password.getCharacters().toString();
    }

    public void setButtonAuthication(ActionEvent actionEvent) {

    }

    public void currentAuthorizedClient(ActionEvent actionEvent) {
        currentAuthorizedClient.setText("Client is authorized");
    }

    public Button getButtonRegistration() {
        return buttonRegistration;
    }

}
