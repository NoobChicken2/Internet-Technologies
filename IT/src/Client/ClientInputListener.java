package Client;

import java.io.*;

public class ClientInputListener implements Runnable {

    private String command = "";
    private final Client client;

    private final PrintWriter clientWriter;

    public ClientInputListener(Client client) {
        this.client = client;
        System.out.println(client.getClientSocket());
        this.clientWriter = new PrintWriter(sendToServer());
    }

    private OutputStream sendToServer() {
        OutputStream output;
        try {
            assert client != null;
            output = client.getClientSocket().getOutputStream();
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
                    client.setWaitingResponse(true);
                    command = "";
                }
            }
        }
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

//    public void sendMessage(){
//        if(!command.equals("")){
//            String[] line = command.split(" ");
//            switch (line[0]) {
//                case "IDENT", "BCST", "PONG", "QUIT", "LIST_REQUEST", "PRV_BCST", "SURVEY", "TRANSFER", "TRANSFER_RES" -> {
//                    clientWriter.println(command);
//                    clientWriter.flush();
//                    command = "";
//                }
//            }
//        }
//    }
}
