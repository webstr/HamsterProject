package com.test;

import com.test.dao.ChannelRequest;
import com.test.dao.ChannelView;
import com.test.dao.Dao;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ChannelTraffic extends ChannelTrafficShapingHandler {
    private ChannelRequest cr;
    public ChannelTraffic(long checkInterval, ChannelRequest cr) {
        super(checkInterval);
        this.cr = cr;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        this.trafficCounter().start();
    }

    @Override
    public synchronized void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        this.trafficCounter().stop();
        LocalDateTime timeEnd = LocalDateTime.now();

        double connectionDuration = ChronoUnit.SECONDS.between(cr.getTimeStart(), timeEnd);
        long sentBytes = this.trafficCounter().cumulativeReadBytes();
        long receivedBytes = this.trafficCounter().cumulativeWrittenBytes();
        double speed = (sentBytes + receivedBytes) / connectionDuration;

        String url = "";
        for (String str : cr.getUrl()) {
            url += str + " ";
        }
        Dao.getInstance().addChannelView(new ChannelView(cr.getIp(), url, cr.getTimeStart().toString(),
                timeEnd.toString(), sentBytes, receivedBytes, speed));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
