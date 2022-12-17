package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerResponseListRequest extends MessageProcessor implements ServerResponse{

    public ServerResponseListRequest() {
        super();
    }

    @Override
    public void respond(String request) {
        if (checkClientLoggedIn()) {
            getListOfClients();
        }
    }
    private void getListOfClients(){
        String message= "LIST_RESPONSE";
        message=message+ Server.getClientList(this.name);
        sendMessage(message);
    }
}
