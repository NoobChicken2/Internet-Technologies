package Client;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {

    public static Socket socket;
    protected static boolean hasLoggedIn=false;

    static {
        try {
            socket = new Socket("127.0.0.1", 1337);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    protected static int input;

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
            input = getUserInput();
            switch (input) {
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
                    //todo implement start a survey
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

}
