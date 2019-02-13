package com.proxify.client.core;

import com.proxify.client.ClientDelegate;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerDirect implements Runnable {

    private final Socket inputSocket;
    private final ClientDelegate clientDelegate;
    private final String idPc;

    ServerDirect(ClientDelegate clientDelegate, String idPc, Socket socket) {
        this.inputSocket = socket;
        this.clientDelegate = clientDelegate;
        this.idPc = idPc;
    }

    public void run() {
        try {

            InputStream is = inputSocket.getInputStream();
            OutputStream os = inputSocket.getOutputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            PrintWriter out = new PrintWriter(os, true);

            String line = in.readLine();

            if (line.startsWith("check:")) {
                String idRecebido = line.replaceFirst("check:", "");

                if (idPc.equals(idRecebido)) {

                    out.println("ok");

                } else {

                    out.println("ko");

                }

                out.flush();

                try {
                    inputSocket.close();
                } catch (Exception exSocket) {
                }

            } else if (line.startsWith("connect:")) {

                out.println("autenticado!");
                out.flush();

                clientDelegate.setInputStream(is);
                clientDelegate.setOutputStream(os);

                System.out.print(inputSocket.isConnected());

                clientDelegate.startLink(inputSocket);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
