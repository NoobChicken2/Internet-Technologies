package Server;

import java.io.InputStream;
import java.io.OutputStream;

public class FileTransferSession implements Runnable{
    private InputStream inputStream;
    private OutputStream outputStream;
    private String role;

    public FileTransferSession(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {

    }

    public String getRole() {
        return role;
    }
}
