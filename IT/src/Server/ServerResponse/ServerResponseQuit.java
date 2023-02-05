package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.IOException;

public class ServerResponseQuit implements ServerResponse {
    private MessageProcessor mp;

    public ServerResponseQuit(MessageProcessor mp) {
        this.mp = mp;
    }

    @Override
    public void respond(String message) {
        try {
            if (mp.checkClientLoggedIn()) {
                quit(message);
                mp.setExit(true);
                //Stops the heartbeat thread of the message processor
                mp.stopHeartbeat();
            } else {
                mp.getServer().removeClient(mp.getName());
                mp.socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void quit(String message) throws IOException {
        mp.sendMessage(message);
        mp.getServer().removeClient(mp.getName());
        mp.socket.close();
    }
}
