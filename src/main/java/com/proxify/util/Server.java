/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxify.util;

import java.io.IOException;
import java.io.InputStream;
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
    static public final int portaApoio = 11234;
    public static String portaSaida;
    static public InputStream is;
    static public OutputStream os;
    static private String tipoProxy = "";
    static private String tipoPortaProxy = "";
    static private String hostProxy;
    static private String portaProxy;
    static private String usuarioProxy;
    static private String senhaProxy;
    static private Boolean useProxy;
    static private Boolean automaticoSistema = true;
    public static Socket socket;
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
        usuarioProxy = _usuarioProxy;
        senhaProxy = _senhaProxy;

    }

    public static void configurarProxy(Integer tipo, String _hostProxy, String _portaProxy, String _usuarioProxy, String _senhaProxy) {


        hostProxy = _hostProxy;
        portaProxy = _portaProxy;
        usuarioProxy = _usuarioProxy;
        senhaProxy = _senhaProxy;


        if (tipo == null) {
            automaticoSistema = true;
        } else {
            automaticoSistema = false;
            tipoProxyInt = tipo;
        }

        Preferences preferenciasCliente = Preferences.userRoot();

        preferenciasCliente.putByteArray("hostProxy", hostProxy.getBytes());
        preferenciasCliente.putByteArray("portaProxy", portaProxy.getBytes());
        preferenciasCliente.putByteArray("usuarioProxy", usuarioProxy.getBytes());
        preferenciasCliente.putByteArray("senhaProxy", senhaProxy.getBytes());
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
        usuarioProxy = _usuarioProxy;
        senhaProxy = _senhaProxy;

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
        usuarioProxy = _usuarioProxy;
        senhaProxy = _senhaProxy;

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
            usuarioProxy = new String(preferenciasCliente.getByteArray("usuarioProxy", null));
            senhaProxy = new String(preferenciasCliente.getByteArray("senhaProxy", null));
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
                retorno = obterHttp(address, porta);
            } else {
                retorno = new Socket();
                InetSocketAddress dest = new InetSocketAddress(address, porta);
                retorno.connect(dest);
            }
        } else {
            switch (tipoProxyInt) {
                case HTTP:
                    retorno = obterHttp(address, porta);
                    break;
                case HTTPS:
                    System.out.println("Valor é 2");
                    break;
                case SOCKET:
                    System.out.println("Valor é 3");
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


        return retorno;
    }

    public static Socket obterHttp(String address, int porta) throws IOException {
        Socket retorno = null;
        ProxyHTTP proxyHTTP;
        if (usuarioProxy != null && senhaProxy != null) {
            proxyHTTP = new ProxyHTTP(hostProxy, new Integer(portaProxy), usuarioProxy, senhaProxy);
        } else {
            proxyHTTP = new ProxyHTTP(hostProxy, new Integer(portaProxy));
        }

        retorno = proxyHTTP.openSocket(address, porta);

        return retorno;
    }
}
