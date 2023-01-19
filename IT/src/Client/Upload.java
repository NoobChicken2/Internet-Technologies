package Client;

import java.io.IOException;
import java.io.InputStream;

public class Upload implements Runnable{
    @Override
    public void run() {

    }

    private InputStream fileTransferInputStream() {
        InputStream inputStream;
        try {
            inputStream = Client.getClientFileTransferSocket().getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
    }
}
