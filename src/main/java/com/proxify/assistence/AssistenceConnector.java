package com.proxify.assistence;

import com.proxify.assistence.exception.AssistanceExcepition;
import com.proxify.assistence.exception.ClientNotFoundException;
import com.proxify.util.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author samirantonio
 */
public class AssistenceConnector {

    private final AssistanceClientDelegate assistantDelegate;
    private final Server server;
    private Socket socket;

    public AssistenceConnector(AssistanceClientDelegate assistantDelegate) {
        this.assistantDelegate = assistantDelegate;
        server = new Server();
    }

    public void conenctAdmin(String code, String serverPortString,
            String serverIpString, String clientLocalPortString)
            throws AssistanceExcepition, IOException, Exception {

        if ("".equals(code)) {
            return;
        }

        String address = serverIpString;
        int porta = 9980;
        int clientLocalPort = 5555;

        System.out.println(serverIpString + " : " + serverPortString);

        if (serverIpString != null && !"".equals(serverIpString)) {
            address = serverIpString;
        }

        if (serverPortString != null && !"".equals(serverPortString)) {
            porta = new Integer(serverPortString);
        }

        if (clientLocalPortString != null && !"".equals(clientLocalPortString)) {
            clientLocalPort = new Integer(clientLocalPortString);
        }

        System.out.println(address + " " + porta);

        socket = server.getServerSocket(address, porta);

        final InputStream is = socket.getInputStream();

        final OutputStream os = socket.getOutputStream();

        PrintWriter out = new PrintWriter(os, true);

        out.println(code);
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        String line = in.readLine();

        if ("erro".equals(line)) {
            throw new ClientNotFoundException();
        } else if ("ok:false".equals(line)) {

            assistantDelegate.startLink(socket);

        } else if (line.startsWith("ok:true")) {

            socket.close();

            String lineArray[] = line.split(":");

            String clientAddress = lineArray[2];

            System.out.println(clientAddress + " " + clientLocalPort);

            int clientPort = clientLocalPort;

            socket = server.connectToClient(clientAddress, clientPort, code);

            assistantDelegate.startLink(socket);

        }

    }

}
