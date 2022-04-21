package com.comunnication.sockets;



import java.io.IOException;
import java.net.ServerSocket;


public class Receiver extends Thread {
    private ServerSocket serverSocket;
    boolean isTerminate = false;
    IDoSomething doSomething;


    public Receiver(int port, IDoSomething doSomething){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.doSomething = doSomething;
    }

    public void terminate() throws IOException {
        isTerminate = true;
        serverSocket.close();
        this.interrupt();
    }

    @Override
    public void run() {
        while(!isTerminate) {
            try {
                doSomething.acceptReaction(serverSocket.accept());
            } catch (IOException ignored){}
        }
    }
}