package Client.ClientRequest;

import Client.Client;
import Client.Utils.ClientUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ClientRequestEncryptedMessage implements ClientRequest{
    private Client client;
    public ClientRequestEncryptedMessage(Client client) {
        this.client = client;
    }
    @Override
    public void request() throws InterruptedException, NoSuchAlgorithmException, IOException {
        System.out.println("Current surveys available for you to join!");
        for (int i = 0; i < client.getSurveyEventCreators().size(); i++) {
            System.out.println("1. "+client.getSurveyEventCreators().get(i)+"'s survey");
        }
        int surveyNum= ClientUtils.getUserInput();
        String surveyName=client.getSurveyEventCreators().get(surveyNum);
        String message="SURVEY_EVENT JOIN "+surveyName;
        client.getClientInputListener().setCommand(message);

    }
}
