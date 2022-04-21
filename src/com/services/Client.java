package com.services;

import com.enums.ClientState;
import com.interfaces.IClient;
import com.interfaces.IClientFrame;
import com.comunnication.flagsresponses.Flags;
import com.comunnication.flagsresponses.ServerResponse;
import com.comunnication.sockets.Receiver;
import com.comunnication.sockets.Sender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client implements IClient {
    private String host;
    private int serverPort;
    private int receivingPort;
    private String nickname;
    private Receiver receiver;
    IClientFrame clientFrame;


    public void setFrame(IClientFrame clientFrame) {
        this.clientFrame = clientFrame;
    }


    public boolean isServerOn(){
        try {
           new Sender().send(host, serverPort,  Flags.Separator + "testing");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private void getReceivingPort() throws IOException {
        String strPort = new Sender().send(host, serverPort, Flags.GetReceivingPort + Flags.Separator + nickname, true);
        receivingPort = Integer.parseInt(strPort);
    }

    private void startListening() {
        receiver = new Receiver(receivingPort, socket -> {
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientFrame.displayMessage(in.readLine());
        });
        receiver.start();
    }


    public void setHost(String host) {
        this.host = host;
        clientFrame.changeState(ClientState.SetPort);
        clientFrame.clearOutputArea();
        clientFrame.displayMessage("Enter port of the server! (number [1024 - 40 000])");
    }

    public void setServerPort(String serverPort) {
        int port = 1024;
        try {
            port = Integer.parseInt(serverPort);
            if (port <= 40000 && port >= 1024) {
                this.serverPort = port;
                clientFrame.changeState(ClientState.SetNickName);
                clientFrame.clearOutputArea();
                clientFrame.displayMessage("Please enter your nickname");
            } else {
                clientFrame.displayMessage("Invalid port!");
            }
        } catch (NumberFormatException e) {
            clientFrame.displayMessage("Not a number!");
        }
    }

    @Override
    public void nicknameNegotiation(String nickname) throws IOException {
        String response;
        if (nickname.isEmpty() || nickname.isBlank()) {
            clientFrame.displayMessage("Bad nickname");
            return;
        }

        response = new Sender().send(host, serverPort, Flags.InitialConnection + Flags.Separator + nickname, true);
        if (response.equals(ServerResponse.Accepted)) {
            this.nickname = nickname;
            clientFrame.changeState(ClientState.JoinChannel);
            getReceivingPort();
            clientFrame.clearOutputArea();
            clientFrame.displayMessage("Enter name of the channel you would like to join (if the channel doesn't exist it will be created automatically):");
            getChannelList();
            return;
        }
        clientFrame.displayMessage(response);
    }

    @Override
    public void getChannelList() throws IOException {
        String channels = new Sender().send(host, serverPort, Flags.GetChannelList + Flags.Separator, true);
        clientFrame.displayChannels( channels.replace(Flags.Separator, "\n") );
    }

    @Override
    public void joinChannel(String channelName) throws IOException {
        String response;

        if (channelName.isBlank() || channelName.isEmpty()){
            clientFrame.displayMessage("Bad channelName");
            return;
        }

        response = new Sender().send(host, serverPort, Flags.JoinChannel + Flags.Separator + nickname + Flags.Separator + channelName, true);
        if (response.equals(ServerResponse.JoinedChannel)) {
            startListening();
            clientFrame.changeState(ClientState.OnChannel);
            clientFrame.clearOutputArea();
            clientFrame.displayMessage("This is the beginning of chat :) \n\n");
            return;
        }
        clientFrame.displayMessage(response);
    }

    @Override
    public void sendMessage(String message) throws IOException {
        if (message.contains(Flags.Separator)) {
            clientFrame.displayMessage(" Prohibited character in the message \" " + Flags.Separator + " \" ");
            return;
        }
        if (message.isEmpty() || message.isBlank()){
            return;
        }
        new Sender().send(host, serverPort, Flags.SendMessage + Flags.Separator + nickname + Flags.Separator + message);
    }

    @Override
    public void channelDisconnect() throws IOException {
        new Sender().send(host, serverPort, Flags.DisconnectUser + Flags.Separator + nickname);
        clientFrame.changeState(ClientState.JoinChannel);
        receiver.terminate();
    }

    @Override
    public void terminateConnection() throws IOException {
        new Sender().send(host, serverPort, Flags.DeleteUser + Flags.Separator + nickname);
        if(receiver == null){ return; }
        receiver.terminate();
    }
}