/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ConfigureProxy.java
 *
 * Created on 18/04/2010, 20:14:34
 */
package com.proxify.util;

import java.util.prefs.Preferences;

/**
 *
 * @author sam
 */
public class ConfigureProxy extends javax.swing.JFrame {

    static private ConfigureProxy inatancia;
    
    static private Server server;

    public static ConfigureProxy getConfigureProxy() {
        if (inatancia == null) {
            inatancia = new ConfigureProxy();
        }

        inatancia.setVisible(true);

        preencherDadosTela();

        return inatancia;
    }

    private static void preencherDadosTela() {

        Preferences preferenciasCliente = Preferences.userRoot();

        String hostProxy = new String(preferenciasCliente.getByteArray("hostProxy", "".getBytes()));
        String portaProxy = new String(preferenciasCliente.getByteArray("portaProxy", "".getBytes()));
        String usuarioProxy = new String(preferenciasCliente.getByteArray("usuarioProxy", "".getBytes()));
        String senhaProxy = new String(preferenciasCliente.getByteArray("senhaProxy", "".getBytes()));
        int tipoProxyInt = preferenciasCliente.getInt("tipo", -1);

        inatancia.txtServer.setText(hostProxy);
        inatancia.txtPorta.setText(portaProxy);
        inatancia.txtUsuario.setText(usuarioProxy);
        inatancia.txtPasswd.setText(senhaProxy);

            switch (tipoProxyInt) {
                case Server.HTTP:
                    inatancia.rbHttp.setSelected(true);
                    break;
                case Server.HTTPS:
                    inatancia.rbHttps.setSelected(true);
                    break;
                case Server.SOCKET:
                    inatancia.rbSocks.setSelected(true);
                    break;
                case Server.DIRECT:
                    inatancia.rbDireto.setSelected(true);
                    break;
                default:
                    System.out.println("Valor diferente de 1, 2 e 3");
                    break;
            }



    }

    private ConfigureProxy() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tipoProxy = new javax.swing.ButtonGroup();
        btSalvar = new javax.swing.JButton();
        rbDireto = new javax.swing.JRadioButton();
        rbHttp = new javax.swing.JRadioButton();
        rbHttps = new javax.swing.JRadioButton();
        rbSocks = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        txtPasswd = new javax.swing.JPasswordField();
        rbPadrao = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtServer = new javax.swing.JTextField();
        txtPorta = new javax.swing.JTextField();

