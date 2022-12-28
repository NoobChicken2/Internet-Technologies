package Server;

import Server.ServerResponse.*;
import Server.ServerResponse.ServerResponseSurvey;
import Server.ServerResponse.Survey.Survey;

import java.io.*;
import java.net.Socket;

public class MessageProcessor implements Runnable{
    public Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    protected String name;
    protected boolean exit=false;
    protected Survey survey;
    private Thread clientHeartbeat;

    public MessageProcessor(Socket socket, InputStream inputStream, OutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public MessageProcessor() {
    }

    @Override
    public void run() {
        //Start heartbeat
        Heartbeat heartbeat = new Heartbeat(this);
        clientHeartbeat = new Thread(heartbeat);
        clientHeartbeat.start();

        sendMessage("INIT Yani/Klaus server!");
        ServerResponseManager responseManager=new ServerResponseManager();
        while (!exit) {
            try {
                // getting response from client
                BufferedReader readMessage = new BufferedReader(new InputStreamReader(inputStream));
                String receivedString=readMessage.readLine();

                if (receivedString==null){
                    sendMessage("FAIL00 Unkown command");
                }else{
                    String[] response = receivedString.split(" ");
                    switch (response[0]){
                        case "IDENT" ->{
                           // new ServerResponseLogin(this).respond(receivedString);
                            responseManager.setServerResponse(new ServerResponseLogin(this));
                            responseManager.respond(receivedString);
                        }
                        case "QUIT" ->{
                            responseManager.setServerResponse(new ServerResponseQuit(this));
                            responseManager.respond(receivedString);
                        }
                        case "BCST" ->{
                            responseManager.setServerResponse(new ServerResponseBroadcast(this));
                            responseManager.respond(receivedString);
                        }
                        case "LIST_REQUEST" ->{
                            responseManager.setServerResponse(new ServerResponseListRequest(this));
                            responseManager.respond(receivedString);
                        }
                        case "PRV_BCST" ->{
                            responseManager.setServerResponse(new ServerResponsePrivateMessage(this));
                            responseManager.respond(receivedString);
                        }
                        case "SURVEY" ->{
                            responseManager.setServerResponse(new ServerResponseSurvey(this));
                            responseManager.respond(receivedString);
                        }
                        case "PONG" -> {
                            if (heartbeat.getIsPingSet()) {
                                heartbeat.setPongTrue();
                            } else {
                                sendMessage("FAIL09 Pong without ping");
                            }
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
        PrintWriter sendMessage = new PrintWriter(outputStream);
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
}
