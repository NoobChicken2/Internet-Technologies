import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static Socket socket;
    // Connecting to the server socket
    static {
        try {
            socket = new Socket("127.0.0.1", 1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Client userClient = new Client();
    static Server server = new Server();

    // The User Thread
    static Thread listenUser = new Thread(userClient, "User");
    // The Server Thread
    static Thread listenServer = new Thread(server, "Server");

    public static void main(String[] args) throws InterruptedException {

        Scanner consoleInput = new Scanner(System.in);

        listenUser.start();
        listenServer.start();
        listenUser.join();
        listenServer.join();
    }
}
