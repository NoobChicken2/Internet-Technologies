package Client;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    public static Socket socket;
    protected static boolean hasLoggedIn=false;
    protected static boolean survey=false;

    static {
        try {
            socket = new Socket("127.0.0.1", 1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Thread listenUser = new Thread(new ListenOutputStream());
    static Thread listenServer = new Thread(new ListenInputStream());

    public static void main(String[] args) throws InterruptedException {
        listenUser.start();
        listenServer.start();
        Thread.sleep(1000);

        while (hasLoggedIn==false){
            login();
            Thread.sleep(1000);
        }
        while (hasLoggedIn==true) {
            menu();
            switch (getUserInput()) {
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
                    while (survey==true){
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
                    ListenOutputStream.command="QUIT";
                    System.out.println("Goodbye!");
                    System.exit(0);
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
                        5. QUIT
                        -------------------------------------------
                       """);
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
}
