package Client.ClientRequest;

import Client.Client;
import Client.Utils.ClientUtils;

public class ClientRequestPrivateMessage implements ClientRequest{
    private Client client;

    public ClientRequestPrivateMessage(Client client) {
        this.client = client;
    }

    @Override
    public void request() throws InterruptedException {
        System.out.print("Enter username you want to private message: ");
        String username = ClientUtils.getUserInputString();
        System.out.print("Enter your message: ");
        String message = ClientUtils.getUserInputString();
        client.getClientInputListener().setCommand("PRV_BCST " + username + " " + message);
    }
}
