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
    private boolean exit=false;

    public MessageProcessor(Socket socket, InputStream inputStream, OutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        sendMessage("INIT Welcome to Yani/Klaus server!");
        while (!exit) {
            try {
                // getting response from client
                BufferedReader readMessage = new BufferedReader(new InputStreamReader(inputStream));
                String receivedString=readMessage.readLine();
                String[] response = receivedString.split(" ");

                switch (response[0]){
                    case "QUIT" ->{
                        String message = "OK GOODBYE";
                        sendMessage(message);
                        Server.broadcastMessage("DISCONNECTED "+this.name ,this.name);
                        Server.clients.remove(this.name);
                        exit=true;
                    }
                    case "IDENT" ->{
                        String clientName=response[1];
                        if (checkClientName(clientName)) {
                            Server.clients.put(clientName, this);
                            this.name = clientName;
                            String message = ":OK IDENT " + clientName;
                            sendMessage(message);
                            Server.broadcastMessage("JOINED " + clientName, this.name);
                        }
                    }
                    case "BCST" ->{
                        String message="OK BCST "+ response[1];
                        sendMessage(message);
                        Server.broadcastMessage("BCST "+this.name+" "+response[1], this.name);
                    }
                    case "LIST_REQUEST" ->{
                        String message= "LIST_RESPONSE";
                        message=message+Server.getClientList();
                        sendMessage(message);
                    }
                    case "PRV_BCST" ->{
                        String message= "PRV_BCST "+this.name+" "+response[2];
                        Server.privateMessage(response[1], message);
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
        Pattern pattern = Pattern.compile("([A-Za-z0-9\\-\\_]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(clientName);
        boolean matchFound = matcher.find();
        if (!matchFound){
            sendMessage("FAIL02 Username has an invalid format or length");
            return false;
        }
        return true;
    }
    public String getName() {
        return name;
    }
    public void sendMessage(String message) {
        PrintWriter sendMessage = new PrintWriter(outputStream);
        sendMessage.println(message);
        sendMessage.flush();
    }
}
