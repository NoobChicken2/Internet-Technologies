package Server;

import Server.Heartbeat.Heartbeat;
import Server.ServerResponse.*;
import Server.ServerResponse.ServerResponseSurvey;
import Server.Survey.Survey;

import java.io.*;
import java.net.Socket;

public class MessageProcessor implements Runnable{
//  ==================web sockets=================
    private final Server server;
    public Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private  PrintWriter sendMessage;
    private BufferedReader readMessage;
//  ===============================================
    private String name;
    private boolean exit;
    private Survey survey;
    private Thread clientHeartbeat;

    public MessageProcessor(Server server,Socket socket, InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.socket=socket;
        this.sendMessage = new PrintWriter(outputStream);
        this.readMessage = new BufferedReader(new InputStreamReader(inputStream));
        this.server=server;
        this.exit=false;
    }

    @Override
    public void run() {
        //Start heartbeat
        Heartbeat heartbeat = new Heartbeat(this);
        clientHeartbeat = new Thread(heartbeat);
        clientHeartbeat.start();

        sendMessage("INIT Yani/Klaus server!");
        while (!exit) {
            try {
                // getting response from client
                String receivedString=readMessage.readLine();
                System.out.println(receivedString);
                if (receivedString==null){
                    new ServerResponseQuit(this).respond("QUIT_OK");
                }else{
                    String[] response = receivedString.split(" ");
                    switch (response[0]){
                        case "IDENT" ->{
                            new ServerResponseLogin(this).respond(receivedString);
                        }
                        case "QUIT" ->{
                            new ServerResponseQuit(this).respond("QUIT_OK");
                        }
                        case "BCST" ->{
                            new ServerResponseBroadcast(this).respond(receivedString);
                        }
                        case "LIST_REQUEST" ->{
                            new ServerResponseListRequest(this).respond(receivedString);
                        }
                        case "PRV_BCST" ->{
                            new ServerResponsePrivateMessage(this).respond(receivedString);
                        }
                        case "SURVEY" ->{
                            new ServerResponseSurvey(this).respond(receivedString);
                        }
                        case "SURVEY_EVENT" ->{
                            new ServerResponseSurveyEvent(this).respond(receivedString);
                        }
                        case "ENCRYPT" ->{
                            new ServerResponseEncryptMessage(this).respond(receivedString);
                        }
                        case "PONG" -> {
                            if (heartbeat.getIsPingSet()) {
                                heartbeat.setPongTrue();
                            } else {
                                sendMessage("FAIL05 Pong without ping");
                            }
                        }
                        case "TRANSFER", "TRANSFER_RES" -> {
                            new ServerFileTransfer(this).respond(receivedString);
                        }
                        default -> {
                            sendMessage("FAIL00 Unkown command");
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(name + " timed out and was disconnected");
                new ServerResponseQuit(this).respond("DCSN");
            }
        }
    }
    public boolean checkClientLoggedIn(){
        if (this.name==null) {
            sendMessage("FAIL03 Please log in first");
            return false;
        }
        return true;
    }
    public void sendMessage(String message) {
        sendMessage.println(message);
        sendMessage.flush();
    }
    public void createSurvey(){
        survey=new Survey();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Survey getSurvey() {
        return survey;
    }
    public void setExit(boolean exit) {
        this.exit = exit;
    }
    public void stopHeartbeat() {
        if (clientHeartbeat.isAlive()) {
            clientHeartbeat.interrupt();
        }
    }
    public Server getServer() {
        return server;
    }
}
