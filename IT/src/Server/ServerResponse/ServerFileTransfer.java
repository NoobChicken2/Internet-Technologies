package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

public class ServerFileTransfer implements ServerResponse{

    private MessageProcessor messageProcessor;

    public ServerFileTransfer(MessageProcessor mp) {
        this.messageProcessor = mp;
    }

    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (!checkUsername(response[1])) {
            messageProcessor.sendMessage("FAIL06 User was not found");
            return;
        }
        if (response.length < 3) {
            messageProcessor.sendMessage("FAIL10 Incorrect path");
            return;
        }
        messageProcessor.sendMessage("TRANSFER_OK");

        String notifyMessage = "TRANSFER_REQ The user " + messageProcessor.getName() + " wants to transfer a file to you named " + response[3] + " with the size " + getFileSize(Long.parseLong(response[4])) + ". Enter 9 to accept and 0 to decline!";
        Server.messageClient(response[1].trim(), notifyMessage);
    }

    private boolean checkUsername(String username) {
        String[] users = Server.getClientList(messageProcessor.getName()).split(" ");
        boolean usernameFound = false;
        for (String user : users) {
            if (username.trim().equals(user)) {
                usernameFound = true;
                break;
            }
        }
        return usernameFound;
    }
    private static String getFileSize(long bytes) {
        int kiloBytes = 0;
        int megaBytes = 0;
        int gigaBytes = 0;
        long bytesLeft = bytes;
        if (!(bytes % 1073741824 == bytes)) {
            gigaBytes = (int)bytes / 1073741824;
            bytesLeft = bytes % 1073741824;
            bytes = bytesLeft;
        }
        if (!(bytes % 1048576 == bytes)) {
            megaBytes = (int)bytes / 1048576;
            bytesLeft = bytes % 1048576;
            bytes = bytesLeft;
        }
        if (!(bytes % 1024 == bytes)) {
            kiloBytes = (int)bytes / 1024;
            bytesLeft = bytes % 1024;
        }

        return gigaBytes + " GB " + megaBytes + " MB " + kiloBytes + " KB " + bytesLeft + " B";
    }
}
