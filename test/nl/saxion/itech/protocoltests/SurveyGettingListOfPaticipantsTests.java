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

public class SurveyGettingListOfPaticipantsTests {

    private static final Properties props = new Properties();

    private Socket s, s2,s3;
    private BufferedReader in, in2, in3;
    private PrintWriter out, out2, out3;

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

        s3 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in3 = new BufferedReader(new InputStreamReader(s3.getInputStream()));
        out3 = new PrintWriter(s3.getOutputStream(), true);
        receiveLineWithTimeout(in); //init message
        out.println("IDENT test");
        out.flush();
        receiveLineWithTimeout(in);//ok

        receiveLineWithTimeout(in2); //init message
        out2.println("IDENT test2");
        out2.flush();
        receiveLineWithTimeout(in2);//ok
        receiveLineWithTimeout(in);//test2 joined

        receiveLineWithTimeout(in3); //init message
        out3.println("IDENT test3");
        out3.flush();
        receiveLineWithTimeout(in3);//ok
        receiveLineWithTimeout(in);//test3 joined

        out.println("SURVEY START");
        out.flush();
        receiveLineWithTimeout(in);//Survey OK
    }

    @AfterEach
    void cleanup() throws IOException {
        s.close();
        s2.close();
        s3.close();

    }
    @Test
    void TC9_1_ReceivingClientListAfterQStopReturnsOK() {
        out.println("SURVEY Q /How much is 1+1? /1/2/3/4");
        out.flush();
        receiveLineWithTimeout(in);

        out.println("SURVEY Q_STOP");
        out.flush();
        receiveLineWithTimeout(in);
        String serverResponse = receiveLineWithTimeout(in);

        assertEquals("SURVEY_LIST test2 test3 ", serverResponse);
    }
    @Test
    void TC9_2_ReceivingClientListAfterQStopWithoutSendingQuestionsReturnsError() {
        out.println("SURVEY Q_STOP");
        out.flush();
        String serverResponse = receiveLineWithTimeout(in);

        assertEquals("FAIL06 Invalid question or wrong number of answers", serverResponse);
    }
    @Test
    void TC9_3_SendingClientListCorrectlyReturnsOK() {
        out.println("SURVEY Q /How much is 1+1? /1/2/3/4");
        out.flush();
        receiveLineWithTimeout(in);

        out.println("SURVEY Q_STOP");
        out.flush();
        receiveLineWithTimeout(in);

        out.println("SURVEY LIST_RESPONSE /test2/test3");
        out.flush();
        receiveLineWithTimeout(in);
        String serverResponse = receiveLineWithTimeout(in);

        assertEquals("SURVEY_LIST_OK", serverResponse);
    }
    @Test
    void TC9_4_SendingClientListIncorrectNamesReturnsError() {
        out.println("SURVEY Q /How much is 1+1? /1/2/3/4");
        out.flush();
        receiveLineWithTimeout(in);

        out.println("SURVEY Q_STOP");
        out.flush();
        receiveLineWithTimeout(in);

        out.println("SURVEY LIST_RESPONSE /test5/test3");
        out.flush();
        receiveLineWithTimeout(in);
        String serverResponse = receiveLineWithTimeout(in);

        assertEquals("FAIL07 usernames are incorrect", serverResponse);
    }
    private String receiveLineWithTimeout(BufferedReader reader){
        return assertTimeoutPreemptively(ofMillis(max_delta_allowed_ms), reader::readLine);
    }
}

