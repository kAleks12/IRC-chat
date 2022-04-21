package com.interfaces;

import java.io.IOException;

public interface IClient {

    void setHost(String host);

    void setServerPort(String serverPort);

    void nicknameNegotiation(String nickname) throws IOException;

    void getChannelList() throws IOException ;

    void joinChannel(String channelName) throws IOException;

    void sendMessage(String message) throws IOException ;

    void terminateConnection() throws IOException ;

    void channelDisconnect() throws IOException;
}