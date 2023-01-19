package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerResponseQuit implements ServerResponse{
    private MessageProcessor mp;
    public ServerResponseQuit(MessageProcessor mp) {
        this.mp=mp;
    }

    @Override
    public void respond(String request) {
        String[] quitType = request.split(" ");
        if (mp.checkClientLoggedIn()) {
            try {
                quit(quitType[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mp.setExit(true);
            //Stops the heartbeat thread of the message processor
            mp.stopHeartbeat();
        }
    }

    private void quit(String quitType) throws IOException {
        String message = "QUIT_OK";
        if (quitType.equals("DSCN")) {
            message = "DSCN";
        }
        mp.sendMessage(message);
        Server.broadcastMessage("DISCONNECTED " + mp.getName(), mp.getName());
        Server.clients.remove(mp.getName());
        mp.getSocket().close();
    }
}
