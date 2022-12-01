package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ListenInputStream implements Runnable {

    public static InputStream inputServer() {
        InputStream input = null;

        try {
            input = Client.socket.getInputStream();
            return input;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        BufferedReader serverReader = new BufferedReader(new InputStreamReader(inputServer()));

        while (true) {
            String serverResponse = null;
            try {
                serverResponse = serverReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //PING/PONG heartbeat
            if (serverResponse.equals("PING")){
                ListenOutputStream.command = "PONG";
            }

            //Ending the Connection
            if (serverResponse.equals("OK Goodbye")){
                return;
            }

            //Broadcasting messages
            String[] response = serverResponse.split(" ");
            if (serverResponse.startsWith("OK BCST")){
                System.out.println("ME: "+response[2]);
            }else if(response[0].equals("BCST")){
                System.out.println(response[1]+": "+response[2]);
            }

        }
    }
}
