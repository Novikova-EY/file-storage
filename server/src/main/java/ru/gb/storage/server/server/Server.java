package ru.gb.storage.server.server;

/** Netty-Sever */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.storage.commons.handler.JsonDecoder;
import ru.gb.storage.commons.handler.JsonEncoder;
import ru.gb.storage.server.auth.AuthHandler;
import ru.gb.storage.server.auth.JDBCconnection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private static final Logger logger = LogManager.getLogger(Server.class);

    public static void main(String[] args) throws InterruptedException {
        new Server(9000).run();
    }

    public Server(int port) {
        this.port = port;
    }

    public void run() {
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        final ExecutorService threadPool = Executors.newCachedThreadPool();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 3, 0, 3),
                                    new LengthFieldPrepender(3),
                                    new JsonEncoder(),
                                    new JsonDecoder(),
                                    new AuthHandler(),
                                    new ServerHandler(threadPool)
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            new JDBCconnection();

            ChannelFuture future = server.bind(9000).sync();

            logger.info("SERVER: start");

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.throwing(Level.ERROR, e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            threadPool.shutdownNow();
            JDBCconnection.disconnect();
            logger.info("SERVER: shut down");
        }
    }
}