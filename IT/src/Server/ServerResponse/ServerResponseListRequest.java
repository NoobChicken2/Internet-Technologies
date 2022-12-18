package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerResponseListRequest implements ServerResponse{
    private MessageProcessor mp;
    public ServerResponseListRequest(MessageProcessor mp) {
        this.mp=mp;
    }

    @Override
    public void respond(String request) {
        if (mp.checkClientLoggedIn()) {
            getListOfClients();
        }
    }
    private void getListOfClients(){
        String message= "LIST_RESPONSE";
        message=message+ Server.getClientList(mp.getName());
        mp.sendMessage(message);
    }
}
