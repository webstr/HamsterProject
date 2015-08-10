package com.test.dao;

public class ChannelView {
    private int id;
    private String ip;
    private String url;
    private String timeStart;
    private String timeEnd;
    private long sentBytes;
    private long receivedBytes;
    private double speed;

    public ChannelView(int id, String ip, String url, String timeStart, String timeEnd, long sentBytes, long receivedBytes, double speed) {
        this.id = id;
        this.ip = ip;
        this.url = url;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
        this.speed = speed;
    }

    public ChannelView(String ip, String url, String timeStart, String timeEnd, long sentBytes, long receivedBytes, double speed) {
        this.ip = ip;
        this.url = url;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
        this.speed = speed;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public long getSentBytes() {
        return sentBytes;
    }

    public void setSentBytes(long sentBytes) {
        this.sentBytes = sentBytes;
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
