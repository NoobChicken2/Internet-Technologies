package Server.ServerResponse;

import GlobalUtilities.Utils;
import Server.MessageProcessor;
import Server.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerResponsePrivateMessage implements ServerResponse{
    private MessageProcessor mp;
    public ServerResponsePrivateMessage(MessageProcessor mp) {
        this.mp=mp;
    }
    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if there are any arguments after header
            mp.sendMessage("FAIL00 Unkown command");
        }else {
            if (mp.checkClientLoggedIn()) {
                sendPrivateMessage(response[1], Utils.combinedMessage(2, response));
            }
        }
    }

    private void sendPrivateMessage(String receiver, String privateMessage) {
        mp.sendMessage("PRV_BCST_OK "+ privateMessage);
        String message = "PRV_BCST " + mp.getName() + " " + privateMessage;
        mp.getServer().messageClient(receiver, message);
    }

}
