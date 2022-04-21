package com.services;



import com.comunnication.flagsresponses.Flags;
import com.comunnication.flagsresponses.ServerResponse;
import com.models.Channel;
import com.models.EventType;
import com.models.Message;
import com.models.User;
import com.comunnication.sockets.Receiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class Server {
    private final int port;
    private final Logger serverLogger = new Logger();
    private final List<User> users;
    private final List<Channel> channels;
    private Receiver receiver = null;

    public Server(int port) {
        this.port = port;
        this.users = new LinkedList<>();
        this.channels = new LinkedList<>();
    }

    public void start() {
        serverLogger.logEvent(EventType.ServerAlive);
        receiver = new Receiver(port, socket -> {
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var out = new PrintWriter(socket.getOutputStream(), true);
            String request = in.readLine();
            String[] splitRequest = request.split(Flags.Separator);

            switch (splitRequest[0]) {
                case Flags.InitialConnection -> nicknameCheck(out, splitRequest[1]);
                case Flags.GetReceivingPort -> sendReceivingPort(out, splitRequest[1]);
                case Flags.GetChannelList -> out.println(getChannelList());
                case Flags.JoinChannel -> assignUser(out, splitRequest[1], splitRequest[2]);
                case Flags.SendMessage -> sendChannelMessage(splitRequest[1], splitRequest[2]);
                case Flags.DisconnectUser -> disconnectUser(splitRequest[1]);
                case Flags.DeleteUser -> deleteUser(splitRequest[1]);
                default -> {}
            }
        });
        receiver.setDaemon(true);
        receiver.start();
    }
    public void stop() throws IOException {
        if(receiver != null) {
            receiver.terminate();
        }
        serverLogger.logEvent(EventType.ServerDead);
    }

    private User findUserByNick(String nickname){
        return  users.stream()
                .filter(x -> x.getName().equals(nickname))
                .findFirst()
                .orElse(null);
    }

    private Channel findChannelByName(String name){
        return  channels.stream()
                .filter(x -> x.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void disconnectUser(String nickname){
        User user = findUserByNick(nickname);

        if(user == null){
            return;
        }
        Channel channel = findChannelByName(user.getCurrChannel());

        if(channel == null)
            return;

        channel.removeUser(user.getSendingPort());
        serverLogger.logEvent(EventType.LeftChannel, nickname, channel.getName());
        user.setCurrChannel(null);
    }

    private void deleteUser(String nickname){
        disconnectUser(nickname);
        serverLogger.logEvent(EventType.LeftServer, nickname);
        users.removeIf(x -> x.getName().equals(nickname));
    }

    private void sendChannelMessage(String nickname, String messageBody){
        User user = findUserByNick(nickname);

        if(user == null){
            return;
        }
        Channel channel = findChannelByName(user.getCurrChannel());

        if(channel == null)
            return;

        serverLogger.logEvent(EventType.SentMessage, nickname, channel.getName());
        channel.sendMessage(new Message(user.getName(), messageBody));
    }

    private void sendReceivingPort(PrintWriter out, String nickname) {
        User user = findUserByNick(nickname);

        if(user == null)
            return;

        out.println( user.getSendingPort());
    }

    private int assignPort() {
        int assignedPort = port;
        boolean isAssigned;

        do{
            isAssigned = false;
            assignedPort++;
            for (User user : users) {
                if (user.getSendingPort() == assignedPort) {
                    isAssigned = true;
                    break;
                }
            }
        }while(isAssigned);
        return assignedPort;
    }


    private void assignUser(PrintWriter out, String nickname, String channelName) {
        Channel channel = findChannelByName(channelName);
        User user = findUserByNick(nickname);

        if(channel == null){
            channels.add(new Channel(channelName, user.getSendingPort()));
        }
        else {
            channel.addUser(user.getSendingPort());
        }

        user.setCurrChannel(channelName);
        serverLogger.logEvent(EventType.JoinedChannel, nickname, channelName);
        out.println(ServerResponse.JoinedChannel);
    }

    private void nicknameCheck(PrintWriter out, String nickname) {
        boolean isTaken = users.stream()
                .anyMatch(x -> x.getName().equals(nickname));

        if (isTaken) {
            out.println(ServerResponse.Rejected);
            return;
        }

        users.add(new User(nickname, assignPort()));
        serverLogger.logEvent(EventType.NegotiatedNick, nickname);
        out.println(ServerResponse.Accepted);
    }

    private String getChannelList() {
        if (channels.size() == 0) {
            return "There are no active channels";
        }

        StringBuilder channelList = new StringBuilder();
        for (Channel channel : channels) {
            channelList.append("[*] ").append(channel.getName()).append(Flags.Separator);
        }
        return channelList.toString();
    }
}