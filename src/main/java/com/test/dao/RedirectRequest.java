package com.test.dao;

public class RedirectRequest {
    private int id;
    private String url;
    private int count;

    public RedirectRequest(int id, String url, int count) {
        this.id = id;
        this.url = url;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
