package Client.ClientRequest;

import Client.Client;
import Client.Utils.ClientUtils;

public class ClientRequestSurvey implements ClientRequest{
    private Client client;

    public ClientRequestSurvey(Client client) {
        this.client=client;
        this.client.setSurvey(false);
        this.client.setClientList(false);
    }

    @Override
    public void request() throws InterruptedException {
        client.getClientInputListener().setCommand("SURVEY START");
        Thread.sleep(3000);
        while (client.isSurvey()) {
            sendQuestions();
            String[] response = client.getServerResponse().split(" ");
            if (response[0].equals("SURVEY_LIST")){
                sendClientList(response);
            }
        }
    }

    private void sendClientList(String[]response) throws InterruptedException {
        client.setClientList(true);
        String message="";
        while (client.isClientList()) {
            message=message+"SURVEY LIST_RESPONSE ";
            System.out.println("Currently logged in clients");
            for (int i = 1; i < response.length; i++) {
                System.out.println(response[i]);
            }
            System.out.println("Please enter clients names with one space in between");
            message =message+ ClientUtils.getUserInputString();
            client.getClientInputListener().setCommand(message);
            Thread.sleep(3000);
        }
    }
    public void sendQuestions(){
        boolean questions=true;
        int numOfQuestions=0;
        while (questions) {

            //getting the question
            String message = "SURVEY Q /";
            System.out.println("Please enter your question:");
            message = message + ClientUtils.getUserInputString() ;

            //getting the answers
            System.out.println("How many answers are in this question? (min:2/max:4)");
            int numOfAns =ClientUtils.getUserInput();
            while (numOfAns < 2 || numOfAns > 4) {
                System.out.println("invalid number of answers enter number of answers again");
                numOfAns = ClientUtils.getUserInput();
            }
            for (int i = 0; i < numOfAns; i++) {
                System.out.println("enter your answer");
                message = message + "/"+ClientUtils.getUserInputString() ;
            }
            client.getClientInputListener().setCommand(message);

            //checking if new questions are to be added
            System.out.println("Do you want to add a new Question?  1. Yes  |  2. No");
            int input = ClientUtils.getUserInput();
            if (input==2) {
                client.getClientInputListener().setCommand("SURVEY Q_STOP");
                break;
            }
            numOfQuestions++;
            if (numOfQuestions>10){
                break;
            }
        }
    }
}
