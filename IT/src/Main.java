import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner consoleInput = new Scanner(System.in);
        Socket socket = new Socket("127.0.0.1", 1337);

        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        BufferedReader serverReader = new BufferedReader(new InputStreamReader(input));
        PrintWriter clientWriter = new PrintWriter(output);

        while(true) {
            String serverResponse = serverReader.readLine();
            System.out.println(serverResponse);

            System.out.print("Write to the server: ");
            String line = consoleInput.nextLine();
            clientWriter.println(line);
            clientWriter.flush();
        }
    }
}
