package com.test.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Dao {
    /*input value*/
    private final String databaseName   = "netty";
    private final String ip             = "localhost";
    private final int    port           = 3306;
    private final String user           = "root";
    private final String password       = "root";
    /*-----------*/
    private Connection con = null;
    private Statement stmt = null;

    private static Dao instance = new Dao();

    public static Dao getInstance() {
        return instance;
    }

    public Dao() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://" + ip + ":" + port + "/" + databaseName;
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //create tables need to save statistics
        try {
            String sql1 = "CREATE TABLE redirectrequest (id INT NOT NULL AUTO_INCREMENT, url VARCHAR(45), count INT, " +
                    "PRIMARY KEY (id))";
            stmt.executeUpdate(sql1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            String sql2 = "CREATE TABLE serverrequest (id INT NOT NULL AUTO_INCREMENT, ip VARCHAR(45), count INT, " +
                    "last VARCHAR(45), PRIMARY KEY (id))";
            stmt.executeUpdate(sql2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
            String sql3 = "CREATE TABLE channelrequest (id INT NOT NULL AUTO_INCREMENT, ip VARCHAR(45), url VARCHAR(45), " +
                    "timestart VARCHAR(45), timeend VARCHAR(45), sentbytes INT, receivedbytes INT, speed DOUBLE, PRIMARY KEY (id))";
            stmt.executeUpdate(sql3);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void stop() throws SQLException {
        con.close();
    }
    //redirect table
    public synchronized List<RedirectRequest> getRedirectRequest() {
        List<RedirectRequest> ls = new ArrayList<RedirectRequest>();
        String sql = "SELECT * FROM redirectrequest";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String url = rs.getString("url");
                int count = rs.getInt("count");
                ls.add(new RedirectRequest(id, url, count));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
        return ls;
    }

    public synchronized boolean addRedirectRequest(String url) {
        String sql1 = "SELECT * FROM redirectrequest WHERE url = '" + url + "'";
        try {
            ResultSet rs = stmt.executeQuery(sql1);
            if (!rs.next()) {
                String sql2 = "INSERT INTO redirectrequest (url, count) VALUES ('" + url + "', " + 1 + ")";
                stmt.executeUpdate(sql2);
            } else {
                int count = rs.getInt("count") + 1;
                String sql3 = "UPDATE redirectrequest SET count = " + count + " WHERE url = '" + url + "'";
                stmt.executeUpdate(sql3);
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public synchronized int getNumberRedirectRequest() {
        String sql = "SELECT SUM(count) as s FROM redirectrequest";
        int res = 0;
        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                res = rs.getInt("s");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }

        return res;
    }
    //server table
    public synchronized List<ServerRequest> getServerRequest() {
        List<ServerRequest> ls = new ArrayList<ServerRequest>();
        String sql = "SELECT * FROM serverrequest";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String ip = rs.getString("ip");
                int count = rs.getInt("count");
                String last = rs.getString("last");
                ls.add(new ServerRequest(id, ip, count, last));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
        return ls;
    }

    public synchronized boolean addServerRequest(String ip) {
        String sql1 = "SELECT * FROM serverrequest WHERE ip = '" + ip + "'";
        try {
            ResultSet rs = stmt.executeQuery(sql1);
            if (!rs.next()) {
                String sql2 = "INSERT INTO serverrequest (ip, count, last) VALUES ('" + ip + "', " + 1 + ", '"
                        + LocalDateTime.now().toString() +"')";
                stmt.executeUpdate(sql2);
            } else {
                int count = rs.getInt("count") + 1;
                String sql3 = "UPDATE serverrequest SET count = " + count + ", last = '" + LocalDateTime.now().toString()
                        + "' WHERE ip = '" + ip + "'";
                stmt.executeUpdate(sql3);
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public synchronized int getNumberServerRequest() {
        String sql = "SELECT SUM(count) as s FROM serverrequest";
        int res = 0;
        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                res = rs.getInt("s");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
        return res;
    }

    public synchronized int getUniqueNumberServerRequest() {
        String sql = "SELECT COUNT(DISTINCT id) as s FROM serverrequest";
        int res = 0;
        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                res = rs.getInt("s");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
        return res;
    }
    //channel table
    public synchronized List<ChannelView> getChannelView() {
        List<ChannelView> ls = new ArrayList<ChannelView>();
        String sql = "SELECT * FROM channelrequest";
        try {
            ResultSet rs = stmt.executeQuery(sql);

            rs.last();
            int k = 0;
            while (rs.previous() && k < 16) {
                k++;
                int id = rs.getInt("id");
                String ip = rs.getString("ip");
                String url = rs.getString("url");
                String timeStart = rs.getString("timeStart");
                String timeEnd = rs.getString("timeend");
                long sentBytes = rs.getLong("sentbytes");
                long receivedBytes = rs.getLong("receivedbytes");
                double speed = rs.getDouble("speed");
                ls.add(new ChannelView(id, ip, url, timeStart, timeEnd, sentBytes, receivedBytes, speed));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
        }
        return ls;
    }

    public synchronized boolean addChannelView(ChannelView cv) {
        String sql = "INSERT INTO channelrequest (ip, url, timestart, timeend, sentbytes, receivedbytes, speed) VALUES ('" +
                cv.getIp() + "', '" + cv.getUrl() + "', '" + cv.getTimeStart() + "', '" + cv.getTimeEnd() + "', " +
                cv.getSentBytes() + ", "  + cv.getReceivedBytes() + ", " + cv.getSpeed() + ")";
        try {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
