package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int SERVER_PORT = 1337;

    protected static Map<String, User> clients=new HashMap();
    static ServerSocket serverSocket;
    static {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
    public static void broadcast(String message, User sendingClient){
        for (User user : clients.values()) {
            if (!user.getName().equals(sendingClient.getName())){
                user.messageProcessor.sendMessage(message);
            }
        }
    }

}
