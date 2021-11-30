package ru.gb.storage.server.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.commons.message.Message;
import ru.gb.storage.commons.message.request.auth.AuthMessage;
import ru.gb.storage.commons.message.request.auth.AuthOkMessage;
import ru.gb.storage.commons.message.request.auth.RegistrationMessage;


public class ServerAuthHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LogManager.getLogger(ServerAuthHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        ClientsBD clientsBD = new ClientsBD();

        // проверка регистрации нового пользователя
        if (message instanceof RegistrationMessage) {
            RegistrationMessage registrationMessage = (RegistrationMessage) message;

            if (!clientsBD.checkLogin(registrationMessage.getLogin())) {
                JDBCconnection.createStatement();
                clientsBD.createNewClient(registrationMessage.getLogin(), registrationMessage.getPassword());
                logger.info("Создан новый пользователь: " + registrationMessage.getLogin());

            } else {
                // TODO прописать что делать при попытке зарегистрировать пользователя с существующим логином
                System.out.println("Пользователь уже существует");
            }

        }

        // проверка аутентификации (первичная логика - один пользователь не может авторизоваться с нескольких клиентов)
        if (message instanceof AuthMessage) {
            AuthMessage authMessage = (AuthMessage) message;

            if (clientsBD.checkLoginPassword(authMessage.getLogin(), authMessage.getPassword())) {
                final AuthOkMessage authOk = new AuthOkMessage();
                authOk.setAuthOk(true);
                System.out.println("Пользователь найден");
            } else {
                // TODO прописать автозапрос в регистрации, если логин-пользователь в БД не найден
                System.out.println("Такого пользователя нет в базе");
            }
        }
    }
}
