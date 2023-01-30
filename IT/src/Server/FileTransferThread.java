package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class FileTransferThread implements Runnable{
    private static int fileTransferIdentifier = 0;
    private ServerSocket fileTransferSocket;
    public Map<String, FileTransferClass> fileTransferRequests;
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

            FileTransferSession newSession = new FileTransferSession(inputStream, outputStream);
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
    public static int getNewIdentifier() {
        fileTransferIdentifier ++;
        return fileTransferIdentifier;
    }
}
