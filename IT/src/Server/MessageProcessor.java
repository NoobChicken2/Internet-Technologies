package Server;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageProcessor implements Runnable{
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String name;

    public MessageProcessor(Socket socket, InputStream inputStream, OutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        boolean exit=false;
        sendMessage("INIT Yani/Klaus server!");//todo first print init before asking for username
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
                            if (response.length<2){//check if name is null
                                sendMessage("FAIL02 Username has an invalid format or length");
                            }else {
                                if (checkClientName(response[1])) {
                                    register(response[1]);
                                }
                            }
                        }
                        case "QUIT" ->{
                            if (checkClientLoggedIn()) {
                                quit();
                                exit = true;
                            }
                        }
                        case "BCST" ->{
                            if (checkClientLoggedIn()) {
                                broadcast(response[1]);
                            }
                        }
                        case "LIST_REQUEST" ->{
                            if (checkClientLoggedIn()) {
                                getListOfClients();
                            }
                        }
                        case "PRV_BCST" ->{
                            if (checkClientLoggedIn()) {
                                sendPrivateMessage(response[1], response[2]);
                            }
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

    private boolean checkClientName(String clientName) {
        if (Server.clients.containsKey(clientName)){
            sendMessage("FAIL01 User already logged in");
            return false;
        }
        if (clientName.length()<3){
            sendMessage("FAIL02 Username has an invalid format or length");
            return false;
        }
        if (clientName.length()>=15){
            sendMessage("FAIL02 Username has an invalid format or length");
            return false;
        }
        for (int i = 0; i < clientName.length(); i++) {
            if ((Character.isLetterOrDigit(clientName.charAt(i)) == false)) {
                sendMessage("FAIL02 Username has an invalid format or length");
                return false;
            }
        }
        return true;
    }
    private boolean checkClientLoggedIn(){
        if (!this.name.equals(null)) {
            return true;
        }else {
            sendMessage("FAIL03 Please log in first");
            return false;
        }
    }
    private void quit(){
        String message = "QUIT_OK ";
        sendMessage(message);
        Server.broadcastMessage("DISCONNECTED " + this.name, this.name);
        Server.clients.remove(this.name);
    }
    private void register(String clientName){
        Server.clients.put(clientName, this);
        this.name = clientName;
        String message = "IDENT_OK " + clientName;
        sendMessage(message);
        Server.broadcastMessage("JOINED " + clientName, this.name);
    }
    private void broadcast(String BroadcastMessage){
        String message = "BCST_OK " + BroadcastMessage;
        sendMessage(message);
        Server.broadcastMessage("BCST " + this.name + " " + BroadcastMessage, this.name);

    }
    private void getListOfClients(){
        String message= "LIST_RESPONSE";
        message=message+Server.getClientList(this.name);
        sendMessage(message);
    }
    private void sendPrivateMessage(String receiver, String privateMessage) {
        sendMessage("PRV_BCST_OK "+ privateMessage);
        String message = "PRV_BCST " + this.name + " " + privateMessage;
        Server.privateMessage(receiver, message);
    }
    public void sendMessage(String message) {
        PrintWriter sendMessage = new PrintWriter(outputStream);
        sendMessage.println(message);
        sendMessage.flush();
    }
    public String getName() {
        return name;
    }
}
