package com.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Message {
    private final Timestamp creationDate;
    private final String author;
    private final String content;


    public Message(String author, String content){
        this.author = author;
        this.content = content;
        creationDate  = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public String toString(){
        LocalDateTime date = creationDate.toLocalDateTime();
        return date.getYear() +"-"+ date.getMonth() +"-"+ date.getDayOfMonth() +"  "+ date.getHour() +":"+ date.getMinute() +":"+ date.getSecond() + " By: " + author + " ->  " + content;
    }
}