package Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransfer implements Runnable{
    private ServerSocket fileTransferSocket;

    public FileTransfer(ServerSocket fileTransferSocket) {
        this.fileTransferSocket = fileTransferSocket;
    }
    @Override
    public void run() {
        while (true) {
            Socket socket = getSocket();

            InputStream inputStream = getInputStream(socket);
            OutputStream outputStream = getOutputStream(socket);

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
}
