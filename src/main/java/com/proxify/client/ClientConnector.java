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

    private final ClientDelegate clientDelegate;
    private final Server server;

    public ClientConnector(ClientDelegate assistantDelegate) {
        this.clientDelegate = assistantDelegate;
        server = new Server();
    }
    public boolean active = false;
    private String idPc = "";
    public boolean connect = true;
    public ClientDelegate clientConnector;
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
    public void conencClient(Integer serverPort, String ipServer,
            Integer _directPort, ClientDelegate _clientConnector) {

        clientConnector = _clientConnector;

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
        clientConnector.beforeLoopConnectServer();
        try {

            do {
                try {

                    try { // Try to connect to the server earch 10 seconds
                        Thread.sleep(10000);
                        if (active) {
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

                    active = false;

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

        new Thread(new DirectConnectorServer(clientDelegate,
                idPc, localPort)).start();

    }

    public void run() {

        idPc = DesktopSystem.getFixedId();

        startDirectConnectThread();

        connectToServer();

    }
}
