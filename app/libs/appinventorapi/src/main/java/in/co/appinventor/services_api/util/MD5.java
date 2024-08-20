package in.co.appinventor.services_api.util;

import java.security.MessageDigest;

/* renamed from: in.co.appinventor.services_api.util.MD5 */
public class MD5 {
    static final String HEXES = "0123456789abcdef";

    private static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        StringBuilder hex = new StringBuilder(raw.length * 2);
        for (byte b : raw) {
            hex.append(HEXES.charAt((b & 240) >> 4)).append(HEXES.charAt(b & 15));
        }
        return hex.toString();
    }

    public static String digest(String toMd5) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("SHA-256");
            md5.update(toMd5.getBytes());
            return getHex(md5.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
