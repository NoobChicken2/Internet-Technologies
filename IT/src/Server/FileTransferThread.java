package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class FileTransferThread implements Runnable{
    private ServerSocket fileTransferSocket;
    private Map<String, FileTransferSession> fileTransferRequests = new HashMap();
    public FileTransferThread() {
        fileTransferSocket = Server.getFileTransferSocket();
    }
    @Override
    public void run() {
        System.out.println("File transfer Socket Thread started");
        while (true) {
            Socket socket = getSocket();

            InputStream inputStream = getInputStream(socket);
            OutputStream outputStream = getOutputStream(socket);

            FileTransferSession newSession = new FileTransferSession(this, inputStream, outputStream);
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
    public OutputStream getDownloaderOutputStream(String identifier) {
        String downloaderIdentifier = "D" + identifier.substring(1);
        OutputStream os = null;
        for(String key: fileTransferRequests.keySet()){
            if(key.equals(downloaderIdentifier)) {
                os = fileTransferRequests.get(key).getOutputStream();
            }
        }
        return os;
    }
    public InputStream getUploaderInputStream(String identifier) {
        String uploaderIdentifier = "U" + identifier.substring(1);
        InputStream is = null;
        for(String key: fileTransferRequests.keySet()){
            if(key.equals(uploaderIdentifier)) {
                is = fileTransferRequests.get(key).getInputStream();
            }
        }
        return is;
    }
}