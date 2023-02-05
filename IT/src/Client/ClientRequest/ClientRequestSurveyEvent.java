package Client.ClientRequest;

import Client.Client;
import Client.Utils.ClientUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ClientRequestSurveyEvent implements ClientRequest{
    private Client client;

    public ClientRequestSurveyEvent(Client client) {
        this.client=client;
    }
    @Override
    public void request(int menuValue) throws InterruptedException, NoSuchAlgorithmException, IOException {
        if (client.getSurveyEventCreators().size() > 0) {
            System.out.println("Current surveys available for you to join!");
            for (int i = 0; i < client.getSurveyEventCreators().size(); i++) {
                System.out.println(i + 1 + ". " + client.getSurveyEventCreators().get(i) + "'s survey");
            }
            int surveyNum = ClientUtils.getUserInput();
            String surveyName = client.getSurveyEventCreators().get(surveyNum + 1);
            String message = "SURVEY_EVENT JOIN " + surveyName;
            client.getClientInputListener().setCommand(message);
            answeringQuestions(client.getServerResponse());
        } else {
            System.out.println("no available surveys to join");
        }
    }

    private void answeringQuestions(String serverResponse){
        boolean ansQ=true;
        while (ansQ) {
            if (client.getServerResponse().startsWith("SURVEY_EVENT Q")) {
                gettingAns(serverResponse);
                client.setServerResponse("");

            }
            if (client.getServerResponse().startsWith("SURVEY_EVENT QF")) {
                gettingAns(serverResponse);
                client.setServerResponse("");
                ansQ=false;
            }
        }
    }
    private void gettingAns(String serverResponse){
        String[] question = serverResponse.split(";");
        System.out.println(question[1]);
        System.out.println("pick an answer");
        for (int i = 3; i < question.length; i++) {
            System.out.println(i - 3 + " " + question[i]);
        }
        int ans = ClientUtils.getUserInput();
        client.getClientInputListener().setCommand("SURVEY_EVENT A " + ans + ";" + question[3]);
    }
}
