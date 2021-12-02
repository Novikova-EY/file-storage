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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.commons.handler.JsonDecoder;
import ru.gb.storage.commons.handler.JsonEncoder;

public class Network {
    private static final Logger logger = LogManager.getLogger(Network.class);

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

//                FileDownloadRequest fileToDownload = new FileDownloadRequest();
//                fileToDownload.setPath("F:\\[GB] Java\\2 четверть\\2-2-1. Разработка сетевого хранилища на " +
//                        "Java_Плеханов А\\HW\\file_storage\\");
//                channelFuture.channel().writeAndFlush(new FileDownloadRequest());


                // отправка файла на сервер
//                clientController.getButtonAuthorization().setOnMouseClicked(event -> {
//                    authMessageClient.setLogin(clientController.getLoginField().getText());
//                    authMessageClient.setPassword(clientController.getPasswordField().getText());
//                    System.out.println("authMessageClient.getPassword() = " + authMessageClient.getPassword());
//                    System.out.println("authMessageClient.getLogin() = " + authMessageClient.getLogin());
//                    channelFuture.channel().writeAndFlush(authMessageClient);
//            loginField.clear();
//            passwordField.clear();
//                });


            }
        } finally {
            group.shutdownGracefully();
            logger.info("CLIENT: disconnect");
        }
    }
}
