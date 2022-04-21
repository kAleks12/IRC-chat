package com.models;

import java.sql.Timestamp;

public class User {
    private final String userName;
    private final Timestamp joinTime;
    private final int sendingPort;
    private String currChannel;


    public User(String name, int sendingPort) {
        this.userName = name;
        this.sendingPort = sendingPort;
        joinTime = new Timestamp(System.currentTimeMillis());
    }

    public String getCurrChannel() { return currChannel; }

    public void setCurrChannel(String currChannel) { this.currChannel = currChannel; }

    public String getName(){ return this.userName; }

    public Timestamp getJoinTime() { return joinTime; }

    public int getSendingPort() { return sendingPort; }
}