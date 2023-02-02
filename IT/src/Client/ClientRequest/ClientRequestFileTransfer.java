package Client.ClientRequest;

import Client.Client;
import Client.Utils.ClientUtils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class ClientRequestFileTransfer implements ClientRequest{
    private Client client;
    private Socket fileTransferSocket;

    public ClientRequestFileTransfer(Client client) {
        this.client=client;
        this.fileTransferSocket = new Socket();
        this.client.setTransferRequest(false);
        this.client.setLastTransferRequestUser("");
    }

    @Override
    public void request() throws InterruptedException, NoSuchAlgorithmException, IOException {
        String input;
        System.out.print("Enter the username of the user you want to transfer a file: ");
        input = ClientUtils.getUserInputString();
        System.out.print("Enter the name of the file that you want to transfer from the TransferUpload folder: ");
        String fileName = ClientUtils.getUserInputString();
        if (filePathFound(fileName)) {
            String pathString = getFullPathString(fileName);
            input += " " + fileName + " " + getFileSizeBytes(pathString) + " " + ClientUtils.checksum(pathString);
        }
        client.getClientInputListener().setCommand("TRANSFER " + input);

    }
    private boolean filePathFound(String fileName) {
        String file = new File("").getAbsolutePath() + "\\IT\\TransferUpload" + "\\" + fileName.trim();
        File filePath = new File(file);
        return filePath.exists();
    }
    private String getFullPathString(String fileName){
        return new File("").getAbsolutePath() + "\\IT\\TransferUpload" + "\\" + fileName.trim();
    }
    private long getFileSizeBytes(String pathString){
        Path path = Paths.get(pathString);
        long bytes = 0;
        try {
            bytes = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public Socket getFileTransferSocket() {
        return fileTransferSocket;
    }

    public void setFileTransferSocket(Socket fileTransferSocket) {
        this.fileTransferSocket = fileTransferSocket;
    }
}
