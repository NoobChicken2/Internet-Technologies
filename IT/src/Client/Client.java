package Client;

import GlobalUtilities.Utils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private Socket fileTransferSocket;

    protected boolean hasLoggedIn=false;
    protected boolean survey=false;
    private boolean pongAllowed = true;
    protected boolean transferRequest = false;
    private String lastTransferRequestUser = "";
    private String lastTransferRequestFileName = "";
    private boolean waitingResponse = false;
    private ClientInputListener clientInputListener;
    private ServerListener serverListener;

    public void run () {
        try {
            socket = new Socket("127.0.0.1", 8000);
            clientInputListener = new ClientInputListener(this);
            serverListener = new ServerListener(this, clientInputListener);
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
                int menuValue = getUserInput();
                switch (menuValue) {
                    case 1 -> {
                        System.out.print("Enter your message: ");
                        clientInputListener.setCommand("BCST " + getUserInputString());
                    }
                    case 2 -> {
                        clientInputListener.setCommand("LIST_REQUEST");
                    }
                    case 3 -> {
                        System.out.print("Enter username you want to private message: ");
                        String username = getUserInputString();
                        System.out.print("Enter your message: ");
                        String message = getUserInputString();
                        clientInputListener.setCommand("PRV_BCST " + username + " " + message);
                    }
                    case 4 -> {
                        clientInputListener.setCommand("SURVEY START");
                        while (survey) {
                            sendQuestion();
                            System.out.println("Do you want to add a new Question?  1. Yes  |  2. No");
                            int input = getUserInput();//todo prevent the bug from ansking the new question before user even responds
                            if (input == 1) {
                                sendQuestion();
                            } else {
                                clientInputListener.setCommand("SURVEY Q_STOP");
                                survey = false;//todo this is wrong for now !!!
                            }
                        }

                    }
                    case 5 -> {
                        String userInput;
                        System.out.print("Enter the username of the user you want to transfer a file: ");
                        userInput = getUserInputString();
                        System.out.print("Enter the name of the file that you want to transfer from the TransferUpload folder: ");
                        String fileName = getUserInputString();
                        if (filePathFound(fileName)) {
                            String pathString = getFullPathString(fileName);
                            userInput += " " + fileName + " " + getFileSizeBytes(pathString) + " " + Utils.checksum(pathString);
                        }
                        clientInputListener.setCommand("TRANSFER " + userInput);
                    }
                    case 6 -> {
                        clientInputListener.setCommand("QUIT");
                        System.out.println("Goodbye!");
                        System.exit(0);
                    }
                    case 7 -> {
                        pongAllowed = !pongAllowed;
                    }
                    case 9, 0 -> {
                        if (!transferRequest) {
                            System.out.println("You are not allowed to do this right now!");
                            break;
                        }
                        if (menuValue == 9) {
                            clientInputListener.setCommand("TRANSFER_RES accepted " + lastTransferRequestUser + " " + lastTransferRequestFileName);
                            System.out.println("You have accepted the file transfer. The download will start shortly.");
                        } else {
                            clientInputListener.setCommand("TRANSFER_RES declined " + lastTransferRequestUser);
                        }
                        lastTransferRequestUser = "";
                        lastTransferRequestFileName = "";
                    }
                }
//                clientInputListener.sendMessage();
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Client().run();
    }

    protected void menu() {

        System.out.println("""
                        -------------------------------------------
                        1. Broadcast a message
                        2. Get List of all users
                        3. Private message
                        4. Start a survey
                        5. Transfer a file
                        6. QUIT
                        -------------------------------------------
                       """);
    }
    public Socket getClientSocket() {
        return socket;
    }
    public Socket getClientFileTransferSocket() {
        return fileTransferSocket;
    }
    public void login(){
        System.out.println("Please login as a client: ");
        clientInputListener.setCommand("IDENT " + getUserInputString());
        waitingResponse = true;
    }
    public int getUserInput() {
        Scanner scanner;
        try {
            scanner = new Scanner(System.in);
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.err.println("Entered value is not an integer");
            return -1;
        }
    }
    public String getUserInputString() {
        Scanner scanner;
        try {
            scanner = new Scanner(System.in);
            return scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entered value is not an String");
            return "";
        }
    }

    public void sendQuestion(){
        String message="SURVEY Q ";
        System.out.println("Please enter your question:");
        message=message+getUserInputString()+" ";
        System.out.println("How many answers are in this question? (min:2/max:4)");
        int numOfAns= getUserInput();
        while (numOfAns<2||numOfAns>4){
            System.out.println("invalid number of answers enter number of answers again");
            numOfAns= getUserInput();
        }
        for (int i=0;i<numOfAns;i++){
            System.out.println("enter your answer");
            message=message+getUserInputString()+" ";
        }
        clientInputListener.setCommand(message);
    }
    private boolean filePathFound(String fileName) {
        String file = new File("").getAbsolutePath() + "\\IT\\TransferUpload" + "\\" + fileName.trim();
        File filePath = new File(file);
        return filePath.exists();
    }
    private String getFullPathString(String fileName){
        return new File("").getAbsolutePath() + "\\IT\\TransferUpload" + "\\" + fileName.trim();
    }
    private long getFileSizeBytes(String pathString){
        Path path = Paths.get(pathString);
        long bytes = 0;
        try {
            bytes = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
    public boolean getPongAllowed () {
        return pongAllowed;
    }
    public void setLastTransferRequestUser(String username) {
        lastTransferRequestUser = username;
    }
    public void setLastTransferRequestFileName(String fileName) {
        lastTransferRequestFileName = fileName;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Socket getFileTransferSocket() {
        return fileTransferSocket;
    }

    public void setFileTransferSocket(Socket fileTransferSocket) {
        this.fileTransferSocket = fileTransferSocket;
    }

    public boolean isHasLoggedIn() {
        return hasLoggedIn;
    }

    public boolean isWaitingResponse() {
        return waitingResponse;
    }

    public void setWaitingResponse(boolean waitingResponse) {
        this.waitingResponse = waitingResponse;
    }


    public void setHasLoggedIn(boolean hasLoggedIn) {
        this.hasLoggedIn = hasLoggedIn;
    }

    public boolean isSurvey() {
        return survey;
    }

    public void setSurvey(boolean survey) {
        this.survey = survey;
    }

    public boolean isPongAllowed() {
        return pongAllowed;
    }

    public void setPongAllowed(boolean pongAllowed) {
        this.pongAllowed = pongAllowed;
    }

    public boolean isTransferRequest() {
        return transferRequest;
    }

    public void setTransferRequest(boolean transferRequest) {
        this.transferRequest = transferRequest;
    }

    public String getLastTransferRequestUser() {
        return lastTransferRequestUser;
    }

}
