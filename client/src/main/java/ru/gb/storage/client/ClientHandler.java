package ru.gb.storage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.client.controller.ClientController;
import ru.gb.storage.commons.message.Message;
import ru.gb.storage.commons.message.file.EndFileTransferMessage;
import ru.gb.storage.commons.message.file.FileMessage;
import ru.gb.storage.commons.message.request.auth.AuthMessage;
import ru.gb.storage.commons.message.request.auth.AuthOkMessage;
import ru.gb.storage.commons.message.request.auth.RegistrationMessage;


import java.io.RandomAccessFile;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.fatal("Cause.client :" + cause.getMessage());
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        // получение входящего файла от сервера
        if (message instanceof FileMessage) {
            FileMessage file = (FileMessage) message;
            try (final RandomAccessFile randomAccessFile = new RandomAccessFile(file.getName(), "rw")) {

                randomAccessFile.seek(file.getPosition());
                randomAccessFile.write(file.getContent());
            }
        }

        if (message instanceof EndFileTransferMessage) {
            logger.info("CLIENT: File transfer: success");
            ctx.close();
        }


        if (message instanceof AuthMessage) {
            AuthMessage authMessage = (AuthMessage) message;
            ctx.writeAndFlush(authMessage);
        }


        if (message instanceof AuthOkMessage) {
            AuthOkMessage authOk = (AuthOkMessage) message;
            if (authOk.isAuthOk()) {


            }
        }


//        if (message instanceof TextMessage) {
//            TextMessage incomingMessage = (TextMessage) message;
//            System.out.println("ECHO: incoming message from client: " + new Date().toString() + ": " + incomingMessage.getText());
//        }
    }
}
