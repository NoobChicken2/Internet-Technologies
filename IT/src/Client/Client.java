package Client;

import Client.ClientRequest.*;
import Client.Encryption.RSA;
import Client.Utils.ClientUtils;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    //=================Web Socket=====================
    private Socket socket;
    private ClientInputListener clientInputListener;
    private ServerListener serverListener;
    //=================Client=========================
    private String username;
    private boolean hasLoggedIn;
    private String serverResponse;
    //=================Encryption======================
    private HashMap<String, String>keys;
    private HashMap<String, String>PublicKeys;
    private RSA rsa;

    //=================Survey==========================
    private boolean clientList;
    private ArrayList<String>surveyEventCreators;
    //=================File Transfer===================
    private boolean transferRequest;
    private String lastTransferRequestUser;
    private String lastTransferRequestFileName;

    public Client() throws IOException {
        this.socket = new Socket("127.0.0.1", 3000);
        this.clientInputListener = new ClientInputListener(this);
        this.serverListener = new ServerListener(this, clientInputListener);
        this.surveyEventCreators=new ArrayList<>();
        this.keys=new HashMap<>();
        this.PublicKeys=new HashMap<>();
        this.username="";
        this.hasLoggedIn=false;
        this.serverResponse="";
        this.rsa=new RSA();
    }

    public void run () throws Exception {
        Thread listenUser = new Thread(clientInputListener);
        Thread listenServer = new Thread(serverListener);
        listenServer.start();
        listenUser.start();
        while (!hasLoggedIn) {
            login();
            // Delayed response from server causes login to be repeated a second time
            // Optimize this later to wait for response using asynchronous methods
            Thread.sleep(1000);
        }
        while (hasLoggedIn) {
            menu();
            int menuValue = ClientUtils.getUserInput();
            switch (menuValue) {
                case 1 -> {
                    new ClientRequestBroadcast(this).request(menuValue);
                }
                case 2 -> {
                    new ClientRequestListRequest(this).request(menuValue);
                }
                case 3 -> {
                    new ClientRequestPrivateMessage(this).request(menuValue);
                }
                case 4 -> {
                    new ClientRequestSurvey(this).request(menuValue);
                }
                case 5 -> {
                   new ClientRequestSurveyEvent(this).request(menuValue);
                }
                case 6, 9, 0 -> {
                    new ClientRequestFileTransfer(this).request(menuValue);
                }
                case 7 -> {
                    new ClientRequestEncryptedMessage(this, rsa).request(menuValue);
                }
                case 8 -> {
                    new ClientRequestQuit(this).request(menuValue);
                }
                case 10 -> {
                    printHelp();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new Client().run();
    }

    protected void menu() {
        System.out.println("""
                        -------------------------------------------
                        1. Broadcast a message
                        2. Get List of all users
                        3. Private message
                        4. Start a survey
                        5. Join a survey
                        6. Transfer a file
                        7. Encrypted Message
                        8. QUIT
                        10.Help
                        -------------------------------------------
                       """);
    }
    public void login(){
        System.out.println("Please login as a client: ");
        clientInputListener.setCommand("IDENT " + ClientUtils.getUserInputString());
    }
    public void printHelp(){
        System.out.println("===========================================================================================================================");
        System.out.println("BCST <message> | Sends broadcast messages to everyone on the server ");
        System.out.println("IDENT <username> | Logs you in the server");
        System.out.println("PONG | Periodically send to the server its automatic");
        System.out.println("LIST_REQUEST | Returns a list of all logged in clients");
        System.out.println("SURVEY START | Starts a survey");
        System.out.println("SURVEY Q /<question>/<answer</<answer>... | Sends a question to the server");
        System.out.println("SURVEY Q_STOP | Stops the question sending loop and returns a list of potential participants" );
        System.out.println("SURVEY LIST_RESPONSE /<username>/<username> | Sends invitation for the currently created survey to selected users" );
        System.out.println("SURVEY_EVENT JOIN <Survey Creator> | Accepts survey invitation" );
        System.out.println("SURVEY_EVENT A <answer>;<question number> | Sends answer to a question" );
        System.out.println("Encryption...?" );
        System.out.println("QUIT | Closes the connection to the server");
        System.out.println("===========================================================================================================================");

    }
    public ClientInputListener getClientInputListener() {
        return clientInputListener;
    }
    public Socket getClientSocket() {
        return socket;
    }
    public boolean isClientList() {
        return clientList;
    }
    public String getLastTransferRequestUser() {
        return lastTransferRequestUser;
    }
    public String getLastTransferRequestFileName() {
        return lastTransferRequestFileName;
    }
    public boolean getTransferRequest() {
        return transferRequest;
    }
    public String getServerResponse() {
        return serverResponse;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public void setHasLoggedIn(boolean hasLoggedIn) {
        this.hasLoggedIn = hasLoggedIn;
    }
    public void setClientList(boolean clientList) {
        this.clientList = clientList;
    }
    public void setTransferRequest(boolean transferRequest) {
        this.transferRequest = transferRequest;
    }
    public void setLastTransferRequestUser(String lastTransferRequestUser) {
        this.lastTransferRequestUser = lastTransferRequestUser;
    }
    public void setServerResponse(String serverResponse) {
        this.serverResponse = serverResponse;
    }
    public void addEventCreator(String creator){
        surveyEventCreators.add(creator);
    }
    public ArrayList<String> getSurveyEventCreators() {
        return surveyEventCreators;
    }
    public void setLastTransferRequestFileName(String fileName) {
        lastTransferRequestFileName = fileName;
    }
    public String getKey(String client) {
        return keys.get(client);
    }
    public void addKey(String client, String key) {
        this.keys.put(client, key);
    }
    public String getPublicKey(String client) {
        PublicKeys.forEach((k,v) -> {
            System.out.println("Map Test" + " " + k + " : " + v);
        });
        return PublicKeys.get(client);
    }
    public void addPublicKey(String client, String key) {
        this.PublicKeys.put(client, key);
        System.out.println(client + " : " + key);
    }

    public String getUsername() {
        return username;
    }

    public RSA getRsa() {
        return rsa;
    }
}
