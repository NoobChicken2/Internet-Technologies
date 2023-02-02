package Server.Utils;

public class ServerUtils {

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
}
