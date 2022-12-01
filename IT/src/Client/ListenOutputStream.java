package Client;

import java.io.*;

public class ListenOutputStream implements Runnable {
    public static String command = "";

    private static OutputStream sendToServer() {
        OutputStream output = null;

        try {
            output = Client.socket.getOutputStream();
            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {

        PrintWriter clientWriter = new PrintWriter(sendToServer());

        while(true) {
            String[] line = command.split(" ");
            switch (line[0]) {
                case "IDENT", "BCST", "PONG", "QUIT" -> {
                    clientWriter.println(command);
                    clientWriter.flush();
                    command = "";
                }
            }
        }
    }
}
