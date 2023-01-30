package Client;

import java.io.*;
import java.util.UUID;

public class FileTransferClientThread implements Runnable{

    private String identifier;
    private String fileName;

    public FileTransferClientThread(String identifier, String fileName) {
        this.identifier = identifier;
        this.fileName = fileName;
    }
    @Override
    public void run() {
        if (identifier.indexOf('U') != -1) {
            upload();
        } else {
            download();
        }
    }
    private InputStream fileTransferInputStream() {
        InputStream inputStream;
        try {
            inputStream = Client.getClientFileTransferSocket().getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inputStream;
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
    private String getDownloadPath() {
        return new File("").getAbsolutePath() + "\\TransferUpload\\" + fileName + " " + identifier;
    }
    private String getUploadPath() {
        return new File("").getAbsolutePath() + "\\TransferUpload\\" + fileName;
    }
    private void upload() {
        System.out.println("I am uploading");
        FileInputStream in;

        try {
            in = new FileInputStream(getUploadPath());
//            UUID id = UUID.randomUUID();
//
//            byte[] a = id.toString().getBytes();
//            fileTransferOutputStream().write(a);
//
//            fileTransferInputStream().readNBytes()
//
//            fileTransferOutputStream().write(identifier);
            in.transferTo(fileTransferOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void download() {
        System.out.println("I am downloading");
        FileOutputStream os;

        try {
            os = new FileOutputStream(getDownloadPath());
            fileTransferInputStream().transferTo(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
