package com.ThePod.Admirals.network;

import com.ThePod.Admirals.exception.AdmiralsException;
import com.ThePod.Admirals.network.callback.ConnectionCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection extends Connection {
    private final String ip;

    public ClientConnection(String ip, ConnectionCallback connectionCallback) {
        super(connectionCallback);
        this.ip = ip;
        start();
    }

    @Override
    public void start() {
        new Thread(() -> {
            try {
                socket = new Socket(ip, PORT);
                System.out.println("Connected to host: " + ip);

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                connectionCallback.onConnect();

                if (readThread != null) readThread.start();

            } catch (IOException e) {
                connectionCallback.onDisconnect(AdmiralsException.UNREACHABLE_SERVER);
            }
        }).start();
    }
}
