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
        if (mp.checkClientLoggedIn()) {
            try {
                quit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mp.setExit(true);
        }
    }

    private void quit() throws IOException {
        String message = "QUIT_OK";
        mp.sendMessage(message);
        Server.broadcastMessage("DISCONNECTED " + mp.getName(), mp.getName());
        Server.clients.remove(mp.getName());
        mp.socket.close();
    }
}
