package Server.ServerResponse;

import Server.MessageProcessor;

public class ServerFileTransferResponse implements ServerResponse{
    private MessageProcessor messageProcessor;

    public ServerFileTransferResponse(MessageProcessor mp) {
        this.messageProcessor = mp;
    }

    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response[1].equals("declined")) {
            System.out.println("Transfer declined!");
            return;
        }
    }
}
