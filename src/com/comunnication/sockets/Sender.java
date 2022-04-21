package com.comunnication.sockets;

import java.io.*;
import java.net.Socket;

public class Sender {
    private Socket socket;


    public String send(String ip, int port, String message) throws IOException {
        return send(ip, port, message, false);
    }

    public  String send(String ip, int port, String message, boolean awaitResponse) throws IOException {
        socket = new Socket(ip, port);
        var out = new PrintWriter(socket.getOutputStream(), true);
        out.println(message);
        String response = null;
        if(awaitResponse) {
            var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = in.readLine();
        }
        socket.close();
        return response;
    }
}