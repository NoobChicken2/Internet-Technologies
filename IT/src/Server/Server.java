package Server;

import Server.FileTransfer.FileTransferThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private ServerSocket serverSocket;
    private final int SERVER_PORT = 3000;
    private Map<String, MessageProcessor> clients;

    public Server() throws IOException {
        clients=new HashMap();
        serverSocket = new ServerSocket(SERVER_PORT);
    }

    public static void main(String[] args) throws IOException {
        new Server().run();
    }

    private void run() throws IOException {
        // This starts the file transfer thread which listens to file transfer connections
        new Thread(new FileTransferThread()).start();

        while (true) {
            // Wait for an incoming client-connection request (blocking).
            Socket socket = serverSocket.accept();

            //Getting the input and output streams
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            //creating the new client
            MessageProcessor messageProcessor=new MessageProcessor(this,socket,inputStream, outputStream);
            Thread client = new Thread(messageProcessor);
            client.start();
        }
    }

    public synchronized void broadcastMessageToEveryone(String message, String sendingClientName){
        for (MessageProcessor user : clients.values()) {
            if (!user.getName().equals(sendingClientName)){
                user.sendMessage(message);
            }
        }
    }
    public synchronized void broadcastMessageToListOfClients(String message, ArrayList<String>clientList){
        for (String user : clientList) {
            clients.get(user).sendMessage(message);
        }
    }
    // Gets the list of all clients connected except the client requesting the list
    public String getClientList(String sendingClientName){
        String result="";
        for (MessageProcessor user : clients.values()) {
            if (!user.getName().equals(sendingClientName)) {
                result = result + user.getName() + " ";
            }
        }
        return result;
    }
    public void messageClient(String client, String message){
        clients.get(client).sendMessage(message);
    }
    public int getClientsHashMapSize() {
        return clients.size();
    }
    public void addClient(String clientName, MessageProcessor mp) {
        clients.put(clientName, mp);
    }
    public void removeClient(String clientName) {
        clients.remove(clientName);
    }
    public boolean checkIfClientExists(String clientName) {
        if (clients.containsKey(clientName)){
            return true;
        }else {
            return false;
        }
    }
    public MessageProcessor getMessageProcessor(String name){
        return clients.get(name);
    }
}
