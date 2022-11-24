import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static Socket socket;

    static {
        try {
            socket = new Socket("127.0.0.1", 1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner consoleInput = new Scanner(System.in);
        Client userClient = new Client();

        Thread listenUser = new Thread(userClient, "User");
        Thread listenServer = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {

                    InputStream input = null;
                    try {
                        input = socket.getInputStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    BufferedReader serverReader = new BufferedReader(new InputStreamReader(input));

                    String serverResponse = null;
                    try {
                        serverResponse = serverReader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(serverResponse);
                }
            }
        }, "Server");

        listenUser.start();
        listenServer.start();
        listenUser.join();
        listenServer.join();
    }
}
