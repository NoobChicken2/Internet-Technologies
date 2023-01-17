package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int SERVER_PORT = 8000;
    private static final int SERVER_PORT_FT = 8080;

    public static Map<String, MessageProcessor> clients=new HashMap();
    private static ServerSocket serverSocket;
    static {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static ServerSocket fileTransferSocket;

    public static void main(String[] args) throws IOException {
        while (true) {
            // Wait for an incoming client-connection request (blocking).
            Socket socket = serverSocket.accept();

            //Getting the input and output streams
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            //creating the new client
            MessageProcessor messageProcessor=new MessageProcessor(socket, inputStream, outputStream);
            Thread client = new Thread(messageProcessor);
            client.start();
            // TODO: Start a ping thread for each connecting client.
        }
    }
    public static void broadcastMessage(String message, String sendingClientName){
        for (MessageProcessor user : clients.values()) {
            if (!user.getName().equals(sendingClientName)){
                user.sendMessage(message);
            }
        }
    }
    // Gets the list of all clients connected except the client requesting the list
    public static String getClientList(String sendingClientName){
        String result="";
        for (MessageProcessor user : clients.values()) {
            if (!user.getName().equals(sendingClientName)) {
                result = result + user.getName() + " ";
            }
        }
        return result;
    }
    // Gets the list of all current clients connected
    public static String getClientList() {
        String result="";
        for (MessageProcessor user : clients.values()) {
            result += user.getName() + " ";
        }
        return result;
    }
    public static void messageClient(String client, String message){
        clients.get(client).sendMessage(message);
    }
}
