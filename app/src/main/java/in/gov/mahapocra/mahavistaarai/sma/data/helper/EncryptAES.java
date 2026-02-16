package in.gov.mahapocra.mahavistaarai.sma.data.helper;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptAES {

    private static final String KEY = "ZXQ4Z3leuueoEeEpeFgMa57TCPB2EEW1"; // 32 chars
    private static final String IV = "QhZV5WctUQzgmYLt"; // 16 chars

    public static String encrypt(String plainText) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes("UTF-8"));
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // same as PHP AES-256-CBC
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);  // same as PHP base64_encode

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
