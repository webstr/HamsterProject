package com.test;

import com.test.dao.ChannelRequest;
import com.test.dao.Dao;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.util.*;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private ChannelRequest cr;
    private static Timer timer = new HashedWheelTimer();

    private class HelloPageTimer implements TimerTask {
        ChannelHandlerContext ctx;
        FullHttpRequest req;
        FullHttpResponse res;

        public HelloPageTimer(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
            this.ctx = ctx;
            this.req = req;
            this.res = res;
        }

        public void run(Timeout timeout) throws Exception {
            sendHttpResponse(ctx, req, res);
        }
    }

    public static DefaultChannelGroup allChannels = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    public ServerHandler(ChannelRequest cr) {
        this.cr = cr;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            allChannels.add(ctx.channel());
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // bad request
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        if (req.method() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }


        if ("/hello".equals(req.uri())) {
            ByteBuf content = ServerIndexPage.getHelloContent();
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            String ip = ((InetSocketAddress) (ctx.channel()).remoteAddress()).getAddress().getHostAddress();
            Dao.getInstance().addServerRequest(ip);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpHeaderUtil.setContentLength(res, content.readableBytes());
            cr.addUrl(req.uri());
            timer.newTimeout(new HelloPageTimer(ctx, req, res), 10, TimeUnit.SECONDS);
            return;
        }

        if ("/status".equals(req.uri())) {
            ByteBuf content = ServerIndexPage.getStatusContent();
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);

            String ip = ((InetSocketAddress) (ctx.channel()).remoteAddress()).getAddress().getHostAddress();
            Dao.getInstance().addServerRequest(ip);

            res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpHeaderUtil.setContentLength(res, content.readableBytes());
            cr.addUrl(req.uri());
            sendHttpResponse(ctx, req, res);
            return;
        }
        Pattern redirect = Pattern.compile("^/redirect[?]url=.*$");
        Matcher matcher = redirect.matcher(req.uri());
        if (matcher.matches()) {
            QueryStringDecoder qsd = new QueryStringDecoder(req.uri());
            List<String> url = qsd.parameters().get("url");

            if (url.get(0) != "") {
                Dao.getInstance().addRedirectRequest(url.get(0));
            }
            String ip = ((InetSocketAddress) (ctx.channel()).remoteAddress()).getAddress().getHostAddress();
            Dao.getInstance().addServerRequest(ip);

            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
            res.headers().set(LOCATION, "http://" + url.get(0));
            cr.addUrl(url.get(0));
            sendHttpResponse(ctx, req, res);
            return;
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaderUtil.setContentLength(res, res.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaderUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        //ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}

