package nl.saxion.itech.protocoltests;

import Client.Encryption.RSA;
import Client.Utils.ClientUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Properties;

import static Client.Utils.ClientUtils.generatePkFromString;
import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class EncryptionTests {

    private static final Properties props = new Properties();

    private Socket s, s2;
    private BufferedReader in, in2;
    private PrintWriter out, out2;

    private final static int max_delta_allowed_ms = 1000;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = AcceptedUsernames.class.getResourceAsStream("testconfig.properties");
        props.load(in);
        if (in != null) {
            in.close();
        }
    }

    @BeforeEach
    void setup() throws IOException, InterruptedException {
        Thread.sleep(1000);
        s = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);

        s2 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
        out2 = new PrintWriter(s2.getOutputStream(), true);

        receiveLineWithTimeout(in); //init message
        out.println("IDENT test");
        out.flush();
        receiveLineWithTimeout(in);//ok

        receiveLineWithTimeout(in2); //init message
        out2.println("IDENT test2");
        out2.flush();
        receiveLineWithTimeout(in2);//ok
        receiveLineWithTimeout(in);
    }

    @Test
    public void TC12_1_receiverKeyRequest() throws Exception {
        out.println("ENCRYPT PUBLIC test2");
        out.flush();
        assertEquals("ENCRYPT_PUBLIC test",receiveLineWithTimeout(in2));
    }

    @Test
    public void TC12_2_keyExchange() {
        String sentKey = Base64.getEncoder().encodeToString(new RSA().getPublicKey().getEncoded());
        out2.println("ENCRYPT REQUEST test "+ sentKey);
        out2.flush();
        String receivedKey = receiveLineWithTimeout(in).split(" ")[2];
        out.println("ENCRYPT OK");
        out.flush();
        assertEquals(sentKey,receivedKey);
    }

    @Test
    public void TC12_3_fullKeyExchange() throws Exception {
        String sentKey = Base64.getEncoder().encodeToString(new RSA().getPublicKey().getEncoded());
        out2.println("ENCRYPT REQUEST test "+ sentKey);
        out2.flush();
        String receivedKey = receiveLineWithTimeout(in).split(" ")[2];
        out.println("ENCRYPT OK");
        out.flush();
        PublicKey pk = ClientUtils.generatePkFromString(receivedKey);
        out.println("ENCRYPT REQUEST test2 "+ new RSA().encryptWithPK("aesEncryptionKey",pk));
        out.flush();
        assertEquals("ENCRYPT_REQUEST_OK",receiveLineWithTimeout(in));
        assertEquals("ENCRYPT_REQUEST_OK",receiveLineWithTimeout(in2));
        receiveLineWithTimeout(in2);
    }

    @Test
    public void TC12_4_encryptMessage(){
        out.println("ENCRYPT MSG "+ ClientUtils.encrypt("yani", "aesEncryptionKey") + " test2");
        out.flush();
        assertEquals("yani",ClientUtils.decrypt(receiveLineWithTimeout(in2).split(" ")[1],"aesEncryptionKey"));
    }

    @AfterEach
    void cleanup() throws IOException {
        s.close();
        s2.close();
    }

    private String receiveLineWithTimeout(BufferedReader reader){
        return assertTimeoutPreemptively(ofMillis(max_delta_allowed_ms), reader::readLine);
    }
}
