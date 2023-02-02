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

public class ClientListTest {

        private static final Properties props = new Properties();
        private Socket s;
        private BufferedReader in;
        private PrintWriter out;

        private Socket s2;
        private BufferedReader in2;
        private PrintWriter out2;

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
        void TC6_1_getClientListWithOneUser() {
                receiveLineWithTimeout(in); //init message
                out.println("IDENT test");
                out.flush();
                receiveLineWithTimeout(in);
                out.println("LIST_REQUEST");
                out.flush();
                String serverResponse = receiveLineWithTimeout(in);
                assertEquals("LIST_RESPONSE ", serverResponse);
        }
        @Test
        void TC6_2_getClientListWithTwoUsers() {
                receiveLineWithTimeout(in); //init message
                out.println("IDENT test");
                out.flush();
                receiveLineWithTimeout(in);

                receiveLineWithTimeout(in2); //init message
                out2.println("IDENT test2");
                out2.flush();
                receiveLineWithTimeout(in2);
                receiveLineWithTimeout(in);
                out.println("LIST_REQUEST");
                out.flush();

                String serverResponse = receiveLineWithTimeout(in);
                assertEquals("LIST_RESPONSE test2 ", serverResponse);
        }
        @Test
        void TC6_3_getClientListWithoutLoggingIn() {
                receiveLineWithTimeout(in); //init message
                out.println("LIST_REQUEST");
                out.flush();
                String serverResponse = receiveLineWithTimeout(in);
                String []responseText = serverResponse.split(" ");
                assertEquals("FAIL03", responseText[0]);
        }
        private String receiveLineWithTimeout(BufferedReader reader){
                return assertTimeoutPreemptively(ofMillis(max_delta_allowed_ms), reader::readLine);
        }
}
