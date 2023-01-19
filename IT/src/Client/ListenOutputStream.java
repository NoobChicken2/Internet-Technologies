package Client;

import java.io.*;

public class ListenOutputStream implements Runnable {
    public static String command = "";

    private static OutputStream sendToServer() {
        OutputStream output = null;

        try {
            output = Client.getClientSocket().getOutputStream();
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
                case "IDENT", "BCST", "PONG", "QUIT", "LIST_REQUEST", "PRV_BCST", "SURVEY", "TRANSFER", "TRANSFER_RES" -> {
                    clientWriter.println(command);
                    clientWriter.flush();
                    command = "";
                }
            }
        }
    }
}
