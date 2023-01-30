package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.IOException;

public class ServerResponseQuit implements ServerResponse{
    private MessageProcessor mp;
    public ServerResponseQuit(MessageProcessor mp) {
        this.mp=mp;
    }

    @Override
    public void respond(String message) {
        if (mp.checkClientLoggedIn()) {
            try {
                quit(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mp.setExit(true);
            //Stops the heartbeat thread of the message processor
            mp.stopHeartbeat();
        }
    }

    private void quit(String message) throws IOException {
        mp.sendMessage(message);
        mp.getServer().broadcastMessageToEveryone("DISCONNECTED " + mp.getName(), mp.getName());
        mp.getServer().removeClient(mp.getName());
        mp.socket.close();
    }
}
