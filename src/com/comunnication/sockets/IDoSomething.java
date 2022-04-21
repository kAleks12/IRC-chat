package com.comunnication.sockets;

import java.io.IOException;
import java.net.Socket;

public interface IDoSomething {
    void acceptReaction(Socket client) throws IOException;
}