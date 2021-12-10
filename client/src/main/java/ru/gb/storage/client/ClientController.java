package ru.gb.storage.client;

import io.netty.handler.codec.serialization.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.commons.message.*;
import ru.gb.storage.commons.message.FileDelete;
import ru.gb.storage.commons.message.FileMessage;
import ru.gb.storage.commons.message.FileRequest;
import ru.gb.storage.commons.message.FilesListInfo;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientController implements Initializable {

    private static final Logger logger = LogManager.getLogger(ClientController.class);

    public TableColumn<FilesListInfo, String> serColDate;
    public TableColumn<FilesListInfo, String> serColName;
    public TableColumn<FilesListInfo, String> serColType;
    public TableColumn<FilesListInfo, Long> serColSize;
    public TableView<FilesListInfo> serViewList;
    public TextField serViewPath;
    public TableColumn<FilesListInfo, String> clColDate;
    public TableColumn<FilesListInfo, String> clColName;
    public TableColumn<FilesListInfo, String> clColType;
    public TableColumn<FilesListInfo, Long> clColSize;
    public TableView<FilesListInfo> clViewList;
    public TextField clViewPath;
    public Path userSerPath;
    public ComboBox<String> clDiskBox;
    public Button serverUpdate;
    public TextField login;
    public TextField password;
    public Button btnConnect;
    public HBox boxServer;
    public TextField loginReg;
    public TextField passwordReg;
    public Button btnReg;
    public HBox mainWindow;
    public VBox clSide;
    public VBox cerSide;
    public HBox buttonBox;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;

    public void prepareColumns() {
        serColType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        serColName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        serColSize.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        serColSize.setCellFactory(column -> new TableCell<FilesListInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) {
                        text = "[DIR]";
                    }
                    setText(text);
                }
            }
        });
        clDiskBox.getItems().clear();
        for (Path p : FileSystems.getDefault().getRootDirectories()) {
            clDiskBox.getItems().add(p.toString());
        }
        clDiskBox.getSelectionModel().select(0);
        clColType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName()));
        clColName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        clColSize.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        clColSize.setCellFactory(column -> new TableCell<FilesListInfo, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    String text = String.format("%,d bytes", item);
                    if (item == -1L) {
                        text = "[DIR]";
                    }
                    setText(text);
                }
            }
        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        serColDate.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDate().format(dtf)));
        clColDate.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDate().format(dtf)));
        clViewList.getSortOrder().add(clColType);
        serViewList.getSortOrder().add(serColType);
    }

    public void initialize(URL location, ResourceBundle resources) {
        prepareColumns();
//        aught();
        serViewList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path p = Paths.get(serViewPath.getText()).resolve(serViewList.getSelectionModel().getSelectedItem().getName());
                logger.debug("Action on chek " + p.toString());
                if (Files.isDirectory(p)) {
                    updateServerInfo(p.toString());
                    serViewPath.setText(p.toString());
                    logger.debug("Action " + p.toString());
                }
            }
        });
        clViewList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Path p = Paths.get(clViewPath.getText()).resolve(clViewList.getSelectionModel().getSelectedItem().getName());
                if (Files.isDirectory(p)) {
                    updateClViewList(p);
                }
            }
        });
        try {
            Socket socket = new Socket("localhost", 9000);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            Thread t = new Thread(() -> {
                updateClViewList(Paths.get(clViewPath.getText()));
                while (true) {
                    try {
                        Message msg = (Message) is.readObject();

                        if (!(msg instanceof ServerList)) {
                            if (msg instanceof UserReject) {
                                updateUI(()->{
                                    Alert a = new Alert(Alert.AlertType.ERROR, "Пользователь не зарегистрирован", ButtonType.OK);
                                    a.showAndWait();
                                });
                            }
                            else if (msg instanceof RegUser){
                                RegUser user = (RegUser) msg;
                                updateUI(()->{
                                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Пользователь "+ user.getLogin()+ " зарегистрирован", ButtonType.OK);
                                    a.showAndWait();
                                });
                            }
                            else if (msg instanceof FileMessage) {
                                FileMessage file = (FileMessage) msg;
                                Path p = Paths.get(clViewPath.getText()).resolve(file.getFileName());
                                Files.write(p, file.getData());
                                updateServerInfo(clViewPath.getText());
                                updateClViewList(Paths.get(clViewPath.getText()));

                            } else if (msg instanceof UserDirectory) {
                                UserDirectory dir = (UserDirectory) msg;
                                logger.debug("User path from server - " + dir.getDir());
                                userSerPath = Paths.get(dir.getDir());
                                serViewPath.setText(dir.getDir());
                                logger.debug("User path userServerPath " + userSerPath.toString());
                                if (serViewList.getItems().isEmpty()) {
                                    updateServerInfo(userSerPath.toString());
                                }

                            } else if (msg instanceof ParentUserDirectory) {
                                ParentUserDirectory dir = (ParentUserDirectory) msg;
                                logger.debug("Parent user path from server - " + dir.getDir());
                                userSerPath = Paths.get(dir.getDir());
                                serViewPath.setText(dir.getDir());
                                logger.debug("Parent user path Parent UserServerPath " + userSerPath.toString());
                                updateServerInfo(userSerPath.toString());
                            }
                        } else {
                            ServerList list = (ServerList) msg;
                            updateUI(() -> {
                                logger.debug(" Client received -  " + list.toString());
                                serViewList.getItems().clear();
                                serViewList.getItems().addAll(list.getServerList());
                            });
                        }
                        logger.debug("Current user path -" + userSerPath);
                        logger.debug("Current text field - " + serViewPath.getText());

                    } catch (ClassNotFoundException | IOException e) {
                        logger.debug("Error of serializing message ");
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (
                IOException e) {
            logger.error("e = ", e);
            Alert a = new Alert(Alert.AlertType.ERROR, "Разорвано соединение с сервером", ButtonType.OK);
            a.showAndWait();
        }
    }

    public void getServerPath() throws IOException {
        os.writeObject(new DirectoryRequest());
        os.flush();
        logger.debug("Send request for user directory");
    }

    public void upload() throws IOException {
        if (!clViewList.isFocused()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Выберите локальный файл", ButtonType.OK);
            alert.showAndWait();
        }
        Path p = Paths.get(clViewPath.getText()).resolve(clViewList.getSelectionModel().getSelectedItem().getName());
        logger.debug("Selected client side- " + p.toString());
        os.writeObject(new FileMessage(p));
        os.flush();
        updateServerInfo(serViewPath.getText());
        updateClViewList(Paths.get(clViewPath.getText()));
    }

    public void download() throws IOException {
        if (!serViewList.isFocused()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Выберете файл на сервере", ButtonType.OK);
            alert.showAndWait();
        }
        logger.debug("Selected server side- " + serViewList.getSelectionModel().getSelectedItem().getName());
        String fileName = serViewList.getSelectionModel().getSelectedItem().getName();
        os.writeObject(new FileRequest(fileName));
        os.flush();
        updateServerInfo(serViewPath.getText());
        updateClViewList(Paths.get(clViewPath.getText()));
    }

    public static void updateUI(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    public void updateClViewList(Path path) {
        updateUI(() -> {
                    try {
                        clViewPath.setText(path.normalize().toAbsolutePath().toString());
                        clViewList.getItems().clear();
                        clViewList.getItems().addAll(Files.list(path).map(FilesListInfo::new).collect(Collectors.toList()));
                        clViewList.sort();
                    } catch (IOException e) {
                        logger.error("e = ", e);
                        Alert a = new Alert(Alert.AlertType.WARNING, "Can not update interface ", ButtonType.OK);
                        a.showAndWait();
                    }
                }
        );
    }

    public void btnClPathUp() {
        Path upPath = Paths.get(clViewPath.getText()).getParent();
        if (upPath != null) {
            updateClViewList(upPath);
        }
    }

    public void selectDisk(ActionEvent a) {
        ComboBox<String> element = (ComboBox<String>) a.getSource();
        updateClViewList(Paths.get(element.getSelectionModel().getSelectedItem()));
    }
    public Path getClCurrentPath() {
        return Paths.get(clViewPath.getText());
    }

    public void updateServerInfo(String path) {
        try {
            os.writeObject(new ListRequest(path));
            os.flush();
            logger.debug("From client to server request " + path);
        } catch (IOException e) {
            logger.error("e = ", e);
            e.printStackTrace();
        }
    }

    public void deletePC() {
        if (clViewList.isFocused()) {
            Path p = getClCurrentPath().resolve(clViewList.getSelectionModel().getSelectedItem().getName());
            try {
                Files.delete(p);
                updateClViewList(getClCurrentPath());
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Ошибка удаления", ButtonType.OK);
                a.showAndWait();
            }
        } else {
            String fileName = serViewList.getSelectionModel().getSelectedItem().getName();
            try {
                os.writeObject(new FileDelete(fileName));
                os.flush();
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Удалено", ButtonType.OK);
                a.showAndWait();
                updateServerInfo(serViewPath.getText());
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Ошибка", ButtonType.OK);
                a.showAndWait();
            }
        }
    }

    public void makeDir() {
        TextInputDialog dialog = new TextInputDialog("Name");
        dialog.setTitle("Create the directory");
        dialog.setHeaderText("Enter directory name");
        dialog.setContentText("Please enter name:");
        Optional<String> result = dialog.showAndWait();
        File dir = new File(Paths.get(clViewPath.getText(), result.get()).toString());
        if (dir.mkdir()) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Успешно", ButtonType.OK);
            a.showAndWait();
            updateClViewList(getClCurrentPath());
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING, "Ошибка", ButtonType.OK);
            a.showAndWait();
        }
    }

    public void connect() {
        User user = new User(login.getText(), password.getText());
        try {
            os.writeObject(user);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        login.setEditable(false);
        password.setEditable(false);
        serverUpdate.setVisible(true);
    }

    public void registration() {
        RegUser regUser = new RegUser(loginReg.getText(),passwordReg.getText());
        try {
            os.writeObject(regUser);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginReg.clear();
        passwordReg.clear();
    }
}