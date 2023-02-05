package Client.ClientRequest;

import Client.Client;
import Client.Encryption.RSA;
import Client.Utils.ClientUtils;

import java.security.PublicKey;

public class ClientRequestEncryptedMessage implements ClientRequest{
    private Client client;
    private RSA rsa;
    private final String key = "aesEncryptionKey";
    public ClientRequestEncryptedMessage(Client client, RSA Rsa) {
        this.client = client;
        this.rsa=Rsa;
    }
    @Override
    public void request(int menuValue) throws Exception {

        //rsa goes here
        System.out.println("Enter receiver name");
        String receiver= ClientUtils.getUserInputString();
        client.getClientInputListener().setCommand("ENCRYPT PUBLIC "+receiver);
        Thread.sleep(3000);

        String publicKey = client.getPublicKey(receiver);
        System.out.println(receiver);
        PublicKey pk= ClientUtils.generatePkFromString(publicKey);
        client.getClientInputListener().setCommand("ENCRYPT REQUEST "+receiver+ " "+ rsa.encryptWithPK(key,pk));
        Thread.sleep(2000);

        System.out.println("Enter your message");
        String message=ClientUtils.getUserInputString();
        String cypherText=ClientUtils.encrypt(message, key);

        client.getClientInputListener().setCommand("ENCRYPT MSG "+cypherText+" "+receiver);
    }
}
