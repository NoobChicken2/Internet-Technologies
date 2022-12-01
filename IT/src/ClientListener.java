import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientListener implements Runnable {
    private Socket socket;
    public ClientListener(Socket socket) {
        this.socket=socket;
    }

    @Override
    public void run() {
        Scanner consoleInput = new Scanner(System.in);

        OutputStream output = null;

        try {
            output = this.socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PrintWriter clientWriter = new PrintWriter(output);
        clientWriter.println();//message
        clientWriter.flush();
    }
}
