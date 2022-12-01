import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private Socket socket;

    public Main(String address, int port) throws InterruptedException {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");
        } catch (IOException u) {
            System.out.println(u);
        }

        Thread client = new Thread(new ClientListener(socket));
        Thread server = new Thread(new ServerListener(socket));
        client.start();
        server.start();
        client.join();
        server.join();
        try {
            socket.close();
        } catch (Exception i) {
            System.out.println(i);
        } finally {
            System.out.println("You have disconnected");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Main client= new Main("127.0.0.1", 1337);
        menu();
        while(true){
            switch (getUserInput()){
                case 0 -> {
                    System.out.println("Enter your username : ");

                    menu();
                }
                case 1 -> {
                    System.out.println("Broadcast your message : ");

                    menu();
                }
                case 2 -> {
                    //todo quit
                }
            }

        }

    }
    private static void menu(){
        System.out.println("Menu");
        System.out.println("0) login");
        System.out.println("1) Send message");
        System.out.println("2) quit");
    }
    private static int getUserInput() {

        Scanner scanner;
        try {
            scanner = new Scanner(System.in);
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.err.println("Entered value is not an integer");
            return 10000;
        }

    }
}
