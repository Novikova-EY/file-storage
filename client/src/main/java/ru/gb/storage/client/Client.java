package ru.gb.storage.client;

import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.gb.storage.client.controller.ClientController;
import ru.gb.storage.client.controller.FxController;
import ru.gb.storage.client.controller.RegistrationController;

public class Client extends Application {
    private static final Network network = new Network();

    public static void main(String[] args) throws InterruptedException {
        network.run();
        Client.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage registrationStage = new Stage();
        registrationStage.initOwner(primaryStage);
        registrationStage.initModality(Modality.APPLICATION_MODAL);
        ClientController clientController = FxController.init(primaryStage, "fxml/client.fxml");
        RegistrationController registrationController = FxController.init(registrationStage, "fxml/registration.fxml");
        clientController.getButtonRegistration().setOnMouseClicked(event -> registrationController.getStage().show());
        clientController.getStage().show();
    }
}
