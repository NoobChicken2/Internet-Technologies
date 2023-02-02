package Client.ClientRequest;

import Client.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ClientRequestListRequest implements ClientRequest{
    private Client client;

    public ClientRequestListRequest(Client client) {
        this.client = client;
    }

    @Override
    public void request(int menuValue) throws InterruptedException, NoSuchAlgorithmException, IOException {
        client.getClientInputListener().setCommand("LIST_REQUEST");
    }
}
