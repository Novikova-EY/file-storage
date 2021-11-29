package ru.gb.storage.server.server;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import ru.gb.storage.commons.message.file.EndFileTransferMessage;
import ru.gb.storage.commons.message.file.FileMessage;


public class ServerHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LogManager.getLogger(ServerHandler.class);

    //TODO заменить FILE_NAME
    private final String FILE_NAME = "1.docx";
    private final int BUFFER_SIZE = 64 * 1024;
    private Executor executor;

    public ServerHandler(Executor executor) {
        this.executor = executor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) {

        // скачивание файла с сервера на клиент
        if (message instanceof FileMessage) {
            executor.execute(() -> {
                try (final RandomAccessFile randomAccessFile = new RandomAccessFile(FILE_NAME, "r")) {
                    long fileLength = randomAccessFile.length();
                    do {
                        long position = randomAccessFile.getFilePointer();
                        long byteToEndOfFile = fileLength - position;

                        byte[] content;
                        boolean fileTransferred = false;
                        if (byteToEndOfFile >= BUFFER_SIZE) {
                            content = new byte[BUFFER_SIZE];
                        } else {
                            content = new byte[(int) byteToEndOfFile];
                            fileTransferred = true;
                        }

                        randomAccessFile.read(content);

                        FileMessage fileMessage = new FileMessage();

                        fileMessage.setName(FILE_NAME);
                        fileMessage.setContent(content);
                        fileMessage.setPosition(position);
                        fileMessage.setFileTransfer(fileTransferred);

                        ctx.writeAndFlush(fileMessage).sync();
                    }
                    while (randomAccessFile.getFilePointer() < fileLength);
                    ctx.writeAndFlush(new EndFileTransferMessage());
                } catch (InterruptedException e) {
                    logger.error("CLIENT: File don't send" + e);
                } catch (IOException e) {
                    logger.throwing(Level.ERROR, e);
                }
            });
        }

        // TODO загрузка файла с клиента на сервер

        // TODO переименование файла

        // TODO удаление файла

        // TODO просмотр файла на клиенте

        // TODO просмотр дерева файлов
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("SERVER: ERROR: " + cause.getMessage());
        ctx.close();
    }
}
