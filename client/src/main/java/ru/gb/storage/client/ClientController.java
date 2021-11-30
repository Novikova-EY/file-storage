package ru.gb.storage.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import ru.gb.storage.commons.message.request.auth.AuthMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private Button buttonAuthication;

    @FXML
    private TextField login;

    @FXML
    private Button buttonAuthorization;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public void setButtonAuthication(ActionEvent event) {
        new AuthMessage();
    }

    public void setButtonAuthorization(ActionEvent event) {

    }

    public void setButtonAythication(ActionEvent actionEvent) {
    }
}
