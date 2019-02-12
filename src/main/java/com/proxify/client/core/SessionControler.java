package com.proxify.client.core;

import com.proxify.client.DirectConnectorServer;
import com.proxify.util.DesktopSystem;
import com.proxify.util.Server;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Manage all events of remote connection with the server
 * @author sam
 */
public class SessionControler
        implements Runnable {

    public static boolean active = false;
    static private String idPc = "";
    public static boolean connect = true;
    public static ClientDelegate clientConnector;
    private static SessionControler sessionControler = null;
    private Integer serverPort;
    public Integer localPort;
    private String ipServer;
    
    private Server server;

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
            Integer _directPort, ClientDelegate _clientConnector) {

        sessionControler = new SessionControler();

        clientConnector = _clientConnector;

        sessionControler.localPort = _directPort;

        sessionControler.serverPort = serverPort;
        sessionControler.ipServer = ipServer;

        Thread thread = new Thread(sessionControler);

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

                    Socket socket = server.getServerSocket(ipServer, serverPort);

                    clientConnector.afterConnectServer(socket);

                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();

                    clientConnector.setInputStream(is);
                    clientConnector.setOutputStream(os);

                    //Send id
                    os.write(("id:" + getIdPc() + "\n").getBytes());

                    BufferedReader in = new BufferedReader(new InputStreamReader(is));

                    String line = in.readLine();
                    
                    System.out.println(line);
                    
                    clientConnector.getConnectCode(line);

                    line = in.readLine();
                    if ("gogogo".equals(line)) {
                        clientConnector.startLink(socket);
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
