package app.lifewin.utils;

import android.util.Base64;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtils {

    private static String algorithm = "AES";

    // Performs Encryption
    public static String encrypt(String plainText, String value) throws Exception {
        Key key = generateKey(value);
        Cipher chiper = Cipher.getInstance(algorithm);
        chiper.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = chiper.doFinal(plainText.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    // Performs decryption
    public static String decrypt(String encryptedText, String value) throws Exception {
        // generate key
        Key key = generateKey(value);
        Cipher chiper = Cipher.getInstance(algorithm);
        chiper.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] decValue = chiper.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    //generateKey() is used to generate a secret key for AES algorithm
    private static Key generateKey(String value) throws Exception {
        byte[] keyValue = value.getBytes(Charset.forName("UTF-8"));
        Key key = new SecretKeySpec(keyValue, algorithm);
        return key;
    }


    public static String newPassword() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32).substring(3,11);

    }

}
