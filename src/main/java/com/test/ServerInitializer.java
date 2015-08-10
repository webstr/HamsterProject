package com.test;

import com.test.dao.ChannelRequest;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.time.LocalDateTime;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        String ip = ch.remoteAddress().getAddress().getHostAddress();
        LocalDateTime timeStart = LocalDateTime.now();
        ChannelRequest cr = new ChannelRequest();
        cr.setIp(ip);
        cr.setTimeStart(timeStart);

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChannelTraffic(0, cr));
        pipeline.addLast(new HttpObjectAggregator(12345));
        pipeline.addLast(new ServerHandler(cr));
    }
}