package Client.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientUtils {
    protected static final String initVector = "encryptionIntVec";
    public static int getUserInput() {
        Scanner scanner = new Scanner(System.in);
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.err.println("Entered value is not an integer");
            return -1;
        }
    }
    public static String getUserInputString() {
        Scanner scanner = new Scanner(System.in);
        try {
            return scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entered value is not an String");
            return "";
        }
    }
    // This function takes the split message of the user from response and returns a complete String
    public static String combinedMessage(int messageStart, String[] response) {
        String message = "";
        for (int i = messageStart; i < response.length; i++) {
            if (i != response.length - 1) {
                response[i] += " ";
            }
            message += response[i];
        }
        return message;
    }
    // Creates a md5 checksum of any file
    public static String checksum(String path) throws NoSuchAlgorithmException, IOException {
        File file = new File(path);
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        int bytesCount = 0;
        while ((bytesCount = fis.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, bytesCount);
        }

        fis.close();
        byte[] bytes = messageDigest.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b:bytes) {
            sb.append(Integer
                    .toString((b & 0xff) + 0x100, 16)
                    .substring(1));
        }

        return sb.toString();
    }
    public static PublicKey generatePkFromString(String PUBLIC_KEY_STRING){
        PublicKey publicKey=null;
        try{
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(PUBLIC_KEY_STRING));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            publicKey = keyFactory.generatePublic(keySpecPublic);
        }catch (Exception ignored){}
        return publicKey;
    }
    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
    public static String encrypt(String PlainText, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(PlainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public static String decrypt(String encryptedMessage, String key) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
