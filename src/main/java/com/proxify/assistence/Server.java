/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxify.assistence;


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
    static public final int portaApoio = 11234;
    static public InputStream is;
    static public OutputStream os;
    static private String tipoProxy = "";
    static private String tipoPortaProxy = "";
    static private String hostProxy;
    static private String portaProxy;
    static private String proxyUser;
    static private String proxyPassword;
    static private Boolean useProxy;
    static private Boolean automaticoSistema = true;
    private static Socket socket;
    static private int tipoProxyInt = 0;
    public static final int HTTP = 0;
    public static final int HTTPS = 1;
    public static final int SOCKET = 3;
    public static final int DIRETO = 4;

    public static void configurarDireto(String hostProxy, String portaProxy, String usuarioProxy, String senhaProxy) {

        automaticoSistema = false;
        System.getProperties().remove(tipoProxy);
        System.getProperties().remove(tipoPortaProxy);

        configurarProxy();

    }

    public static void configurarProxy(String _hostProxy, String _portaProxy, String _usuarioProxy, String _senhaProxy) {

        useProxy = true;
        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

    }

    public static void configurarProxy(Integer tipo, String _hostProxy, String _portaProxy, String _usuarioProxy, String _senhaProxy) {


        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;


        if (tipo == null) {
            automaticoSistema = true;
        } else {
            automaticoSistema = false;
            tipoProxyInt = tipo;
        }

        Preferences preferenciasCliente = Preferences.userRoot();

        preferenciasCliente.putByteArray("hostProxy", hostProxy.getBytes());
        preferenciasCliente.putByteArray("portaProxy", portaProxy.getBytes());
        preferenciasCliente.putByteArray("usuarioProxy", proxyUser.getBytes());
        preferenciasCliente.putByteArray("senhaProxy", proxyPassword.getBytes());
        preferenciasCliente.putLong("tipo", tipo);

    }

    public static void configurarProxyHttps(String _hostProxy, String _portaProxy, String _usuarioProxy, String _senhaProxy) {
        automaticoSistema = false;
        System.getProperties().remove(tipoProxy);
        System.getProperties().remove(tipoPortaProxy);

        useProxy = true;
        tipoProxy = "https.proxyHost";
        tipoPortaProxy = "https.proxyPort";
        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

        configurarProxy();

    }

    public static void configurarProxyHttp(String _hostProxy, String _portaProxy, String _usuarioProxy, String _senhaProxy) {
        automaticoSistema = false;
        System.getProperties().remove(tipoProxy);
        System.getProperties().remove(tipoPortaProxy);

        useProxy = true;

        int tipo = HTTP;

        configurarProxy();
    }

    public static void configurarProxyPadrao() {
        automaticoSistema = true;
        System.getProperties().remove(tipoProxy);
        System.getProperties().remove(tipoPortaProxy);
        useProxy = false;

        configurarProxy();
    }

    public static void configurarProxySocks(String _hostProxy, String _portaProxy, String _usuarioProxy, String _senhaProxy) {
        automaticoSistema = false;
        if (tipoPortaProxy != null) {
            System.getProperties().remove(tipoProxy);
            System.getProperties().remove(tipoPortaProxy);
        }
        useProxy = true;
        tipoProxy = "socksProxyHost";
        tipoPortaProxy = "socksProxyPort";
        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        proxyUser = _usuarioProxy;
        proxyPassword = _senhaProxy;

        configurarProxy();

    }

    private static void configurarProxy() {

        if (useProxy) {

            System.setProperty(tipoProxy, hostProxy);
            System.setProperty(tipoPortaProxy, portaProxy);

        } else {
            System.getProperties().remove("http.nonProxyHosts");
        }


    }

    public static void conenct(String codigo, String portaAssistenteString, String ipAssistenteString, String portaEsperaString) throws AssistanceExcepition, IOException, Exception {
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
            VncViewer.getVncViewer().initVncViwer();
        } else if (line.startsWith("ok:true")) {
//                lbStatus.setText("Iniciando conexão direta");

            socket.close();

            String lineArray[] = line.split(":");

            String cliente = lineArray[2];

            System.out.println(cliente + " " + portaEsperaClient);

            socket = new Socket(cliente, portaEsperaClient);

        }

    }

    public static void connectToClient(String clientAddress, 
            int clientPort, String codigo) throws ErrException, IOException {
        BufferedReader in;
        String line;
        socket = new Socket(clientAddress, clientPort);
        is = socket.getInputStream();
        os = socket.getOutputStream();
        os.write(("connect:" + codigo + "\n").getBytes());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        line = in.readLine();
        if ("autenticado!".equals(line)) {
            
            VncViewer.getVncViewer().initVncViwer();
            
        } else {
            throw new ErrException();
        }
    }

    public static Socket obterSocket(String address, int porta) throws Exception {
        Socket retorno = null;

        Preferences preferenciasCliente = Preferences.userRoot();



        tipoProxyInt = preferenciasCliente.getInt("tipo", -1);

        if (tipoProxyInt == -1) {
            automaticoSistema = true;
        } else {
            automaticoSistema = false;
            hostProxy = new String(preferenciasCliente.getByteArray("hostProxy", null));
            portaProxy = new String(preferenciasCliente.getByteArray("portaProxy", null));
            proxyUser = new String(preferenciasCliente.getByteArray("usuarioProxy", null));
            proxyPassword = new String(preferenciasCliente.getByteArray("senhaProxy", null));
        }

        if (automaticoSistema) {
            String url = "http://" + address + ":" + porta + "/";
            List<java.net.Proxy> proxys = ProxySelector.getDefault().select(new URI(url));

            useProxy = false;

            for (Proxy proxy : proxys) {

                System.out.println("proxy hostname : " + proxy.type());

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
            switch (tipoProxyInt) {
                case HTTP:
                    retorno = getSocketHttp(address, porta);
                    break;
                case HTTPS:
                    System.out.println("2");
                    break;
                case SOCKET:
                    System.out.println("3");
                    break;
                case DIRETO:
                    retorno = new Socket();
                    InetSocketAddress dest = new InetSocketAddress(address, porta);
                    retorno.connect(dest);
                    break;
                default:
                    System.out.println("Valor diferente de 1, 2 e 3");
                    break;
            }
        }

        if (retorno == null) {
            throw new AssistanceExcepition();
        }

        return retorno;
    }

    public static Socket getSocketHttp(String address, int porta) throws IOException {
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
