package com.proxify.client.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.proxify.client.ClientDelegate;

/**
 *
 * @author sam
 */
public class DirectConnectorServer implements Runnable {

    private final ClientDelegate clientDelegate;
    private final int localPort;
    private final String idPc;

    public DirectConnectorServer(ClientDelegate clientDelegate, String idPc,
            int localPort) {
        this.clientDelegate = clientDelegate;
        this.localPort = localPort;
        this.idPc = idPc;
    }

    public void run() {
        try {

            ServerSocket socketServerClient = new ServerSocket(localPort);

            while (true) {

                Socket socket = socketServerClient.accept();

                new Thread(new ServerDirect(clientDelegate, idPc, socket)).start();

            }

        } catch (IOException ex) {
            Logger.getLogger(DirectConnectorServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
