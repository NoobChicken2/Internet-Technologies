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
    private int sessionNumber;

    public FileTransferSession(FileTransferThread instance, InputStream inputStream, OutputStream outputStream, Socket socket, int sessionNumber) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.fileTransferThreadInstance = instance;
        this.socket = socket;
        this.sessionNumber = sessionNumber;
    }

    @Override
    public void run() {
        // Identify
        identifyFirstBytes();

        // Runs depending on identifier
        System.out.println("Current: " + sessionNumber + " " + fileTransferThreadInstance.getMatchingIdentifierSession(identifier).getSessionNumber());
        if (fileTransferThreadInstance.checkForMatchingIdentifier(identifier)) {
            if(sessionNumber > fileTransferThreadInstance.getMatchingIdentifierSession(identifier).getSessionNumber()) {
                System.out.println("ran");
                transferBytes();
            }
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
    private void transferBytes() {
        FileTransferSession matchingSession = fileTransferThreadInstance.getMatchingIdentifierSession(identifier);

        if (identifier.indexOf('U') != -1) {
            try {
                inputStream.transferTo(matchingSession.getOutputStream());
            }catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                matchingSession.getInputStream().transferTo(outputStream);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Remove these instances from the map
        fileTransferThreadInstance.removeFileTransferRequest(identifier);
        fileTransferThreadInstance.removeFileTransferRequest(matchingSession.getIdentifier());

        // Close their sockets
        closeSocket();
        matchingSession.closeSocket();
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
    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int getSessionNumber() {
        return sessionNumber;
    }
}
