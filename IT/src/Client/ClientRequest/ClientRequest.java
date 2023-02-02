package Client.ClientRequest;

import Client.Client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ClientRequest {
    public abstract void request() throws InterruptedException, NoSuchAlgorithmException, IOException;
}
