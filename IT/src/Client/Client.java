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

    private static Socket socket;
    private static Socket fileTransferSocket;
    protected static boolean hasLoggedIn=false;
    protected static boolean survey=false;
    private static boolean pongAllowed = true;

    // Transfer request variables
    protected static boolean transferRequest = false;
    private static String lastTransferRequestUser = "";
    private static String lastTransferRequestFileName = "";

    // Socket connection
    static {
        try {
            socket = new Socket("127.0.0.1", 8000);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // File Transfer Socket connection
    static {
        try {
            fileTransferSocket = new Socket("127.0.0.1", 8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Thread listenUser = new Thread(new ListenOutputStream());
    static Thread listenServer = new Thread(new ListenInputStream());

    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException, IOException {
//        Client c = new Client();
//        c.init();
//        //put everything below


        listenUser.start();
        listenServer.start();
        Thread.sleep(1000);

        while (!hasLoggedIn){
            login();
            Thread.sleep(1000);
        }
        while (hasLoggedIn) {
            menu();
            int menuValue = getUserInput();
            switch (menuValue) {
                case 1 -> {
                    System.out.print("Enter your message: ");
                    ListenOutputStream.command = "BCST " + getUserInputString();
                }
                case 2 -> {
                    ListenOutputStream.command = "LIST_REQUEST";
                }
                case 3 -> {
                    System.out.print("Enter username you want to private message: ");
                    String username = getUserInputString();
                    System.out.print("Enter your message: ");
                    String message = getUserInputString();
                    ListenOutputStream.command="PRV_BCST "+ username+" "+message;
                }
                case 4 -> {
                    ListenOutputStream.command="SURVEY START";
                    Thread.sleep(1000);
                    while (survey){
                        Thread.sleep(1000);
                        sendQuestion();
                        System.out.println("Do you want to add a new Question?  1. Yes  |  2. No");
                        int input =getUserInput();//todo prevent the bug from ansking the new question before user even responds
                        if (input==1){
                            sendQuestion();
                        }else {
                            ListenOutputStream.command="SURVEY Q_STOP";
                            survey=false;//todo this is wrong for now !!!
                        }
                        Thread.sleep(1000);
                    }

                }
                case 5-> {
                    String userInput;
                    System.out.print("Enter the username of the user you want to transfer a file: ");
                    userInput = getUserInputString();
                    System.out.print("Enter the name of the file that you want to transfer from the TransferUpload folder: ");
                    String fileName = getUserInputString();
                    if (filePathFound(fileName)) {
                        String pathString = getFullPathString(fileName);
                        userInput += " " + fileName + " " + getFileSizeBytes(pathString) + " " + Utils.checksum(pathString);
                    }
                    ListenOutputStream.command = "TRANSFER " + userInput;
                }
                case 6 -> {
                    ListenOutputStream.command="QUIT";
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
                        ListenOutputStream.command = "TRANSFER_RES accepted " + lastTransferRequestUser + " " + lastTransferRequestFileName;
                    } else {
                        ListenOutputStream.command = "TRANSFER_RES declined " + lastTransferRequestUser;
                    }
                    resetFileTransferVariables();
                }
            }
        }
    }

    protected static void menu() {

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
    public static Socket getClientSocket() {
        return socket;
    }
    public static Socket getClientFileTransferSocket() {
        return fileTransferSocket;
    }
    public static void login(){
        System.out.println("Please login as a client: ");
        ListenOutputStream.command = "IDENT " + getUserInputString();
    }
    public static int getUserInput() {

        Scanner scanner;
        try {
            scanner = new Scanner(System.in);
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.err.println("Entered value is not an integer");
            return -1;
        }
    }
    public static String getUserInputString() {
        Scanner scanner;
        try {
            scanner = new Scanner(System.in);
            return scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entered value is not an String");
            return "";
        }
    }
    public static void sendQuestion(){
        String message="SURVEY Q ";
        System.out.println("Please enter your question:");
        message=message+getUserInputString()+" ";
        System.out.println("How many answers are in this question? (min:2/max:4)");
        int numOfAns=Client.getUserInput();
        while (numOfAns<2||numOfAns>4){
            System.out.println("invalid number of answers enter number of answers again");
            numOfAns=Client.getUserInput();
        }
        for (int i=0;i<numOfAns;i++){
            System.out.println("enter your answer");
            message=message+getUserInputString()+" ";
        }
        ListenOutputStream.command=message;
    }
    private static boolean filePathFound(String fileName) {
        String file = new File("").getAbsolutePath() + "\\IT\\TransferUpload" + "\\" + fileName.trim();
        System.out.println(file);
        File filePath = new File(file);
        return filePath.exists();
    }
    private static String getFullPathString(String fileName){
        return new File("").getAbsolutePath() + "\\IT\\TransferUpload" + "\\" + fileName.trim();
    }
    private static long getFileSizeBytes(String pathString){
        Path path = Paths.get(pathString);
        long bytes = 0;
        try {
            bytes = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
    public static boolean getPongAllowed () {
        return pongAllowed;
    }
    public static void setLastTransferRequestUser(String username) {
        lastTransferRequestUser = username;
    }
    public static void setLastTransferRequestFileName(String fileName) {
        lastTransferRequestFileName = fileName;
    }
    private static void resetFileTransferVariables() {
        transferRequest = false;
        lastTransferRequestUser = "";
        lastTransferRequestFileName = "";
    }
}
