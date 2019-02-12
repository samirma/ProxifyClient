package com.proxify.util;

import com.proxify.assistence.exception.AssistanceExcepition;
import com.proxify.assistence.exception.ErrException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    private String portProxy;
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

    public void setupDirectConnection(String hostProxy, String portaProxy,
            String usuarioProxy, String senhaProxy) {

        automaticSystem = false;
        System.getProperties().remove(typeProxy);
        System.getProperties().remove(typePortProxy);

        Server.this.setupProxy();

    }

    public void setupProxy(String _hostProxy, String _portaProxy,
            String _usuarioProxy, String _senhaProxy) {

        useProxy = true;
        hostProxy = _hostProxy;
        portProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

    }

    public void setupProxy(Integer type, String _hostProxy,
            String _portaProxy, String _usuarioProxy, String _senhaProxy) {

        hostProxy = _hostProxy;
        portProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

        if (type == null) {
            automaticSystem = true;
        } else {
            automaticSystem = false;
            typeProxyInt = type;
        }

        Preferences preferenciasCliente = Preferences.userRoot();

        preferenciasCliente.putByteArray("hostProxy", hostProxy.getBytes());
        preferenciasCliente.putByteArray("portaProxy", portProxy.getBytes());
        preferenciasCliente.putByteArray("usuarioProxy", proxyUser.getBytes());
        preferenciasCliente.putByteArray("senhaProxy", proxyPassword.getBytes());
        preferenciasCliente.putLong("tipo", type);

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
        portProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

        Server.this.setupProxy();

    }

    public void setupProxyHttp(String _hostProxy, String _portaProxy,
            String _usuarioProxy, String _senhaProxy) {
        automaticSystem = false;
        System.getProperties().remove(typeProxy);
        System.getProperties().remove(typePortProxy);

        useProxy = true;

        Server.this.setupProxy();
    }

    public void configurarProxyPadrao() {
        automaticSystem = true;
        System.getProperties().remove(typeProxy);
        System.getProperties().remove(typePortProxy);
        useProxy = false;

        Server.this.setupProxy();
    }

    public void configurarProxySocks(String _hostProxy, String _portaProxy,
            String _proxyUser, String _proxyPassword) {
        automaticSystem = false;
        if (typePortProxy != null) {
            System.getProperties().remove(typeProxy);
            System.getProperties().remove(typePortProxy);
        }
        useProxy = true;
        typeProxy = "socksProxyHost";
        typePortProxy = "socksProxyPort";
        hostProxy = _hostProxy;
        portProxy = _portaProxy;
        proxyUser = _proxyUser;
        proxyPassword = _proxyPassword;

        Server.this.setupProxy();

    }

    private void setupProxy() {

        if (useProxy) {

            System.setProperty(typeProxy, hostProxy);
            System.setProperty(typePortProxy, portProxy);

        } else {
            System.getProperties().remove("http.nonProxyHosts");
        }

    }


    public Socket connectToClient(String clientAddress,
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

            return socket;

        } else {
            throw new ErrException();
        }
    }

    public Socket getServerSocket(String address, int port) throws Exception {
        Socket retorno = null;

        Preferences preferenciasCliente = Preferences.userRoot();

        typeProxyInt = preferenciasCliente.getInt("tipo", -1);

        if (typeProxyInt == -1) {
            automaticSystem = true;
        } else {
            automaticSystem = false;
            hostProxy = new String(preferenciasCliente.getByteArray("hostProxy", null));
            portProxy = new String(preferenciasCliente.getByteArray("portaProxy", null));
            proxyUser = new String(preferenciasCliente.getByteArray("usuarioProxy", null));
            proxyPassword = new String(preferenciasCliente.getByteArray("senhaProxy", null));
        }

        if (automaticSystem) {
            String url = "http://" + address + ":" + port + "/";
            List<java.net.Proxy> proxys = ProxySelector.getDefault().select(new URI(url));

            useProxy = false;

            for (Proxy proxy : proxys) {

                System.out.println("Proxy hostname : " + proxy.type());

                InetSocketAddress addr = (InetSocketAddress) proxy.address();

                if (proxy.type().equals(Proxy.Type.DIRECT)) {
                    break;
                } else {
                    hostProxy = addr.getHostName();
                    portProxy = Integer.toString(addr.getPort());
                    useProxy = true;
                }
            }
            if (useProxy) {
                retorno = getSocketHttp(address, port);
            } else {
                retorno = new Socket();
                InetSocketAddress dest = new InetSocketAddress(address, port);
                retorno.connect(dest);
            }
        } else {
            switch (typeProxyInt) {
                case HTTP:
                    retorno = getSocketHttp(address, port);
                    break;
                case HTTPS:
                    System.out.println("2");
                    break;
                case SOCKET:
                    System.out.println("3");
                    break;
                case DIRECT:
                    retorno = new Socket();
                    InetSocketAddress dest = new InetSocketAddress(address, port);
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

    private Socket getSocketHttp(String address, int port) throws IOException {
        Socket result = null;
        ProxyHTTP proxyHTTP;
        if (proxyUser != null && proxyPassword != null) {
            proxyHTTP = new ProxyHTTP(hostProxy, new Integer(portProxy), proxyUser, proxyPassword);
        } else {
            proxyHTTP = new ProxyHTTP(hostProxy, new Integer(portProxy));
        }

        result = proxyHTTP.openSocket(address, port);

        return result;
    }
}
