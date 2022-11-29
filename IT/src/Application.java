import java.util.InputMismatchException;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) {
        int choice;
        menu();
        while(true){
            choice=getUserInput();
            switch (choice){
                case 0:
                    //todo login
                    break;
                case 1:
                    //todo broadcast message
                    break;
                case 2:
                    //todo quit
                    break;
            }

        }
    }
    public static void menu(){
        System.out.println("Menu");
        System.out.println("0) login");
        System.out.println("1) Send message");
        System.out.println("2) quit");
    }
    public static int getUserInput() {

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
