package Client.ClientRequest;

import Client.Client;
import Client.Utils.ClientUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ClientRequestBroadcast implements ClientRequest{
    private Client client;
    public ClientRequestBroadcast(Client client) {
        this.client = client;
    }

    @Override
    public void request(int menuValue) throws InterruptedException, NoSuchAlgorithmException, IOException {
        System.out.print("Enter your message: ");
        client.getClientInputListener().setCommand("BCST " + ClientUtils.getUserInputString());
    }
}
