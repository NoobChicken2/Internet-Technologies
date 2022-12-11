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
            String[] response = serverResponse.split(" ");
            switch (response[0]){
                case "PING" ->{
                    ListenOutputStream.command = "PONG";
                }case "OK Goodbye" ->{
                    return;
                }case "INIT" ->{
                    System.out.println(serverResponse);
                }case "JOINED" ->{
                    System.out.println(response[1]+" joined the chat!");
                }case "DISCONNECTED" ->{
                    System.out.println(response[1]+" left the chat :(");
                }case "OK" ->{
                    System.out.println("ME: "+response[2]);
                }case "BCST" ->{
                    System.out.println(response[1]+": "+response[2]);
                }case "LIST_RESPONSE" ->{
                    for (int i=1;i< response.length;i++){
                        System.out.println(response[i]);
                    }
                }case "PRV_BCST" ->{
                    System.out.println("PRIVATE< "+response[1]+": "+response[2]+" >");
                }case "FAIL01" ->{
                    System.out.println("User already logged in");
                }case "FAIL02" ->{
                    System.out.println("Username has an invalid format or length");
                }
            }
        }
    }
}