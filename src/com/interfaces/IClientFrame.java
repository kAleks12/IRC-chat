package com.interfaces;

import com.enums.ClientState;

public interface IClientFrame {
    void displayMessage (String message);
    void displayChannels (String channels);
    void changeState (ClientState state);
    void clearOutputArea();
}