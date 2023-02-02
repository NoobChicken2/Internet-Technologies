package Client.ClientRequest;

import Client.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ClientRequestSurveyEvent implements ClientRequest{
    private Client client;

    public ClientRequestSurveyEvent(Client client) {
        this.client=client;
    }
    @Override
    public void request() throws InterruptedException, NoSuchAlgorithmException, IOException {
        //todo implement survey event
    }
}
