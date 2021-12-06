package ru.gb.storage.server.auth;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.commons.message.Message;
import ru.gb.storage.commons.message.request.auth.AuthMessage;
import ru.gb.storage.commons.message.request.auth.AuthOkMessage;
import ru.gb.storage.commons.message.request.auth.RegistrationMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class ServerAuthHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LogManager.getLogger(ServerAuthHandler.class);
    private Executor executor;

    public ServerAuthHandler(Executor executor) {
        this.executor = executor;
    }

    // коллекция подключенных клиентов
    static final List<Channel> channels = new ArrayList<Channel>();

    // база данных SQL клиентов
    private final ClientsBD clientsBD = new ClientsBD();

    // добавление канала клиента в коллекцию при его подключении
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        logger.info("Подключен новый клиент" + ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelUnregistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

        logger.info("Входящее сообщение на сервер" + message);

        for (Channel c : channels) {
            executor.execute(() -> {
//                 проверка регистрации нового пользователя
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
                        // TODO прописать что делать после успешной проверки пользователя
                        System.out.println("Пользователь найден");
                        c.writeAndFlush(new AuthOkMessage(true));
                    } else {
                        // TODO прописать автозапрос в регистрации, если логин-пользователь в БД не найден
                        System.out.println("Такого пользователя нет в базе");
                    }
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("SERVER: ERROR: " + cause.getMessage());
        ctx.close();
    }
}
