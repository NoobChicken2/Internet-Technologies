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

public class PerformanceTests {

    private static final Properties props = new Properties();

    private Socket s,s2,s3,s4,s5,s6,s7,s8,s9,s10;
    private BufferedReader in,in2,in3,in4,in5,in6,in7,in8,in9,in10;
    private PrintWriter out,out2,out3,out4,out5,out6,out7,out8,out9,out10;

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

        s4 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in4 = new BufferedReader(new InputStreamReader(s4.getInputStream()));
        out4 = new PrintWriter(s4.getOutputStream(), true);

        s5 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in5 = new BufferedReader(new InputStreamReader(s5.getInputStream()));
        out5 = new PrintWriter(s5.getOutputStream(), true);

        s6 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in6 = new BufferedReader(new InputStreamReader(s6.getInputStream()));
        out6 = new PrintWriter(s6.getOutputStream(), true);

        s7 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in7 = new BufferedReader(new InputStreamReader(s7.getInputStream()));
        out7 = new PrintWriter(s7.getOutputStream(), true);

        s8 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in8 = new BufferedReader(new InputStreamReader(s8.getInputStream()));
        out8 = new PrintWriter(s8.getOutputStream(), true);

        s9 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in9 = new BufferedReader(new InputStreamReader(s9.getInputStream()));
        out9 = new PrintWriter(s9.getOutputStream(), true);

        s10 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        in10 = new BufferedReader(new InputStreamReader(s10.getInputStream()));
        out10 = new PrintWriter(s10.getOutputStream(), true);
    }

    @AfterEach
    void cleanup() throws IOException {
        s.close();
        s2.close();
        s3.close();
        s4.close();
        s5.close();
        s6.close();
        s7.close();
        s8.close();
        s9.close();
        s10.close();

    }

    @Test
    void TC11_1_LoggingInWithTenClientsAndSendingPrivateMessageRetunsOK() throws InterruptedException {
        receiveLineWithTimeout(in); //init message
        out.println("IDENT test");
        out.flush();
        receiveLineWithTimeout(in);//ok

        receiveLineWithTimeout(in2); //init message
        out2.println("IDENT test2");
        out2.flush();
        receiveLineWithTimeout(in2);//ok
        receiveLineWithTimeout(in);//JOINED

        receiveLineWithTimeout(in3); //init message
        out3.println("IDENT test3");
        out3.flush();
        receiveLineWithTimeout(in3);//ok
        receiveLineWithTimeout(in);//JOINED

        receiveLineWithTimeout(in4); //init message
        out4.println("IDENT test4");
        out4.flush();
        receiveLineWithTimeout(in4);//ok
        receiveLineWithTimeout(in);//JOINED

        receiveLineWithTimeout(in5); //init message
        out5.println("IDENT test5");
        out5.flush();
        receiveLineWithTimeout(in5);//ok
        receiveLineWithTimeout(in);//JOINED

        receiveLineWithTimeout(in6); //init message
        out6.println("IDENT test6");
        out6.flush();
        receiveLineWithTimeout(in6);//ok
        receiveLineWithTimeout(in);//JOINED

        receiveLineWithTimeout(in7); //init message
        out7.println("IDENT test7");
        out7.flush();
        receiveLineWithTimeout(in7);//ok
        receiveLineWithTimeout(in);//JOINED

        receiveLineWithTimeout(in8); //init message
        out8.println("IDENT test8");
        out8.flush();
        receiveLineWithTimeout(in8);//ok
        receiveLineWithTimeout(in);//JOINED

        receiveLineWithTimeout(in9); //init message
        out9.println("IDENT test9");
        out9.flush();
        receiveLineWithTimeout(in9);//ok
        receiveLineWithTimeout(in);//JOINED

        receiveLineWithTimeout(in10); //init message
        out10.println("IDENT test10");
        out10.flush();
        receiveLineWithTimeout(in10);//ok
        receiveLineWithTimeout(in);//JOINED

        out.println("PRV_BCST test10 test");
        out.flush();
        String serverResponse =receiveLineWithTimeout(in);
        assertEquals("PRV_BCST_OK test", serverResponse);
    }
    private String receiveLineWithTimeout(BufferedReader reader){
        return assertTimeoutPreemptively(ofMillis(max_delta_allowed_ms), reader::readLine);
    }
}

