import java.io.*;
import java.util.Scanner;

public class Client implements Runnable {

    @Override
    public void run() {
        Scanner consoleInput = new Scanner(System.in);

        OutputStream output = null;

        try {
            output = Main.socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PrintWriter clientWriter = new PrintWriter(output);

        while(true) {
            System.out.print("Write to the server: ");
            String line = consoleInput.nextLine();
            clientWriter.println(line);
            clientWriter.flush();
        }
    }
}
