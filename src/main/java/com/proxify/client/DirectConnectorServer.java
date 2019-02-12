package com.proxify.client;

import com.proxify.client.core.SessionControler;
import com.proxify.util.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sam
 */
public class DirectConnectorServer implements Runnable {

    SessionControler vnch = null;

    public DirectConnectorServer(SessionControler aThis) {
        vnch = aThis;
    }

    class ServerDirect implements Runnable {

        Socket inputSocket;

        public ServerDirect(Socket _inputSocket) {
            inputSocket = _inputSocket;
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

                    if (SessionControler.getIdPc().equals(idRecebido)) {

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

//                    if (line.endsWith(ClienteApplet.txtCodigo.getText())) {

                    out.println("autenticado!");
                    out.flush();


                    SessionControler.clientConnector.setInputStream(is);
                    SessionControler.clientConnector.setOutputStream(os);

                    System.out.print(inputSocket.isConnected());

                    SessionControler.clientConnector.startLink(inputSocket);


                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void run() {
        try {

            ServerSocket socketServerClient = new ServerSocket(vnch.localPort);

            while (true) {

                Socket socket = socketServerClient.accept();

                new Thread(new ServerDirect(socket)).start();

            }

        } catch (IOException ex) {
            Logger.getLogger(DirectConnectorServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
