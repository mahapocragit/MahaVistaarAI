package in.co.appinventor.services_api.app_util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* renamed from: in.co.appinventor.services_api.app_util.AESEnDnHelper */
public class AESEnDnHelper {
    private static final AESEnDnHelper ourInstance = new AESEnDnHelper();

    public static AESEnDnHelper getInstance() {
        return ourInstance;
    }

    private AESEnDnHelper() {
    }

    public String encryptedString(byte[] encrypted) {
        try {
            return new String(encrypted, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return "";
    }

    public byte[] encrypted(String key, String data) {
        try {
            return encrypt("ABCDEF1234567890", key, data.getBytes());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    private byte[] encrypt(String ivStr, String keyStr, byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(ivStr.getBytes());
        byte[] ivBytes = md.digest();
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(keyStr.getBytes());
        return encrypt(ivBytes, sha.digest(), bytes);
    }

    private byte[] encrypt(byte[] ivBytes, byte[] keyBytes, byte[] bytes) throws Exception {
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, newKey, ivSpec);
        return cipher.doFinal(bytes);
    }

    public String decryptedString(String key, byte[] encrypted) {
        try {
            return new String(decrypt("ABCDEF1234567890", key, encrypted), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return "";
    }

    private byte[] decrypt(String ivStr, String keyStr, byte[] bytes) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(ivStr.getBytes());
        byte[] ivBytes = md.digest();
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(keyStr.getBytes());
        return decrypt(ivBytes, sha.digest(), bytes);
    }

    private byte[] decrypt(byte[] ivBytes, byte[] keyBytes, byte[] bytes) throws Exception {
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, newKey, ivSpec);
        return cipher.doFinal(bytes);
    }

    public String encryptStrAndToBase64(String ivStr, String keyStr, String enStr) throws Exception {
        return new String(Base64.encode(encrypt(keyStr, keyStr, enStr.getBytes("UTF-8")), 0), "UTF-8");
    }

    public String decryptStrAndFromBase64(String ivStr, String keyStr, String deStr) throws Exception {
        return new String(decrypt(keyStr, keyStr, Base64.decode(deStr.getBytes("UTF-8"), 0)), "UTF-8");
    }
}
