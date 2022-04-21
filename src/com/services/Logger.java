package com.services;

import com.gui.ServerGui;
import com.models.EventType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Logger {
    private PrintWriter fileStream;
    private final String path = "serverLog.log";



    public void openPrintWriter(){
        if (!Files.exists(Path.of(path))) {
            try {
                Files.createFile(Path.of(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file = new File(path);
        try {
            fileStream =  new PrintWriter(new FileOutputStream(file, true), true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closePrintWriter(){
        fileStream.close();
    }

    public String generateDate(){
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MMMM-dd HH:mm:ss ->  ").withLocale(Locale.ENGLISH);

        return date.format(formatter);
    }

    public String createBody(EventType type){
        return generateDate() + switch (type){
            case ServerAlive -> "Server started working";
            case ServerDead -> "Server stopped working\n==================================================================";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
    public String createBody(EventType type, String user){
        return generateDate()  + "User " + user + switch (type){
            case NegotiatedNick -> " officially joined the server";
            case LeftServer ->  " left the server";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
    public String createBody(EventType type, String user, String entity){
        return generateDate()  + "User " + user + switch (type){
            case JoinedChannel -> " joined channel \"" + entity + "\"";
            case LeftChannel ->  " left channel \"" + entity + "\"";
            case SentMessage -> " sent message to channel \"" + entity + "\"";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public void logEvent(EventType type){
        openPrintWriter();
        fileStream.println(createBody(type));
        closePrintWriter();
        ServerGui.printToScreen(createBody(type) + "\n");
    }
    public void logEvent(EventType type, String user){
        openPrintWriter();
        fileStream.println(createBody(type, user));
        closePrintWriter();
        ServerGui.printToScreen(createBody(type, user) + "\n");
    }
    public void logEvent(EventType type, String user, String entity){
        openPrintWriter();
        fileStream.println(createBody(type, user, entity));
        closePrintWriter();
        ServerGui.printToScreen(createBody(type, user, entity) + "\n");
    }
}