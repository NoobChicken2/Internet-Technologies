package Client;

import Client.ClientRequest.*;
import Client.Utils.ClientUtils;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Client {
    //=================Web Socket=====================
    private Socket socket;
    private ClientInputListener clientInputListener;
    private ServerListener serverListener;
    //=================Client=========================
    private String username;
    private boolean hasLoggedIn;
    private String serverResponse;
    private boolean waitingResponse;
    //=================Survey==========================
    private boolean survey;
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
        this.username="";
        this.hasLoggedIn=false;
        this.serverResponse="";
        this.waitingResponse=false;
    }

    public void run () throws InterruptedException,IOException, NoSuchAlgorithmException {
        Thread listenUser = new Thread(clientInputListener);
        Thread listenServer = new Thread(serverListener);
        listenServer.start();
        listenUser.start();
        while (!hasLoggedIn) {
            if (!waitingResponse) {
                login();
            }
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
                    new ClientRequestEncryptedMessage(this).request(menuValue);
                }
                case 8 -> {
                    new ClientRequestQuit(this).request(menuValue);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException, NoSuchAlgorithmException {
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
                        -------------------------------------------
                       """);
    }
    public void login(){
        System.out.println("Please login as a client: ");
        clientInputListener.setCommand("IDENT " + ClientUtils.getUserInputString());
    }
    public ClientInputListener getClientInputListener() {
        return clientInputListener;
    }
    public String getUsername() {
        return username;
    }
    public Socket getClientSocket() {
        return socket;
    }
    public boolean isClientList() {
        return clientList;
    }
    public boolean isSurvey() {
        return survey;
    }
    public boolean isTransferRequest() {
        return transferRequest;
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
    public boolean isWaitingResponse() {
        return waitingResponse;
    }
    public void setWaitingResponse(boolean waitingResponse) {
        this.waitingResponse = waitingResponse;
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
    public void setSurvey(boolean survey) {
        this.survey = survey;
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

}
