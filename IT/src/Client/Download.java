package Client;

import java.io.IOException;
import java.io.OutputStream;

public class Download implements Runnable{
    @Override
    public void run() {

    }
    private OutputStream fileTransferOutputStream() {
        OutputStream outputStream;
        try {
            outputStream = Client.getClientFileTransferSocket().getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream;
    }
}
