/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxify.client.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Interface to control the events
 * @author sam
 */
public interface ClientConnector {

    public void beforeConnectServer();

    /**
     * Execute before to start loop
     */
    public void beforeLoopConnectServer();

    public void afterConnectServer(Socket socket);

    /**
     * Recive code from server
     * @param line
     */
    public void getConnectCode(String line);

    public void closeConnection(Exception e);

    /**
     * Execute just before to connect to other machine
     * @param socket
     */
    public void startLink(Socket socket);

    public void closeConnection();

    public void setInputStream(InputStream is);

    public void setOutputStream(OutputStream os);

    public void close();
}
