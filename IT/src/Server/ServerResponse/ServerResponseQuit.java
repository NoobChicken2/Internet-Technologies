package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerResponseQuit extends MessageProcessor implements ServerResponse{
    public ServerResponseQuit() {
        super();
    }

    @Override
    public void respond(String request) {
        if (checkClientLoggedIn()) {
            try {
                quit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            exit = true;
        }
    }

    private void quit() throws IOException {
        String message = "QUIT_OK";
        sendMessage(message);
        Server.broadcastMessage("DISCONNECTED " + name, name);
        Server.clients.remove(name);
        socket.close();
    }
}
