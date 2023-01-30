package Client;

import GlobalUtilities.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ServerListener implements Runnable {

    private final Client client;
    private final ClientInputListener clientInputListener;

    public ServerListener(Client client, ClientInputListener clientInputListener) {
        this.client = client;
        this.clientInputListener = clientInputListener;
    }


    @Override
    public void run() {
        BufferedReader serverReader = new BufferedReader(new InputStreamReader(inputServer()));

        while (true) {
            String serverResponse = "";
            try {
                serverResponse = serverReader.readLine();
                if (serverResponse == null) {
                    System.out.println("You have timed out!");
                    System.exit(0);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] response = serverResponse.split(" ");;
            switch (response[0]){
                case "PING" ->{
                    if (client.getPongAllowed()) {
                        clientInputListener.setCommand("PONG");
                    }
                }case "QUIT_OK" ->{
                    try {
                        client.getClientSocket().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }case "INIT" ->{
                    System.out.println(serverResponse);
                }case "IDENT_OK" ->{
                    client.setHasLoggedIn(true);
                }case "JOINED" ->{
                    System.out.println(response[1]+" joined the chat!");
                }case "DISCONNECTED" ->{
                    System.out.println(response[1]+" left the chat :(");
                }case "BCST_OK" ->{
                    System.out.println("ME: "+ Utils.combinedMessage(1, response));
                }case "BCST" ->{
                    System.out.println(response[1]+": "+ Utils.combinedMessage(2,response));
                }case "LIST_RESPONSE" ->{
                    for (int i=1;i<response.length;i++){
                        System.out.println(response[i]);
                    }
                }case "PRV_BCST_OK" ->{
                    //todo private message sent do what?
                }case "PRV_BCST" ->{
                    System.out.println("PRIVATE< "+response[1]+": "+ Utils.combinedMessage(2, response)+" >");
                }case "SURVEY_OK" ->{
                    client.setSurvey(true);
                }case "SURVEY_Q_OK" ->{
                    client.sendQuestion();
                }case "SURVEY_LIST" ->{
                    for (int i=1;i<response.length;i++){
                        System.out.println("Currently available people to join your survey");
                        System.out.println(i+". "+response[i]);
                    }
                }case "TRANSFER_OK" ->{
                    System.out.println("The transfer request has been sent!");
                }case "TRANSFER_REQ" -> {
                    System.out.println( "The user " + response[1] + " wants to transfer a file to you named " + response[2] + " with the size " + Utils.combinedMessage(3, response) + ". Enter 9 to accept and 0 to decline!");
                    client.setTransferRequest(true);
                    client.setLastTransferRequestUser(response[1]);
                }case "TRANSFER_DECLINED" -> {
                    System.out.println("The user has declined the file transfer");
                }case "TRANSFER_ACCEPTED" -> {
                    System.out.println("The user has accepted the file transfer. The upload will start shortly.");
                }case "FAIL01" ->{
                    System.out.println("User already logged in");
                }case "FAIL02" ->{
                    System.out.println("Username has an invalid format or length");
                }case "FAIL03" ->{
                    System.out.println("Please log in first");
                }case "FAIL04" -> {
                    System.out.println("User cannot login twice");
                }case "FAIL05" -> {
                    System.out.println("Pong sent without receiving a ping");
                }case "FAIL06", "FAIL10" ->{
                    System.out.println(Utils.combinedMessage(1, response));
                }case "FAIL00" ->{
                    System.out.println("invalid command");
                }
            }
            client.setWaitingResponse(false);
        }
    }
    private InputStream inputServer() {
        InputStream input;
        try {
            input = client.getClientSocket().getInputStream();
            return input;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
