package com;

import com.gui.ServerGui;
import com.services.Server;

import java.io.IOException;
import java.util.Scanner;

public class ServerLauncher {
    private static int negotiatePort(Scanner keyboardInput) {
        boolean isValid = false;
        int port = 1024;

        ServerGui.printToScreen("Welcome, please enter port of the server - number [1024 - 49151]: ");
        while (!isValid) {
            try {
                port = Integer.parseInt(keyboardInput.nextLine());
                if (port <= 40000 && port >= 1024) {
                    isValid = true;
                } else {
                    ServerGui.printToScreen("Invalid port!");
                }
            } catch (NumberFormatException e) {
                ServerGui.printToScreen("Not a number!");
            }
        }
        return port;
    }

    public static void main(String[] args) throws IOException {
        Scanner keyboardInput = new Scanner(System.in);
        Server server = new Server(negotiatePort(keyboardInput));
        ServerGui.printToScreen("If you wish to shut down the server type \"q\" and press enter at any time :)");
        server.start();

        while (true) {
            if (keyboardInput.nextLine().equalsIgnoreCase("q")) {
                server.stop();
                break;
            }
        }
    }
}