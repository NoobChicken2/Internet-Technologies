package Server.ServerResponse;

import Server.MessageProcessor;

import java.util.UUID;

public class ServerFileTransfer implements ServerResponse{

    private MessageProcessor messageProcessor;

    public ServerFileTransfer(MessageProcessor mp) {
        this.messageProcessor = mp;
    }

    @Override
    public void respond(String request) {
        String[] splitRequest = request.split(" ");
        switch (splitRequest[0]) {
            case "TRANSFER" -> {
                // Request is when the sender client starts a file transfer
                fileTransferRequest(splitRequest);
            }
            case "TRANSFER_RES" -> {
                // Response is when the receiving client responds to the FT request
                fileTransferResponse(splitRequest);
            }
        }
    }

    private boolean checkUsername(String username, String messageProcessorName) {
        if (messageProcessorName.equals(username)) {
            return false;
        } else {
            return messageProcessor.getServer().checkIfClientExists(username);
        }
    }
    private String getFileSize(long bytes) {
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

    // File Transfer Request variables
    // request[1] -> Receiving client name
    // request[2] -> File name
    // request[3] -> File size
    // request[4] -> checksum
    private void fileTransferRequest(String[] request) {
        if (!checkUsername(request[1], messageProcessor.getName())) {
            messageProcessor.sendMessage("FAIL06 User was not found");
            return;
        }
        if (request.length < 3) {
            messageProcessor.sendMessage("FAIL10 Incorrect path");
            return;
        }
        messageProcessor.sendMessage("TRANSFER_OK");

        String notifyMessage = "TRANSFER_REQ " + messageProcessor.getName() + " " + request[2] + " " + request[4] + " " + getFileSize(Long.parseLong(request[3]));
        messageProcessor.getServer().messageClient(request[1].trim(), notifyMessage);
    }

    // File Transfer Response variables
    // request[1] -> The response the downloader sends back (accepted or declined)
    // request[2] -> Client name used to find and message a different message processor
    // request[3] -> File name
    private void fileTransferResponse(String[] request) {
        switch (request[1]) {
            case "declined" -> {
                messageProcessor.getServer().messageClient(request[2], "TRANSFER_DECLINED");
            }
            case "accepted" -> {
                UUID id = UUID.randomUUID();
                String identifier = id.toString();
                messageProcessor.getServer().messageClient(request[2], "TRANSFER_ACCEPTED U" + identifier + " " + request[3]);
                messageProcessor.sendMessage("TRANSFER_ACCEPTED D" + identifier + " " + request[3]);
            }
        }
    }
}
