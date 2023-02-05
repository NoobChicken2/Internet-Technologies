package Client;

import Client.FileTransfer.FileTransferClientThread;
import Client.Utils.ClientUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

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
                    clientInputListener.setCommand("PONG");
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
                    client.setUsername(response[1]);
                }case "JOINED" ->{
                    System.out.println(response[1]+" joined the chat!");
                }case "DISCONNECTED" ->{
                    System.out.println(response[1]+" left the chat :(");
                }case "BCST_OK" ->{
                    System.out.println("ME: "+ ClientUtils.combinedMessage(1, response));
                }case "BCST" ->{
                    System.out.println(response[1]+": "+ ClientUtils.combinedMessage(2,response));
                }case "LIST_RESPONSE" ->{
                    for (int i=1;i<response.length;i++){
                        System.out.println(response[i]);
                    }
                }case "PRV_BCST_OK" ->{
                    System.out.println("PRIVATE< me: "+response[1]+" >");
                }case "PRV_BCST" ->{
                    System.out.println("PRIVATE< "+response[1]+": "+ ClientUtils.combinedMessage(2, response)+" >");
                }case "SURVEY_OK" ->{
                    client.setServerResponse(serverResponse);
                }case "SURVEY_Q_OK" ->{
                    //todo do what here?
                }case "SURVEY_LIST" ->{
                    System.out.println(serverResponse);
                    client.setServerResponse(serverResponse);
                }case "SURVEY_LIST_OK" ->{
                    client.setClientList(false);
                }case "SURVEY_EVENT" -> {
                    client.addEventCreator(response[1]);
                }case "SURVEY_EVENT Q" -> {
                    client.setServerResponse(serverResponse);
                }case "ENCRYPT_PUBLIC_OK" -> {
                    client.addPublicKey(response[1], response[2]);
                    System.out.println( "lololwdqd");
                }case "ENCRYPT_PUBLIC" -> {
                    client.getClientInputListener().setCommand("ENCRYPT PUBLIC_OK "+response[1]+" "+Base64.getEncoder().encodeToString(client.getRsa().getPublicKey().getEncoded()));
                }case "ENCRYPT_REQUEST" -> {
                    try {
                        String sessionKey=client.getRsa().decrypt(response[2]);
                        client.addKey(response[1],sessionKey);
                        client.getClientInputListener().setCommand("ENCRYPT OK");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }case "ENCRYPT_MSG" -> {
                    String key = client.getKey(response[2]);
                    String encryptMessage="<Encrypted message:> "+response[2]+" "+ClientUtils.decrypt(response[1], key);
                    System.out.println(encryptMessage);
                }case "TRANSFER_OK" ->{
                    System.out.println("The transfer request has been sent!");
                }case "TRANSFER_REQ" -> {
                    System.out.println( "The user " + response[1] + " wants to transfer a file to you named " + response[2] + " with the size " + ClientUtils.combinedMessage(4, response) + ". Enter 9 to accept and 0 to decline!");
                    client.setTransferRequest(true);
                    client.setLastTransferRequestUser(response[1]);
                    client.setLastTransferRequestFileName(response[2]);
                    client.setLastTransferRequestChecksum(response[3]);
                }case "TRANSFER_DECLINED" -> {
                    System.out.println("The user has declined the file transfer");
                }case "TRANSFER_ACCEPTED" -> {
                    if (response[1].indexOf('U') != -1){
                        System.out.println("The user has accepted the file transfer. The upload will start shortly.");
                    } else {
                        System.out.println("You have accepted the file transfer. The download will start shortly.");
                    }
                    new Thread(new FileTransferClientThread(response[1], response[2], client.getLastTransferRequestChecksum())).start();
                }case "FAIL01" ->{
                    System.out.println("User already logged in");
                }case "FAIL02" ->{
                    System.out.println("Username has an invalid format or length");
                }case "FAIL03" ->{
                    System.out.println("Please log in first");
                }case "FAIL04" -> {
                    System.out.println("Pong without ping");
                }case "FAIL05" -> {
                    System.out.println("Not enough users for survey");
                }case "FAIL06"->{
                    System.out.println("Invalid question or wrong number of answers");
                }case "FAIL08", "FAIL10" ->{
                    System.out.println(ClientUtils.combinedMessage(1, response));
                }case "FAIL07"-> {
                    System.out.println("usernames are incorrect");
                }case "FAIL00" ->{
                    System.out.println("invalid command");
                }
            }
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
