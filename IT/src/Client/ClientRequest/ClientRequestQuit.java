package Client.ClientRequest;

import Client.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ClientRequestQuit implements ClientRequest{
    private Client client;

    public ClientRequestQuit(Client client) {
        this.client = client;
    }

    @Override
    public void request() throws InterruptedException, NoSuchAlgorithmException, IOException {
        client.getClientInputListener().setCommand("QUIT");
        System.out.println("Goodbye!");
        System.exit(0);
    }
}
