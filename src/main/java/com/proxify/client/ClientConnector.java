package com.proxify.client;

import com.proxify.client.core.DirectConnectorServer;
import com.proxify.util.DesktopSystem;
import com.proxify.util.Server;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 *
 * @author samirantonio
 */
public class ClientConnector implements Runnable {

    private final Server server;

    public ClientConnector() {
        server = new Server();
    }
    private boolean active = false;
    private String idPc = "";
    private boolean connect = true;
    private ClientDelegate clientDelegate;
    private Integer serverPort;
    public Integer localPort;
    private String ipServer;

    /**
     * @return the idPc
     */
    public String getIdPc() {
        return idPc;
    }

    /**
     * @param aIdPc the idPc to set
     */
    public void setIdPc(String aIdPc) {
        idPc = aIdPc;
    }

    /**
     * Start a new connection to server
     *
     * @param i
     * @param _displayName
     * @throws NoSuchMethodException
     */
    public void connectClient(String ipServer, 
            Integer serverPort, Integer _directPort, 
            ClientDelegate _clientConnector) {

        clientDelegate = _clientConnector;

        this.localPort = _directPort;

        this.serverPort = serverPort;
        this.ipServer = ipServer;

        Thread thread = new Thread(this);

        thread.setName("SessionControler");

        thread.start();

    }

    /**
     * Start server handshake
     */
    private void connectToServer() {
        clientDelegate.beforeLoopConnectServer();
        try {

            do {
                try {
                    
                    clientDelegate.beforeConnectServer();

                    Socket socket = server.getServerSocket(ipServer, serverPort);

                    clientDelegate.afterConnectServer(socket);

                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();

                    clientDelegate.setInputStream(is);
                    clientDelegate.setOutputStream(os);

                    //Send id
                    os.write(("id:" + getIdPc() + "\n").getBytes());

                    BufferedReader in = new BufferedReader(new InputStreamReader(is));

                    String line = in.readLine();

                    System.out.println(line);

                    clientDelegate.getConnectCode(line);

                    line = in.readLine();
                    if ("gogogo".equals(line)) {
                        clientDelegate.startLink(socket);
                    }

                } catch (Exception e) {

                    active = false;

                    clientDelegate.closeConnection(e);

                }
                
                try { // Try to connect to the server earch 10 seconds
                    Thread.sleep(10000);
                } catch (Exception exception2) {
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
            clientDelegate.closeConnection();
        }

    }

    private void startDirectConnectThread() {

        new Thread(new DirectConnectorServer(clientDelegate,
                idPc, localPort)).start();

    }

    public void run() {

        idPc = DesktopSystem.getFixedId();

        startDirectConnectThread();

        connectToServer();

    }
}
