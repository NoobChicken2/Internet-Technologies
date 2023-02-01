package Client;

import java.io.*;
import java.net.Socket;

public class FileTransferClientThread implements Runnable{

    private String identifier;
    private String fileName;
    private Socket fileTransferSocket = connectSocket();

    public FileTransferClientThread(String identifier, String fileName) {
        this.identifier = identifier;
        this.fileName = fileName;
    }
    @Override
    public void run() {
        byte[] identifierBytes = identifier.getBytes();
        try {
            fileTransferOutputStream().write(identifierBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (identifier.indexOf('U') != -1) {
            upload();
        } else {
            download();
        }
    }
    private InputStream fileTransferInputStream() {
        InputStream inputStream;
        try {
            inputStream = fileTransferSocket.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
    }
    private OutputStream fileTransferOutputStream() {
        OutputStream outputStream;
        try {
            outputStream = fileTransferSocket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream;
    }
    private String getDownloadPath() {
        return new File("").getAbsolutePath() + "\\IT\\TransferDownload\\" + fileName.split("\\.")[0] + " " + identifier + "." + fileName.split("\\.")[1];
    }
    private String getUploadPath() {
        return new File("").getAbsolutePath() + "\\IT\\TransferUpload\\" + fileName;
    }
    private void upload() {
        System.out.println("I am uploading");
        System.out.println(identifier);
        FileInputStream in;

        try {
            in = new FileInputStream(getUploadPath());
            in.transferTo(fileTransferOutputStream());
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Upload finished");
    }
    private void download() {
        System.out.println("I am downloading");
        System.out.println(identifier);
        FileOutputStream os;

        try {
            os = new FileOutputStream(getDownloadPath());
            fileTransferInputStream().transferTo(os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Download finished");
    }

    private Socket connectSocket() {
        Socket socket;
        try {
            socket = new Socket("127.0.0.1", 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return socket;
    }
}
