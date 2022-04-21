package com.gui;

import com.enums.ClientState;
import com.interfaces.IClient;
import com.interfaces.IClientFrame;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ClientFrame extends JFrame implements IClientFrame {
    private JButton quitButton;
    private JButton sendButton;
    private JPanel mainPane;
    private JScrollPane outputPane;
    private JTextArea outputArea;
    private JLabel messageLabel;
    private JTextField textInput;
    private JLabel inputLabel;
    private JPanel InputField;
    private JButton refreshChannelsButton;
    private JButton disconnectButton;
    private JLabel channelsListLabel;
    private JTextArea channelsTextArea;
    private JLabel serverStateLabel;
    private final IClient client;
    private ClientState state = ClientState.SetHost;

    public ClientFrame(IClient client) {
        initializeFrame();
        initializeButtons();
        this.client = client;
    }


    private void initializeFrame() {
        setContentPane(mainPane);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setSize(new Dimension(1020, 700));
        setResizable(false);
        outputArea.setEditable(false);
        channelsTextArea.setEditable(false);
        textInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        });
    }

    private void initializeButtons() {
        textInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendAction();
                }
            }
        });

        disconnectButton.addActionListener(e -> {
            if(state.equals(ClientState.OnChannel)) {
                try {
                    client.channelDisconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                clearOutputArea();
                displayMessage("Enter name of the channel you would like to join (if the channel doesn't exist it will be created automatically):");
            }
        });

        refreshChannelsButton.addActionListener(e -> {
            if(state.equals(ClientState.JoinChannel) || state.equals(ClientState.OnChannel)) {
                try {
                    client.getChannelList();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        quitButton.addActionListener(e -> dispose());

        sendButton.addActionListener(e -> sendAction());
    }

    private void sendAction() {
        try {
            switch (state) {
                case SetHost -> client.setHost(textInput.getText());
                case SetPort -> client.setServerPort(textInput.getText());
                case SetNickName -> client.nicknameNegotiation(textInput.getText());
                case JoinChannel -> client.joinChannel(textInput.getText());
                case OnChannel -> client.sendMessage(textInput.getText());
            }
            if (serverStateLabel.getText().equals("Server is offline")) {
                serverStateLabel.setText("Server is online");
            }
            textInput.setText("");
        } catch (IOException e) {
            if (serverStateLabel.getText().equals("Server is online")) {
                serverStateLabel.setText("Server is offline");
            }
            outputArea.append("Server is down!\n");
            if (state == ClientState.JoinChannel || state == ClientState.OnChannel) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignore) {
                }
                state = ClientState.SetNickName;
                clearOutputArea();
                outputArea.append("Server is down! Your client restarted to the nickname negotiation stage\n");
                outputArea.append("Please enter your nickname\n");
            }
        }
    }


    @Override
    public void dispose() {
        try {
            client.terminateConnection();
        } catch (IOException ignore) {
        }
        super.dispose();
    }


    @Override
    public void displayMessage(String message) {
        outputArea.append(message + "\n");
    }

    @Override
    public void displayChannels(String channels) {
        channelsTextArea.setText("");
        channelsTextArea.append(channels);
    }

    @Override
    public void changeState(ClientState state) {
        this.state = state;
    }

    @Override
    public void clearOutputArea() {
        outputArea.setText("");
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPane = new JPanel();
        mainPane.setLayout(new GridLayoutManager(2, 5, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel1, new GridConstraints(0, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(600, 400), null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        outputPane = new JScrollPane();
        panel1.add(outputPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(700, -1), null, 0, false));
        outputArea = new JTextArea();
        outputArea.setText("");
        outputPane.setViewportView(outputArea);
        messageLabel = new JLabel();
        messageLabel.setHorizontalAlignment(0);
        messageLabel.setHorizontalTextPosition(0);
        messageLabel.setText("Chat messages");
        panel1.add(messageLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        channelsListLabel = new JLabel();
        channelsListLabel.setText("Channels list");
        panel1.add(channelsListLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(500, -1), null, 0, false));
        channelsTextArea = new JTextArea();
        channelsTextArea.setText("");
        scrollPane1.setViewportView(channelsTextArea);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel2, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        refreshChannelsButton = new JButton();
        refreshChannelsButton.setText("Get Channels");
        panel2.add(refreshChannelsButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, -1), null, new Dimension(150, 150), 0, false));
        disconnectButton = new JButton();
        disconnectButton.setText("Disconnect");
        panel2.add(disconnectButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, -1), null, new Dimension(150, 150), 0, false));
        sendButton = new JButton();
        sendButton.setText("Send");
        panel2.add(sendButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, -1), null, new Dimension(150, 150), 0, false));
        quitButton = new JButton();
        quitButton.setText("Quit");
        panel2.add(quitButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(130, -1), null, new Dimension(150, 150), 0, false));
        InputField = new JPanel();
        InputField.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(InputField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        InputField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        textInput = new JTextField();
        InputField.add(textInput, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(600, -1), null, 0, false));
        inputLabel = new JLabel();
        inputLabel.setText("Input");
        InputField.add(inputLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        serverStateLabel = new JLabel();
        serverStateLabel.setText("Server is online");
        mainPane.add(serverStateLabel, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPane.add(spacer1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPane.add(spacer2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }
}