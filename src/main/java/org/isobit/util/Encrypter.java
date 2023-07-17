package org.isobit.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Encrypter {

    public static String MD2 = "MD2";
    public static String MD5 = "MD5";
    public static String SHA1 = "SHA1";
    public static String SHA256 = "SHA-256";
    public static String SHA384 = "SHA-384";
    public static String SHA512 = "SHA-512";
    public static String PEGASUS = "PEGASUS";
    private static String keySecret = "secret";

    public static void main(String[] args) {
        System.out.println(Encrypter.decode("0xa40096009300aa009b00ab00".getBytes()));
    }

    public static String decode(byte[] pass) {
        ArrayList<Character> l = new ArrayList();
        int d;
        for (int i = 0, p = 0; i < pass.length; p++, i += 2) {
            if (p >= keySecret.length()) {
                p = 0;
            }
            d = pass[i] - keySecret.charAt(p) + 1;
            if (d < 0) {
                d += 255;
            }
            l.add((char) d);
        }
        return XUtil.implode(l, "");
    }

    public String encode(String algorithm, String value) {
        try{
        if (algorithm.equals(PEGASUS)) {
            String passPegasus = "";
            for (char c : value.toUpperCase().toCharArray()) {
                int i = (255 - (int) c);
//                    if(i>0&&i<224)
                passPegasus = (char) i + passPegasus;
            }
            return passPegasus;
        } else {
            return encode(algorithm, value.getBytes());
        }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static byte[] encode(String original) {
        int sourcePtr, keyPtr = 0, sourceLen, tempVal;
        sourceLen = original.length();
        ArrayList<Byte> l = new ArrayList();
        for (sourcePtr = 0; sourcePtr < sourceLen; sourcePtr++) {
            tempVal = original.charAt(sourcePtr) + keySecret.charAt(keyPtr++);
            while (tempVal > 255) {
                if (tempVal > 255) {
                    tempVal = tempVal - 255;
                }
            }
            l.add((byte) tempVal);
            l.add((byte) 0);
            if (keyPtr >= keySecret.length()) {
                keyPtr = 0;
            }
        }
        byte b[] = new byte[l.size()];
        for (int i = 0; i < l.size(); i++) {
            b[i] = l.get(i);
        }
        return b;
    }

    public String encode(String algorithm, byte[] value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.reset();
        md.update(value);
        return toHexadecimal(md.digest());
    }

    /**
     * *
     * Convierte un arreglo de bytes a String usando valores hexadecimales
     *
     * @param digest arreglo de bytes a convertir
     * @return String creado a partir de <code>digest</code>
     */
    private static String toHexadecimal(byte[] digest) {
        String hash = "";
        for (byte aux : digest) {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) {
                hash += "0";
            }
            hash += Integer.toHexString(b);
        }
        return hash;
    }
}
