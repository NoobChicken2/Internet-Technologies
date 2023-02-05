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

public class FileTransferTests {
    private static Properties props = new Properties();

    private Socket socketUser1, socketUser2;
    private BufferedReader inUser1, inUser2;
    private PrintWriter outUser1, outUser2;

    private final static int max_delta_allowed_ms = 1000;

    @BeforeAll
    static void setupAll() throws IOException {
        InputStream in = MultipleUserTests.class.getResourceAsStream("testconfig.properties");
        props.load(in);
        in.close();
    }

    @BeforeEach
    void setup() throws IOException {
        socketUser1 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        inUser1 = new BufferedReader(new InputStreamReader(socketUser1.getInputStream()));
        outUser1 = new PrintWriter(socketUser1.getOutputStream(), true);

        socketUser2 = new Socket(props.getProperty("host"), Integer.parseInt(props.getProperty("port")));
        inUser2 = new BufferedReader(new InputStreamReader(socketUser2.getInputStream()));
        outUser2 = new PrintWriter(socketUser2.getOutputStream(), true);
    }

    @AfterEach
    void cleanup() throws IOException {
        socketUser1.close();
        socketUser2.close();
    }

    @Test
    void TC10_fileTransferTest() {
        receiveLineWithTimeout(inUser1); //INIT
        receiveLineWithTimeout(inUser2); //INIT

        // Connect user1
        outUser1.println("IDENT user1");
        outUser1.flush();
        receiveLineWithTimeout(inUser1); //OK

        // Connect user2
        outUser2.println("IDENT user2");
        outUser2.flush();
        receiveLineWithTimeout(inUser2); //OK

        outUser1.println("TRANSFER user2 C:\\Users\\HP\\Desktop\\49\\IT\\TransferUpload\\Hello.txt 5c7a6c971b823d8a6e5c82288eb7087e");
        outUser1.flush();
        receiveLineWithTimeout(inUser1); // TRANSFER_OK

        String fromUser2 = receiveLineWithTimeout(inUser2); // TRANSFER_REQ
        assertEquals("TRANSFER_REQ user1 Hello.txt 5c7a6c971b823d8a6e5c82288eb7087e 0 GB 0 MB 0 KB 39 B", fromUser2);

        outUser2.println("TRANSFER_RES accepted user1");
        outUser2.flush();

        String[] transferAcceptedDownloader = receiveLineWithTimeout(inUser2).split(" ");
        String[] transferAcceptedUploader = receiveLineWithTimeout(inUser1).split(" ");

        assertEquals("TRANSFER_ACCEPTED Hello.txt", transferAcceptedDownloader[0] + " " + transferAcceptedDownloader[2]);
        assertEquals("TRANSFER_ACCEPTED Hello.txt", transferAcceptedUploader[0] + " " + transferAcceptedUploader[2]);
    }

    private String receiveLineWithTimeout(BufferedReader reader) {
        return assertTimeoutPreemptively(ofMillis(max_delta_allowed_ms), () -> reader.readLine());
    }
}
