package Server.FileTransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileTransferThread implements Runnable{
    private final int SERVER_PORT_FT = 8081;
    private ServerSocket fileTransferSocket = openFileTransferSocket();
    private Map<String, FileTransferSession> fileTransferRequests = Collections.synchronizedMap(new HashMap<>());
    private int sessionCount = 0;

    @Override
    public void run() {
        System.out.println("File transfer Socket Thread started");
        while (true) {
            Socket socket = getSocket();
            sessionCount++;

            InputStream inputStream = getInputStream(socket);
            OutputStream outputStream = getOutputStream(socket);

            FileTransferSession newSession = new FileTransferSession(this, inputStream, outputStream, socket, sessionCount);
            new Thread(newSession).start();
        }
    }
    private Socket getSocket() {
        Socket socket;
        try {
            socket = fileTransferSocket.accept();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }
    private InputStream getInputStream(Socket socket) {
        InputStream inputStream;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
    }
    private OutputStream getOutputStream(Socket socket) {
        OutputStream outputStream;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream;
    }
    public void addTransferRequest(String identifier, FileTransferSession session) {
        fileTransferRequests.put(identifier, session);
    }
    public void removeFileTransferRequest(String identifier) {
        fileTransferRequests.remove(identifier);
    }
    private String getMatchingIdentifier(String identifier) {
        String otherIdentifier;
        if (identifier.indexOf('U') != -1) {
            otherIdentifier = "D" + identifier.substring(1);
        } else {
            otherIdentifier = "U" + identifier.substring(1);
        }
        return otherIdentifier;
    }
    public boolean checkForMatchingIdentifier(String identifier) {
        String otherIdentifier = getMatchingIdentifier(identifier);
        for(String key: fileTransferRequests.keySet()){
            if(key.equals(otherIdentifier)) {
                return true;
            }
        }
        return false;
    }
    public FileTransferSession getMatchingIdentifierSession(String identifier) {
        return findSession(getMatchingIdentifier(identifier));
    }
    private FileTransferSession findSession(String identifier) {
        FileTransferSession session = null;
        for(String key: fileTransferRequests.keySet()){
            if(key.equals(identifier)) {
                session = fileTransferRequests.get(key);
            }
        }
        return session;
    }
    private ServerSocket openFileTransferSocket() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(SERVER_PORT_FT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }
}
