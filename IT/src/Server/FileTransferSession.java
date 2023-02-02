package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileTransferSession implements Runnable{
    private Socket socket;
    private FileTransferThread fileTransferThreadInstance;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String identifier;

    public FileTransferSession(FileTransferThread instance, InputStream inputStream, OutputStream outputStream, Socket socket) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.fileTransferThreadInstance = instance;
        this.socket = socket;
    }

    @Override
    public void run() {
        // Identify
        identifyFirstBytes();

        // Run depending on identifier
        if (identifier.indexOf('U') != -1) {
            upload();
        } else if (identifier.indexOf('D') != -1) {
            download();
        }
//        fileTransferThreadInstance.removeFileTransferRequest(identifier);

        // Close socket connection after the upload or download
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void identifyFirstBytes() {
        try {
            byte[] inputBytes = new byte[37];
            inputStream.readNBytes(inputBytes,0,inputBytes.length);
            identifier = new String(inputBytes);
            fileTransferThreadInstance.addTransferRequest(identifier, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void upload() {
        OutputStream downloaderOS = fileTransferThreadInstance.getDownloaderOutputStream(identifier);
        try {
            inputStream.transferTo(downloaderOS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void download() {
        InputStream uploaderIS = fileTransferThreadInstance.getUploaderInputStream(identifier);
        try {
            uploaderIS.transferTo(outputStream);
        } catch (IOException e) {
            for(String key:fileTransferThreadInstance.getMap().keySet()) {
                System.out.println(key);
            }
        }
    }
    public String getIdentifier() {
        return identifier;
    }
    public OutputStream getOutputStream() {
        return outputStream;
    }
    public InputStream getInputStream() {
        return inputStream;
    }
}
