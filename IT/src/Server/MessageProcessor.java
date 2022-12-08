package Server;

import java.io.*;
import java.net.Socket;

public class MessageProcessor implements Runnable{
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    protected String receivedString;
    protected String stringToReturn;
    private String name;

    public MessageProcessor(Socket socket, InputStream inputStream, OutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        sendMessage("INIT Welcome to Yani/Klaus server!");
        while (true) {
            try {
                // getting answers from client
                BufferedReader readMessage = new BufferedReader(new InputStreamReader(inputStream));
                receivedString=readMessage.readLine();
                String[] response = receivedString.split(" ");

                switch (response[0]){
                    case "QUIT" ->{
                        stringToReturn = "OK GOODBYE";
                        sendMessage(stringToReturn);
                        Server.broadcast("DISCONNECTED "+this.name ,this.name);
                        stringToReturn="";
                        this.socket.close();
                    }
                    case "IDENT" ->{
                        String clientName=response[1];
                        Server.clients.put(clientName,this);
                        this.name=clientName;
                        stringToReturn=":OK IDENT "+ clientName;
                        sendMessage(stringToReturn);
                        Server.broadcast("JOINED "+ clientName, this.name);
                        stringToReturn="";
                    }
                    case "BCST" ->{
                        String message= response[1];
                        stringToReturn="OK BCST "+ message;
                        sendMessage(stringToReturn);
                        Server.broadcast("BCST "+this.name+" "+message, this.name);
                        stringToReturn="";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
