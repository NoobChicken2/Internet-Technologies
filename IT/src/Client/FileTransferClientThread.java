package Client;

import Client.Utils.ClientUtils;

import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

public class FileTransferClientThread implements Runnable{

    private String identifier;
    private String fileName;
    private String checksum;
    private Socket fileTransferSocket = connectSocket();

    public FileTransferClientThread(String identifier, String fileName, String checksum) {
        this.identifier = identifier;
        this.fileName = fileName;
        this.checksum = checksum;
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
        FileOutputStream os;

        try {
            os = new FileOutputStream(getDownloadPath());
            fileTransferInputStream().transferTo(os);
            os.close();

            // Compares the checksum of the file to the original checksum value
            if (ClientUtils.checksum(getDownloadPath()).equals(checksum)) {
                System.out.println("Download finished! File integrity is okay");
            } else {
                System.out.println("Download finished! File integrity is NOT okay");
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
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
