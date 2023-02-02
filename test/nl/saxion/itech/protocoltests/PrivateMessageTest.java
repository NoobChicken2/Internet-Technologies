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
    void TC7_1_privateMessageSendAndReceived() throws InterruptedException {
        receiveLineWithTimeout(in); //init message
        out.println("IDENT test");
        out.flush();
        receiveLineWithTimeout(in);//ok

        receiveLineWithTimeout(in2); //init message
        out2.println("IDENT test2");
        out2.flush();
        receiveLineWithTimeout(in2);//ok
        receiveLineWithTimeout(in);//test2 joined

        String message = "testing";
        out.println("PRV_BCST test2 " + message);
        out.flush();

        String serverResponse = receiveLineWithTimeout(in);
        String serverResponse2 = receiveLineWithTimeout(in2);

        assertEquals("PRV_BCST test testing", serverResponse2);
        assertEquals("PRV_BCST_OK testing", serverResponse);

    }

    @Test
    void TC7_2_privateMessageSendToIncorrectUsername() {
        receiveLineWithTimeout(in); //init message
        out.println("IDENT test");
        out.flush();
        receiveLineWithTimeout(in);//ok

        receiveLineWithTimeout(in2); //init message
        out2.println("IDENT test2");
        out2.flush();
        receiveLineWithTimeout(in2);//ok
        receiveLineWithTimeout(in);//test2 joined

        String message = "testing";
        out.println("PRV_BCST test3 " + message);
        out.flush();

        String serverResponse = receiveLineWithTimeout(in);

        assertEquals("FAIL07 usernames are incorrect. ", serverResponse);
    }



    @Test
    void TC7_3_privateMessageSendWithoutLoggingIn() {
        receiveLineWithTimeout(in); //init message
        out.println("IDENT test");
        out.flush();
        receiveLineWithTimeout(in);//ok

        receiveLineWithTimeout(in2); //init message
        out2.println("PRV_BCST test1 testing");
        out2.flush();
        String serverResponse = receiveLineWithTimeout(in2);//error



        assertEquals("FAIL03 Please log in first", serverResponse);
    }


    private String receiveLineWithTimeout(BufferedReader reader){
        return assertTimeoutPreemptively(ofMillis(max_delta_allowed_ms), reader::readLine);
    }
}
