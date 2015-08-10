package com.test.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChannelRequest {
    private int id;
    private String ip;
    private List<String> url = new ArrayList<String>();
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private long sentBytes;
    private long receivedBytes;
    private double speed;

    public ChannelRequest() {

    }

    public void addUrl(String str) {
        if (!url.contains(str)) {
            url.add(str);
        }
    }

    public List<String> getUrl() {
        return this.url;
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

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
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
