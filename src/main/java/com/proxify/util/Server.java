/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxify.util;

import com.proxify.assistence.Assistence;
import com.proxify.assistence.exception.AssistanceExcepition;
import com.proxify.assistence.exception.ClientNotFoundException;
import com.proxify.assistence.exception.ErrException;
import com.proxify.util.ProxyHTTP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.util.List;
import java.util.prefs.Preferences;

/**
 *
 * @author sam
 */
public class Server {

    public final String HOST = "maincontrol.com.br";
    public final int portaApoio = 11234;
    public InputStream is;
    public OutputStream os;
    private String typeProxy = "";
    private String typePortProxy = "";
    private String hostProxy;
    private String portaProxy;
    private String proxyUser;
    private String proxyPassword;
    private Boolean useProxy;
    private Boolean automaticSystem = true;
    private Socket socket;
    private int typeProxyInt = 0;
    public static final int HTTP = 0;
    public static final int HTTPS = 1;
    public static final int SOCKET = 3;
    public static final int DIRECT = 4;

    private Assistence assistence;

    public void setupDirectConnection(String hostProxy, String portaProxy,
            String usuarioProxy, String senhaProxy) {

        automaticSystem = false;
        System.getProperties().remove(typeProxy);
        System.getProperties().remove(typePortProxy);

        setupProxy();

    }

    public void configurarProxy(String _hostProxy, String _portaProxy,
            String _usuarioProxy, String _senhaProxy) {

        useProxy = true;
        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

    }

    public void configurarProxy(Integer tipo, String _hostProxy,
            String _portaProxy, String _usuarioProxy, String _senhaProxy) {

        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

        if (tipo == null) {
            automaticSystem = true;
        } else {
            automaticSystem = false;
            typeProxyInt = tipo;
        }

        Preferences preferenciasCliente = Preferences.userRoot();

        preferenciasCliente.putByteArray("hostProxy", hostProxy.getBytes());
        preferenciasCliente.putByteArray("portaProxy", portaProxy.getBytes());
        preferenciasCliente.putByteArray("usuarioProxy", proxyUser.getBytes());
        preferenciasCliente.putByteArray("senhaProxy", proxyPassword.getBytes());
        preferenciasCliente.putLong("tipo", tipo);

    }

    public void configurarProxyHttps(String _hostProxy,
            String _portaProxy, String _usuarioProxy, String _senhaProxy) {
        automaticSystem = false;
        System.getProperties().remove(typeProxy);
        System.getProperties().remove(typePortProxy);

        useProxy = true;
        typeProxy = "https.proxyHost";
        typePortProxy = "https.proxyPort";
        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

        setupProxy();

    }

    public void setupProxyHttp(String _hostProxy, String _portaProxy,
            String _usuarioProxy, String _senhaProxy) {
        automaticSystem = false;
        System.getProperties().remove(typeProxy);
        System.getProperties().remove(typePortProxy);

        useProxy = true;

        setupProxy();
    }

    public void configurarProxyPadrao() {
        automaticSystem = true;
        System.getProperties().remove(typeProxy);
        System.getProperties().remove(typePortProxy);
        useProxy = false;

        setupProxy();
    }

    public void configurarProxySocks(String _hostProxy, String _portaProxy, String _usuarioProxy, String _senhaProxy) {
        automaticSystem = false;
        if (typePortProxy != null) {
            System.getProperties().remove(typeProxy);
            System.getProperties().remove(typePortProxy);
        }
        useProxy = true;
        typeProxy = "socksProxyHost";
        typePortProxy = "socksProxyPort";
        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

        setupProxy();

    }

    private void setupProxy() {

        if (useProxy) {

            System.setProperty(typeProxy, hostProxy);
            System.setProperty(typePortProxy, portaProxy);

        } else {
            System.getProperties().remove("http.nonProxyHosts");
        }

    }

