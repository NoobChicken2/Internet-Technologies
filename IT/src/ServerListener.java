import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerListener implements Runnable {
    private Socket socket;
    public ServerListener(Socket socket) {
        this.socket=socket;
    }

    @Override
    public void run() {
        while (true) {
            // Getting the server Response
            InputStream input = null;
            try {
                input = this.socket.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(input));

            // Reading the server Response
            String serverResponse = null;
            try {
                serverResponse = serverReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Printing it out
            System.out.println();
            System.out.println(serverResponse);
        }
    }
}
