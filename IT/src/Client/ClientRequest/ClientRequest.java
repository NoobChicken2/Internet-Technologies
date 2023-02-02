package Client.ClientRequest;

import Client.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ClientRequest {

    public abstract void request(int menuValue) throws InterruptedException, NoSuchAlgorithmException, IOException;
}