        setTitle("Proxy");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btSalvar.setText("Salvar");
        btSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btSalvarActionPerformed(evt);
            }
        });
        getContentPane().add(btSalvar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 259, -1));

        tipoProxy.add(rbDireto);
        rbDireto.setText("Direto");
        rbDireto.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbDiretoStateChanged(evt);
            }
        });
        getContentPane().add(rbDireto, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, -1, -1));

        tipoProxy.add(rbHttp);
        rbHttp.setText("Http");
        rbHttp.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbHttpStateChanged(evt);
            }
        });
        getContentPane().add(rbHttp, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 68, -1, -1));

        tipoProxy.add(rbHttps);
        rbHttps.setText("Https");
        rbHttps.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbHttpsStateChanged(evt);
            }
        });
        getContentPane().add(rbHttps, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 40, -1, -1));

        tipoProxy.add(rbSocks);
        rbSocks.setText("Socks");
        rbSocks.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbSocksStateChanged(evt);
            }
        });
        getContentPane().add(rbSocks, new org.netbeans.lib.awtextra.AbsoluteConstraints(93, 68, -1, -1));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Autenticação (caso seja necessária)"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Usuário: ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 26, -1, -1));

        jLabel2.setText("Senha: ");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 59, -1, -1));
        jPanel1.add(txtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 21, 154, -1));
        jPanel1.add(txtPasswd, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 54, 154, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 201, 260, 93));

        tipoProxy.add(rbPadrao);
        rbPadrao.setSelected(true);
        rbPadrao.setText("Configuração padrão do seu sistema");
        rbPadrao.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rbPadraoStateChanged(evt);
            }
        });
        getContentPane().add(rbPadrao, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, -1, -1));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Dados do proxy"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setText("Servidor:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 26, -1, -1));

        jLabel4.setText("Porta:");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 59, -1, -1));
        jPanel2.add(txtServer, new org.netbeans.lib.awtextra.AbsoluteConstraints(87, 21, 154, -1));
        jPanel2.add(txtPorta, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 150, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 96, 260, 100));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btSalvarActionPerformed

        String hostProxy = txtServer.getText();
        String portaProxy = txtPorta.getText();
        String usuarioProxy = txtUsuario.getText();
        String senhaProxy = new String(txtPasswd.getPassword());



        if (rbDireto.isSelected()) {
            server.configurarProxy(Server.DIRECT, hostProxy, portaProxy, usuarioProxy, senhaProxy);
        } else if (rbPadrao.isSelected()) {
            server.configurarProxy(null, hostProxy, portaProxy, usuarioProxy, senhaProxy);
        } else if (rbHttp.isSelected()) {
            server.configurarProxy(Server.HTTP, hostProxy, portaProxy, usuarioProxy, senhaProxy);
        } else if (rbSocks.isSelected()) {
            server.configurarProxy(Server.SOCKET, hostProxy, portaProxy, usuarioProxy, senhaProxy);
        }

        this.setVisible(false);

    }//GEN-LAST:event_btSalvarActionPerformed

    private void rbPadraoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbPadraoStateChanged

        server.configurarProxyPadrao();

    }//GEN-LAST:event_rbPadraoStateChanged

    private void rbSocksStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbSocksStateChanged

        String hostProxy = txtServer.getText();
        String portaProxy = txtPorta.getText();
        String usuarioProxy = txtUsuario.getText();
        String senhaProxy = new String(txtPasswd.getPassword());

//        Servidor.configurarProxySocks(hostProxy, portaProxy, usuarioProxy, senhaProxy);

    }//GEN-LAST:event_rbSocksStateChanged

    private void rbHttpStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbHttpStateChanged
        String hostProxy = txtServer.getText();
        String portaProxy = txtPorta.getText();
        String usuarioProxy = txtUsuario.getText();
        String senhaProxy = new String(txtPasswd.getPassword());

//        Servidor.setupProxyHttp(hostProxy, portaProxy, usuarioProxy, senhaProxy);
    }//GEN-LAST:event_rbHttpStateChanged

    private void rbHttpsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbHttpsStateChanged
        String hostProxy = txtServer.getText();
        String portaProxy = txtPorta.getText();
        String usuarioProxy = txtUsuario.getText();
        String senhaProxy = new String(txtPasswd.getPassword());

//        Servidor.configurarProxyHttps(hostProxy, portaProxy, usuarioProxy, senhaProxy);
    }//GEN-LAST:event_rbHttpsStateChanged

    private void rbDiretoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rbDiretoStateChanged
        String hostProxy = txtServer.getText();
        String portaProxy = txtPorta.getText();
        String usuarioProxy = txtUsuario.getText();
        String senhaProxy = new String(txtPasswd.getPassword());

//        Servidor.setupDirectConnection(hostProxy, portaProxy, usuarioProxy, senhaProxy);
    }//GEN-LAST:event_rbDiretoStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton rbDireto;
    private javax.swing.JRadioButton rbHttp;
    private javax.swing.JRadioButton rbHttps;
    private javax.swing.JRadioButton rbPadrao;
    private javax.swing.JRadioButton rbSocks;
    private javax.swing.ButtonGroup tipoProxy;
    private javax.swing.JPasswordField txtPasswd;
    private javax.swing.JTextField txtPorta;
    private javax.swing.JTextField txtServer;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
