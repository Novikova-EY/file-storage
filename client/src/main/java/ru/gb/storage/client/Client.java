package ru.gb.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import javafx.application.Application;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.client.controller.ClientController;
import ru.gb.storage.client.controller.FxController;
import ru.gb.storage.client.controller.RegistrationController;
import ru.gb.storage.commons.handler.JsonDecoder;
import ru.gb.storage.commons.handler.JsonEncoder;
import ru.gb.storage.commons.message.request.auth.RegistrationMessage;

public class Client extends Application {
    private static final Logger logger = LogManager.getLogger(Client.class);

    public static void main(String[] args) {
        Client.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //create new stage for message window
        Stage registrationStage = new Stage();

        //set parent stage for massage window. It's optional step
//        registrationStage.initOwner(primaryStage);

        //set parent stage behavior when child stage is showing
        registrationStage.initModality(Modality.APPLICATION_MODAL);

        //get demo controller
        ClientController clientController = FxController.init(primaryStage, "fxml/client.fxml");

        //get message controller
        RegistrationController registrationController = FxController.init(registrationStage, "fxml/registration.fxml");

        //set event to button
        clientController.getButtonRegistration().setOnMouseClicked(event -> registrationController.getStage().show());

        clientController.getStage().show();
    }

    public void run() throws InterruptedException {
        final NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonEncoder(),
                                    new JsonDecoder(),
                                    new ClientHandler());
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.connect("localhost", 9000).sync();

            logger.info("CLIENT: start");

            while (true) {
//                Scanner in = new Scanner(System.in);
//                String message = in.nextLine();
//                TextMessage inputMessage = new TextMessage();
//                inputMessage.setText(message);
//                channelFuture.channel().writeAndFlush(inputMessage);

//                FileDownloadRequest fileToDownload = new FileDownloadRequest();
//                fileToDownload.setPath("F:\\[GB] Java\\2 четверть\\2-2-1. Разработка сетевого хранилища на " +
//                        "Java_Плеханов А\\HW\\file_storage\\");
//                channelFuture.channel().writeAndFlush(new FileDownloadRequest());


                // отправка файла на сервер

//                channelFuture.channel().writeAndFlush(new FileMessage());
                final RegistrationMessage registrationMessage = new RegistrationMessage();
                System.out.println(registrationMessage.getLogin() + registrationMessage.getPassword());
                channelFuture.channel().writeAndFlush(registrationMessage);
                channelFuture.channel().closeFuture().sync();

            }
        } finally {
            group.shutdownGracefully();
            logger.info("CLIENT: disconnect");
        }
    }
}
