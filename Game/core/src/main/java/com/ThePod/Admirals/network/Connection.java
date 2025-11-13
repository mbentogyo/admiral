package com.ThePod.Admirals.network;

import com.ThePod.Admirals.exception.AdmiralsException;
import com.ThePod.Admirals.network.callback.ConnectionCallback;
import com.ThePod.Admirals.network.callback.DataCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Connection {
    protected static final int PORT = 6769;
    protected static String ip;

    protected Socket socket;
    protected ConnectionCallback connectionCallback;
    protected PrintWriter out;
    protected BufferedReader in;
    protected Thread readThread;

    public Connection(ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    public static String getCurrentIP() {
        if (ip != null) return ip;

        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress("8.8.8.8", 53));
            return s.getLocalAddress().getHostAddress();
        } catch (IOException e){
            return "N/A";
        }
    }

    public void sendData(String s) {
        if (out != null) out.println(s);
    }

    public void setOnDataReceive(DataCallback<String> listener) {
        readThread = new Thread(() -> {
            try {
                String data;
                while ((data = in.readLine()) != null) {
                    listener.onReceive(data);
                    if ("ABORT".equals(data)) {
                        stop();
                        break;
                    }
                }
            } catch (IOException e) {
                listener.onReceive("ABORT");
                connectionCallback.onDisconnect(AdmiralsException.INTERNAL_ERROR);
            }
        });
        // Thread will be started after `in` is initialized in start()
    }

    abstract public void start();

    public void stop() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            connectionCallback.onDisconnect(null);
        } catch (IOException e) {
            connectionCallback.onDisconnect(AdmiralsException.INTERNAL_ERROR);
        }
    }
}
