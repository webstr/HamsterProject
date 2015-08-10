package com.test.dao;

public class ServerRequest {
    private int id;
    private String ip;
    private int count;
    private String last;

    public ServerRequest(int id, String ip, int count, String last) {
        this.id = id;
        this.ip = ip;
        this.count = count;
        this.last = last;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
