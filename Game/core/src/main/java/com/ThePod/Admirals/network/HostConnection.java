package com.ThePod.Admirals.network;

import com.ThePod.Admirals.exception.AdmiralsException;
import com.ThePod.Admirals.network.callback.ConnectionCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class HostConnection extends Connection {
    private ServerSocket serverSocket;

    public HostConnection(ConnectionCallback connectionCallback) {
        super(connectionCallback);
        start();
    }

    @Override
    public void start() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Waiting for client...");
                socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());

                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                connectionCallback.onConnect();

                if (readThread != null) readThread.start();

            } catch (IOException e) {
                connectionCallback.onDisconnect(AdmiralsException.INTERNAL_ERROR);
            }
        }).start();
    }

    @Override
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            super.stop();
        } catch (IOException e) {
            connectionCallback.onDisconnect(AdmiralsException.INTERNAL_ERROR);
        }
    }
}
