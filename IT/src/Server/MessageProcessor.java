package Server;

import Client.ListenOutputStream;
import jdk.jshell.ImportSnippet;

import java.io.*;
import java.net.Socket;
import java.util.stream.Collectors;

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
        PrintWriter sendMessage = new PrintWriter(outputStream);
        BufferedReader readMessage = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            try {
                // getting answers from client
                receivedString=readMessage.readLine();

                //Ending the connection
                if(receivedString.startsWith("QUIT")) {
                    stringToReturn = "OK GOODBYE";
                    //TODO BROADCAST TO EVERYONE THAT CLIENT HAS DISCONNECTED
                    sendMessage.println(stringToReturn);
                    sendMessage.flush();
                    stringToReturn="";
                    this.socket.close();
                    break;
                }

                //Logging in
                if (receivedString.startsWith("IDENT")){
                    String[] response = receivedString.split(" ");
                    String clientName=response[1];
                    User newUser=new User(clientName,this);
                    Server.clients.put(clientName,newUser);
                    stringToReturn=": OK IDENT "+ clientName;
                    //TODO BROADCAST TO EVERYONE THAT SOMEONE HAS JOINED
                    Server.broadcast("JOINED "+ clientName);
                    sendMessage.println(stringToReturn);
                    sendMessage.flush();
                    stringToReturn="";
                }

                //another user logs in
                if (receivedString.startsWith("JOINED")){
                    receivedString=stringToReturn;
                    sendMessage.println();
                    sendMessage.flush();
                    receivedString="";


                }
                //Broadcasting messages
                if (receivedString.startsWith("BCST")){
                    String[] response = receivedString.split(" ");
                    String message= response[1];
                    //TODO BROADCAST MESSAGE TO EVERYONE MESSAGE
                    stringToReturn="OK BCST "+ message;
                    sendMessage.println(stringToReturn);
                    sendMessage.flush();
                    stringToReturn="";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // closing resources
            this.inputStream.close();
            this.outputStream.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
