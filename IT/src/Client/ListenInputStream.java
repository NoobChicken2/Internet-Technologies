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
                }case "QUIT_OK" ->{
                    try {
                        Client.socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;

                }case "INIT" ->{
                    System.out.println(serverResponse);
                }case "IDENT_OK" ->{
                    Client.hasLoggedIn=true;
                }case "JOINED" ->{
                    System.out.println(response[1]+" joined the chat!");
                }case "DISCONNECTED" ->{
                    System.out.println(response[1]+" left the chat :(");
                }case "BCST_OK" ->{
                    System.out.println("ME: "+response[1]);
                }case "BCST" ->{
                    System.out.println(response[1]+": "+response[2]);
                }case "LIST_RESPONSE" ->{
                    for (int i=1;i<response.length;i++){
                        System.out.println(response[i]);
                    }
                }case "PRV_BCST_OK" ->{
                    //todo private message sent do what?
                }case "PRV_BCST" ->{
                    System.out.println("PRIVATE< "+response[1]+": "+response[2]+" >");
                }case "SURVEY_OK" ->{
                    Client.survey=true;
                }case "SURVEY_Q_OK" ->{
                    Client.sendQuestion();
                }case "SURVEY_LIST" ->{
                    for (int i=1;i<response.length;i++){
                        System.out.println("Currently available people to join your survey");
                        System.out.println(i+". "+response[i]);
                    }
                }case "FAIL01" ->{
                    System.out.println("User already logged in");
                }case "FAIL02" ->{
                    System.out.println("Username has an invalid format or length");
                }case "FAIL03" ->{
                    System.out.println("Please log in first");
                }case "FAIL04" ->{
                    System.out.println("User cannot login twice");
                }case "FAIL05" ->{
                    System.out.println("Not enough users for survey");
                }case "FAIL00" ->{
                    System.out.println("invalid command");
                }
            }
        }
    }
}
