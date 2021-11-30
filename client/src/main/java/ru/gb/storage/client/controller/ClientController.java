package ru.gb.storage.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ru.gb.storage.commons.message.request.auth.AuthMessage;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    @FXML
    private Button buttonAuthication;

    @FXML
    private TextField login;

    @FXML
    private TextField password;

    @FXML
    private TextField currentAuthorizedClient;

    @FXML
    private Button buttonAuthorization;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public String loginInput(ActionEvent actionEvent) {
        return login.getCharacters().toString();
    }

    public String passwordInput(ActionEvent actionEvent) {
        return password.getCharacters().toString();
    }

    public void setButtonAuthication(ActionEvent actionEvent) {
        AuthMessage authMessage = new AuthMessage();
        authMessage.setLogin(loginInput(actionEvent));
        authMessage.setPassword(passwordInput(actionEvent));
    }

    public void setButtonAuthorization(ActionEvent event) {
        Label secondLabel = new Label("I'm a Label on new Window");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(secondLabel);

        Scene secondScene = new Scene(secondaryLayout, 230, 100);

        Stage newWindow = new Stage();
        newWindow.setTitle("Second Stage");
        newWindow.setScene(secondScene);

        newWindow.show();
    }

    public void currentAuthorizedClient(ActionEvent actionEvent) {
        currentAuthorizedClient.setText("Client is authorized");
    }
}