    public void conenct(String codigo, String portaAssistenteString,
            String ipAssistenteString, String portaEsperaString)
            throws AssistanceExcepition, IOException, Exception {
        if ("".equals(codigo)) {
            return;
        }

        String address = ipAssistenteString;
        int porta = 9980;
        int portaEsperaClient = 5555;

        System.out.println(ipAssistenteString + " : " + portaAssistenteString);

        if (ipAssistenteString != null && !"".equals(ipAssistenteString)) {
            address = ipAssistenteString;
        }

        if (portaAssistenteString != null && !"".equals(portaAssistenteString)) {
            porta = new Integer(portaAssistenteString).intValue();
        }

        if (portaEsperaString != null && !"".equals(portaEsperaString)) {
            portaEsperaClient = new Integer(portaEsperaString).intValue();
        }

        System.out.println(address + " " + porta);

        socket = obterSocket(address, porta);

        is = socket.getInputStream();

        os = socket.getOutputStream();

        PrintWriter out = new PrintWriter(os, true);

        out.println(codigo);
        out.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        String line = in.readLine();

        if ("erro".equals(line)) {
            throw new ClientNotFoundException();
        } else if ("ok:false".equals(line)) {
            
            assistence.setConnectedProxy(socket);
            
        } else if (line.startsWith("ok:true")) {

            socket.close();

            String lineArray[] = line.split(":");

            String cliente = lineArray[2];

            System.out.println(cliente + " " + portaEsperaClient);

            socket = new Socket(cliente, portaEsperaClient);

        }

    }

    public void connectToClient(String clientAddress,
            int clientPort, String code) throws ErrException, IOException {
        BufferedReader in;
        String line;
        socket = new Socket(clientAddress, clientPort);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        os.write(("connect:" + code + "\n").getBytes());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        line = in.readLine();
        if ("autenticado!".equals(line)) {

            assistence.setConnectedProxy(socket);

        } else {
            throw new ErrException();
        }
    }

    public Socket obterSocket(String address, int porta) throws Exception {
        Socket retorno = null;

        Preferences preferenciasCliente = Preferences.userRoot();

        typeProxyInt = preferenciasCliente.getInt("tipo", -1);

        if (typeProxyInt == -1) {
            automaticSystem = true;
        } else {
            automaticSystem = false;
            hostProxy = new String(preferenciasCliente.getByteArray("hostProxy", null));
            portaProxy = new String(preferenciasCliente.getByteArray("portaProxy", null));
            proxyUser = new String(preferenciasCliente.getByteArray("usuarioProxy", null));
            proxyPassword = new String(preferenciasCliente.getByteArray("senhaProxy", null));
        }

        if (automaticSystem) {
            String url = "http://" + address + ":" + porta + "/";
            List<java.net.Proxy> proxys = ProxySelector.getDefault().select(new URI(url));

            useProxy = false;

            for (Proxy proxy : proxys) {

                System.out.println("Proxy hostname : " + proxy.type());

                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if (proxy.type().equals(Proxy.Type.DIRECT)) {
                    break;
                } else {
                    hostProxy = addr.getHostName();
                    portaProxy = Integer.toString(addr.getPort());
                    useProxy = true;
                }
            }
            if (useProxy) {
                retorno = getSocketHttp(address, porta);
            } else {
                retorno = new Socket();
                InetSocketAddress dest = new InetSocketAddress(address, porta);
                retorno.connect(dest);
            }
        } else {
            switch (typeProxyInt) {
                case HTTP:
                    retorno = getSocketHttp(address, porta);
                    break;
                case HTTPS:
                    System.out.println("2");
                    break;
                case SOCKET:
                    System.out.println("3");
                    break;
                case DIRECT:
                    retorno = new Socket();
                    InetSocketAddress dest = new InetSocketAddress(address, porta);
                    retorno.connect(dest);
                    break;
                default:
                    System.out.println("Value not found");
                    break;
            }
        }

        if (retorno == null) {
            throw new AssistanceExcepition();
        }

        return retorno;
    }

    public Socket getSocketHttp(String address, int porta) throws IOException {
        Socket retorno = null;
        ProxyHTTP proxyHTTP;
        if (proxyUser != null && proxyPassword != null) {
            proxyHTTP = new ProxyHTTP(hostProxy, new Integer(portaProxy), proxyUser, proxyPassword);
        } else {
            proxyHTTP = new ProxyHTTP(hostProxy, new Integer(portaProxy));
        }

        retorno = proxyHTTP.openSocket(address, porta);

        return retorno;
    }
}
