package com.proxify.core;

import java.net.Socket;

public interface ConnectorDelegate {
    
    /**
     * Execute just before to connect to other machine
     * @param socket
     */
    public void startLink(Socket socket);
    
}
