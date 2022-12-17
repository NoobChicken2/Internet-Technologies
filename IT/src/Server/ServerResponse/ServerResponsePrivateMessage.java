package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerResponsePrivateMessage extends MessageProcessor implements ServerResponse{

    public ServerResponsePrivateMessage() {
    }

    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if name is null
            sendMessage("FAIL02 Username has an invalid format or length");
        }else {
            if (checkClientLoggedIn()) {
                sendPrivateMessage(response[1], response[2]);
            }
        }
    }

    private void sendPrivateMessage(String receiver, String privateMessage) {
        sendMessage("PRV_BCST_OK "+ privateMessage);
        String message = "PRV_BCST " + this.name + " " + privateMessage;
        Server.privateMessage(receiver, message);
    }
}
