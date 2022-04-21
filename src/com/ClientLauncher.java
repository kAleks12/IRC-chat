package com;

import com.gui.ClientFrame;
import com.services.Client;

public class ClientLauncher {
    public static void main(String[] args) {
        Client client = new Client();
        ClientFrame frame = new ClientFrame(client);
        frame.displayMessage("Please enter host name of the server you would like to join! (probably \"localhost\")");
        client.setFrame(frame);
    }
}