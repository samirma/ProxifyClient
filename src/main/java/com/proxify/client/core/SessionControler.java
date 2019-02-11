/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxify.client.core;

import com.proxify.client.DirectConnectorServer;
import com.proxify.util.DesktopSystem;
import com.proxify.util.Server;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * manage all events of remote connection with the server
 * @author sam
 */
public class SessionControler
        implements Runnable {

    public static boolean active = false;
    static private String idPc = "";
    public static boolean connect = true;
    public static ClientConnector clientConnector;
    private static SessionControler estancia = null;
    private Integer serverPort;
    public Integer localPort;
    private String ipServer;

    /**
     * @return the idPc
     */
    public static String getIdPc() {
        return idPc;
    }

    /**
     * @param aIdPc the idPc to set
     */
    public static void setIdPc(String aIdPc) {
        idPc = aIdPc;
    }

    private SessionControler() {
    }

    /**
     * Start a new connection to server
     * @param i
     * @param _displayName
     * @throws NoSuchMethodException
     */
    public static void startConenctionServer(Integer serverPort, String ipServer,
            Integer _directPort, ClientConnector _clientConnector) {

        estancia = new SessionControler();

        clientConnector = _clientConnector;

        estancia.localPort = _directPort;

        estancia.this.serverPort = serverPort;
        estancia.this.ipServer = ipServer;

        Thread thread = new Thread(estancia);

        thread.setName("SessionControler");

        thread.start();

    }

    /**
     * Start server handshake
     */
    private void connectToServer() {
        clientConnector.beforeLoopConnectServer();
        try {


            do {
                try {

                    try { // Try to connect to the server earch 10 seconds
                        Thread.sleep(10000);
                        if (SessionControler.active) {
                            continue;
                        }
                    } catch (Exception exception2) {
                    }
                    clientConnector.beforeConnectServer();

                    Server.socket = Server.obterSocket(ipServer, serverPort);

                    clientConnector.afterConnectServer(Server.socket);

                    InputStream is = Server.socket.getInputStream();
                    OutputStream os = Server.socket.getOutputStream();

                    clientConnector.setInputStream(is);
                    clientConnector.setOutputStream(os);

                    os.write(("id:" + getIdPc() + "\n").getBytes());

                    BufferedReader in = new BufferedReader(new InputStreamReader(is));

                    String line = in.readLine();
                    
                    System.out.println(line);
                    
                    clientConnector.getConnectCode(line);

                    line = in.readLine();
                    if ("gogogo".equals(line)) {
                        clientConnector.startLink(Server.socket);
                    }

                } catch (Exception e) {


                    SessionControler.active = false;

                    clientConnector.closeConnection(e);

                }

            } while (connect);

            System.out.println("Server connection suspended");
//                try {
//                    socket.close();
//                } catch (Exception exception3) {
//                }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            clientConnector.closeConnection();
        }


    }

    private void startDirectConnectThread() {

        new Thread(new DirectConnectorServer(this)).start();

    }

    public void run() {

        String name = DesktopSystem.getFixedId();

        setIdPc(name);

        startDirectConnectThread();

        connectToServer();

    }
}
