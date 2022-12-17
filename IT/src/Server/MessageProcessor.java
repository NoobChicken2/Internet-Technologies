package Server;

import Server.ServerResponse.*;

import java.io.*;
import java.net.Socket;

public class MessageProcessor implements Runnable{
    protected Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    protected String name;
    protected boolean exit=false;

    public MessageProcessor(Socket socket, InputStream inputStream, OutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public MessageProcessor() {
    }

    @Override
    public void run() {
        sendMessage("INIT Yani/Klaus server!");
        ServerResponseManager responseManager=new ServerResponseManager();
        while (!exit) {
            try {
                // getting response from client
                BufferedReader readMessage = new BufferedReader(new InputStreamReader(inputStream));
                String receivedString=readMessage.readLine();

                if (receivedString==null){
                    sendMessage("FAIL00 Unkown command");
                }else{
                    String[] response = receivedString.split(" ");
                    switch (response[0]){
                        case "IDENT" ->{
                            responseManager.setServerResponse(new ServerResponseLogin(this));
                            responseManager.respond(receivedString);
                        }
                        case "QUIT" ->{
                            responseManager.setServerResponse(new ServerResponseQuit());
                            responseManager.respond(receivedString);
                        }
                        case "BCST" ->{
                            responseManager.setServerResponse(new ServerResponseBroadcast());
                            responseManager.respond(receivedString);
                        }
                        case "LIST_REQUEST" ->{
                            responseManager.setServerResponse(new ServerResponseListRequest());
                            responseManager.respond(receivedString);
                        }
                        case "PRV_BCST" ->{
                            responseManager.setServerResponse(new ServerResponsePrivateMessage());
                            responseManager.respond(receivedString);
                        }
                        default -> {
                            sendMessage("FAIL00 Unkown command");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    protected boolean checkClientLoggedIn(){
        if (this.name==null) {
            sendMessage("FAIL03 Please log in first");
            return false;
        }
        return true;
    }
    public void sendMessage(String message) {
        PrintWriter sendMessage = new PrintWriter(outputStream);
        sendMessage.println(message);
        sendMessage.flush();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
