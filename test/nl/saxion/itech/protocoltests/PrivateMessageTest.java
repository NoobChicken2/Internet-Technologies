package nl.saxion.itech.protocoltests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.Properties;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class PrivateMessageTest {

    private static final Properties props = new Properties();

    private Socket s;
    private BufferedReader in;
    private PrintWriter out;

    private Socket s2;
    private BufferedReader in2;
    private PrintWriter out2;

    private final String firstUser = "test";
    private final String secondUser = "test2";

    private final static int max_delta_allowed_ms = 100;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = AcceptedUsernames.class.getResourceAsStream("testconfig.properties");
        props.load(in);
        if (in != null) {
            in.close();
        }
    }

    @BeforeEach
    void setup() throws IOException {
        s = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);

        s2 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in2 = new BufferedReader(new InputStreamReader(s2.getInputStream()));
        out2 = new PrintWriter(s2.getOutputStream(), true);
    }

    @AfterEach
    void cleanup() throws IOException {
        s.close();
        s2.close();
    }

    @Test
    void privateMessageAccepted() throws InterruptedException {
        receiveLineWithTimeout(in); //init message
        out.println("IDENT test");
        out.flush();
        receiveLineWithTimeout(in);
        receiveLineWithTimeout(in2); //init message
        out2.println("IDENT test2");
        out2.flush();
        String secondClientSignedIn = receiveLineWithTimeout(in2);
        if(secondClientSignedIn.equals("IDENT_OK test2")){
            String message = "testing";
            out.println("PRV_BCST test2 " + message);
            out.flush();
            String serverResponse = receiveLineWithTimeout(in2);
            String [] responseText = serverResponse.split(" ");
            assertEquals("PRV_BCST", responseText[0]);
            assertEquals(firstUser, responseText[1]);
            assertEquals(message,responseText[2]);
        }
    }

    @Test
    void privateMessageReceived() {
        receiveLineWithTimeout(in); //init message
        out.println("IDENT test");
        out.flush();
        receiveLineWithTimeout(in);
        String message = "testing";
        out.println("PRV_BCST test " + message);
        out.flush();
        String serverResponse = receiveLineWithTimeout(in);
        String []responseText = serverResponse.split(" ");
        assertEquals("PRV_BCST_OK", responseText[0]);
        assertEquals(message,responseText[1]);
    }



    @Test
    void privateMessageError() {
        receiveLineWithTimeout(in); //init message;
        String message = "testing";
        out.println("PRV_BCST test " + message);
        out.flush();
        String serverResponse = receiveLineWithTimeout(in);
        String []responseText = serverResponse.split(" ");
        assertEquals("FAIL03", responseText[0]);
    }


    private String receiveLineWithTimeout(BufferedReader reader){
        return assertTimeoutPreemptively(ofMillis(max_delta_allowed_ms), reader::readLine);
    }
}
