package GlobalUtilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
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
}
