package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerResponseBroadcast extends MessageProcessor implements ServerResponse{

    public ServerResponseBroadcast() {
    }

    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if name is null
            sendMessage("FAIL02 Username has an invalid format or length");
        }else {
            if (checkClientLoggedIn()) {
                broadcast(response[1]);
            }
        }

    }
    private void broadcast(String BroadcastMessage){
        String message = "BCST_OK " + BroadcastMessage;
        sendMessage(message);
        Server.broadcastMessage("BCST " + this.name + " " + BroadcastMessage, this.name);

    }
}
