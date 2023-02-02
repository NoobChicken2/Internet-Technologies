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

    public ClientRequestFileTransfer(Client client) {
        this.client=client;
    }

    @Override
    public void request(int menuValue) throws InterruptedException, NoSuchAlgorithmException, IOException {
        switch(menuValue) {
            case 6 -> {
                startFileTransfer();
            }
            case 9, 0 -> {
                checkFileTransferAcceptInput(menuValue);
            }
        }
    }
    private void startFileTransfer() throws NoSuchAlgorithmException, IOException {
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
    private void checkFileTransferAcceptInput(int menuValue) {
        if (!client.getTransferRequest()) {
            System.out.println("You are not allowed to do this right now!");
            return;
        }
        if (menuValue == 9) {
            client.getClientInputListener().setCommand("TRANSFER_RES accepted " + client.getLastTransferRequestUser() + " " + client.getLastTransferRequestFileName());
            System.out.println("You have accepted the file transfer. The download will start shortly.");
        } else {
            client.getClientInputListener().setCommand("TRANSFER_RES declined " + client.getLastTransferRequestUser());
        }
        client.setLastTransferRequestUser("");
        client.setLastTransferRequestFileName("");
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
}
