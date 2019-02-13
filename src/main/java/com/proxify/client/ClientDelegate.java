package com.proxify.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import com.proxify.core.ConnectorDelegate;

/**
 * Interface to control the events
 */
public interface ClientDelegate extends ConnectorDelegate {

    public void beforeConnectServer();

    /**
     * Execute before to start loop
     */
    public void beforeLoopConnectServer();

    public void afterConnectServer(Socket socket);

    /**
     * Recive code from server
     *
     * @param line
     */
    public void getConnectCode(String line);

    public void closeConnection(Exception e);

    public void closeConnection();

    public void setInputStream(InputStream is);

    public void setOutputStream(OutputStream os);

    public void close();
}
