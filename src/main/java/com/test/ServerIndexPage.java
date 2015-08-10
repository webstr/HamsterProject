package com.test;

import com.test.dao.ChannelView;
import com.test.dao.Dao;
import com.test.dao.RedirectRequest;
import com.test.dao.ServerRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.util.List;

public class ServerIndexPage {

    public static ByteBuf getHelloContent() {
            return Unpooled.copiedBuffer("<html><head><title>Hello</title></head><body>Hello World</body></html>", CharsetUtil.UTF_8);

    }

    public static ByteBuf getStatusContent() {
        return Unpooled.copiedBuffer(generateStatusContent(), CharsetUtil.UTF_8);
    }

    private static String generateStatusContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("<head><title>status</title>")
                .append("<style>\n" +
                        "table, th, td {\n" +
                        "    border: 1px solid black;\n" +
                        "}\n" +
                        "</style>")
                .append("</head>")
                .append("<body>")
                //requests count
                .append("<p>Server request number: ")
                .append(Dao.getInstance().getNumberServerRequest() + "</p>")
                .append("<p>Server unique request number: ")
                .append(Dao.getInstance().getUniqueNumberServerRequest() + "</p>")

                //server requests
                .append("<table><caption>Server request</caption>")
                .append("<tr><th>ip</th><th>count</th><th>last time</th></tr>");
        List<ServerRequest> sr = Dao.getInstance().getServerRequest();
        for (ServerRequest data : sr) {
            sb.append("<tr><td>")
                    .append(data.getIp())
                    .append("</td><td>")
                    .append(data.getCount())
                    .append("</td><td>")
                    .append(data.getLast())
                    .append("</td></tr>");
        }
        sb.append("</table>");

	    //redirect request
        sb.append("<p><table><caption>Redirect requests</caption>")
                .append("<tr><th>url</th><th>count</th></tr>");
        List<RedirectRequest> rr = Dao.getInstance().getRedirectRequest();
        for (RedirectRequest data : rr) {
            sb.append("<tr><td>")
                    .append(data.getUrl()).append("</td><td>")
                    .append(data.getCount()).append("</td></tr>");
        }
        sb.append("</table></p>");


        sb.append("<p>Open connections: ").append(ServerHandler.allChannels.size() + "</p>");
        sb.append("<table><caption>Last 16 connections</caption>")
                .append("<tr><th>ip</th><th>url</th><th>timeStart</th><th>timeEnd</th><th>sent</th><th>received</th><th>speed</th></tr>");
        List<ChannelView> cv = Dao.getInstance().getChannelView();
        for (ChannelView data : cv) {
            sb.append("<tr><td>")
                    .append(data.getIp()).append("</td><td>")
                    .append(data.getUrl()).append("</td><td>")
                    .append(data.getTimeStart()).append("</td><td>")
                    .append(data.getTimeEnd()).append("</td><td>")
                    .append(data.getSentBytes()).append("</td><td>")
                    .append(data.getReceivedBytes()).append("</td><td>")
                    .append(data.getSpeed()).append("</td></tr>");
        }
        sb.append("</table>");

        sb.append("</body>");
        return sb.toString();
    }
}