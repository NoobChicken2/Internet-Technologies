package Server;

import java.io.*;
import java.net.Socket;

public class MessageProcessor implements Runnable{
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    protected String receivedString;
    protected String stringToReturn;

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
                        //TODO BROADCAST TO EVERYONE THAT CLIENT HAS DISCONNECTED
                        sendMessage(stringToReturn);
//                        Server.broadcast();
                        stringToReturn="";
                        this.socket.close();
                    }
                    case "IDENT" ->{
                        String clientName=response[1];
                        User newUser=new User(clientName,this);
                        Server.clients.put(clientName,newUser);
                        stringToReturn=":OK IDENT "+ clientName;
                        Server.broadcast("JOINED "+ clientName, newUser);
                        sendMessage(stringToReturn);
                        stringToReturn="";
                    }
                    case "BCST" ->{
                        String message= response[1];
                        //Server.broadcast(message );
                        stringToReturn="OK BCST "+ message;
                        sendMessage(stringToReturn);
                        stringToReturn="";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        PrintWriter sendMessage = new PrintWriter(outputStream);
        sendMessage.println(message);
        sendMessage.flush();
    }
}
