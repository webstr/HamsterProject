package com.test;

import com.test.dao.Dao;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {

    static final int PORT = 8080;

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerInitializer());
            Channel ch = b.bind(PORT).sync().channel();
            System.out.println("Open http://127.0.0.1:" + PORT + "/status to see status");
            System.out.println("Open http://127.0.0.1:" + PORT + "/hello to see hello page after 10 second");
            System.out.println("Open http://127.0.0.1:" + PORT + "/redirect?url= and your url to redirect to this page");
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            Dao.getInstance().stop();
        }
    }
}