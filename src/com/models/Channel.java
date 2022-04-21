package com.models;

import com.comunnication.sockets.Sender;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Channel {
    private final String name;
    private final List<Message> messages;
    private List<Integer> ports;

    public Channel(String name, int initialPort) {
        this.name = name;
        ports = new LinkedList<>();
        ports.add(initialPort);
        messages = new LinkedList<>();
    }

    public void addUser(int port) {
        ports.add(port);
    }

    public void removeUser(int port) {
        ports.removeIf(x -> x == port);
    }

    public String getName() {
        return name;
    }

    public void sendMessage(Message message) {
        messages.add(message);
        Sender sender = new Sender();
        for (int port : ports) {
            try {
                sender.send("localhost", port, message.toString());
            } catch (IOException e) {
                System.out.println("Could not deliver message to port: " + port);
            }
        }
    }
}