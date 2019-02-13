/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proxify.util;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 *
 * @author samir
 */
public class DesktopSystem {

    private final static String ROOT = "main/control/system/remote";
    private final static String ID_PC = "idPc";

    public static boolean registered = false;

    /**
     * Get Domain name to create a Id if error will use a user name
     *
     * @return
     */
    public static String getFixedId() {
        Preferences prefs = Preferences.userRoot().node(ROOT);

        String machineName = prefs.get(ID_PC, null);

        if (machineName == null) {
            try {
                machineName = InetAddress.getLocalHost().getHostName();
            } catch (Exception ex) {
                machineName = (System.getProperty("user.name"));
            }

            prefs.put(
                    ID_PC,
                    machineName
                    + "-"
                    + genMd5((new Date().getTime() + "-" + registered)
                            .getBytes()));
        }

        return machineName;
    }

    public static String genMd5(byte[] b) {
        String retorno = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(b);
            BigInteger hashBig = new BigInteger(1, bytes);
            retorno = hashBig.toString(16);
        } catch (NoSuchAlgorithmException ex) {
//            LOGGER.log(Level.SEVERE,
//                    null, ex);
        }
        return retorno;
    }

    public static String recoverMd5(String arquivo) {
        String retorno = null;
        try {

            FileInputStream file = new FileInputStream(arquivo);
            byte[] b;
            try (DataInputStream in = new DataInputStream(file)) {
                b = new byte[in.available()];
                in.readFully(b);
            }

            retorno = genMd5(b);

        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "err", e);
        }

        return retorno;

    }

}
