package ru.gb.storage.server.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.commons.message.*;
import ru.gb.storage.server.utils.SqlClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private Path currentClientDir;
    private static final Logger logger = LogManager.getLogger(ServerHandler.class);
    private final int BUFFER_SIZE = 64 * 1024;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof User) {
            User au = (User) msg;
            SqlClient.connect();
            String path = SqlClient.getUserPath(au.getLogin(), au.getPassword());
            if (path != null) {
                logger.debug("Request SQL - " + path);
                Path clientDir = Paths.get(path);
                currentClientDir = clientDir;
                if (!Files.exists(clientDir)) {
                    Files.createDirectory(clientDir);
                }
                ctx.writeAndFlush(new UserDirectory(clientDir.toString()));
            } else {
                ctx.writeAndFlush(new UserReject());
            }
        } else if (msg instanceof RegUser) {
            RegUser regUser = (RegUser) msg;
            SqlClient.connect();
            SqlClient.regUser(regUser.getLogin(), regUser.getPassword());
            SqlClient.disconnect();
            ctx.writeAndFlush(new RegUser(regUser.getLogin(), regUser.getPassword()));

        } else if (msg instanceof ListRequest) {
            ListRequest path = (ListRequest) msg;
            logger.debug("Server receive request String - " + path.getPath());
            logger.debug("Server receive request- " + Paths.get(path.getPath()).toString());
            ctx.writeAndFlush(new ServerList(Files.list(Paths.get(path.getPath())).map(FilesListInfo::new).collect(Collectors.toList())));
            logger.debug("Server sent list ");
            currentClientDir = Paths.get(path.getPath());
            logger.debug("From server current client directory " + currentClientDir.toString());

        } else if (msg instanceof FileRequest) {
            FileRequest request = (FileRequest) msg;
            ctx.writeAndFlush(new FileMessage(currentClientDir.resolve(request.getFilename())));
            currentClientDir = Paths.get(String.valueOf(currentClientDir.resolve(request.getFilename())));

        } else if (msg instanceof FileMessage) {
            FileMessage file = (FileMessage) msg;
            Files.write(currentClientDir.resolve(file.getFileName()), file.getData());

        } else if (msg instanceof DirectoryRequest) {

            ctx.writeAndFlush(new UserDirectory(currentClientDir.toString()));
            logger.debug("Send to client-  server user directory - " + currentClientDir.toString());

        } else if (msg instanceof ParentDirectoryRequest) {
            logger.debug("Request for parent dir - " + currentClientDir.getParent().toString());
            Path p = currentClientDir.getParent();
            ctx.writeAndFlush(new ParentUserDirectory(p.toString()));
            logger.debug("Sent parent dir - " + currentClientDir.getParent().toString());

        } else if (msg instanceof ExitRequest) {
            logger.debug("Disconnected- ");
            ctx.close();
        } else if (msg instanceof FileDelete) {
            FileDelete file = (FileDelete) msg;
            Files.delete(currentClientDir.resolve(file.getFileName()));
            logger.debug("Deleted on server- " + file.getFileName());
        }
    }
}
