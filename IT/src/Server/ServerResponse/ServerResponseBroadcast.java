package Server.ServerResponse;

import GlobalUtilities.Utils;
import Server.MessageProcessor;
import Server.Server;

public class ServerResponseBroadcast implements ServerResponse{
    private MessageProcessor mp;

    public ServerResponseBroadcast(MessageProcessor mp) {
        this.mp=mp;
    }

    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if there are any arguments after header
            mp.sendMessage("FAIL00 Unkown command");
        }else {
            if (mp.checkClientLoggedIn()) {
                broadcast(Utils.combinedMessage(1, response));
            }
        }

    }
    private void broadcast(String BroadcastMessage){
        String message = "BCST_OK " + BroadcastMessage;
        mp.sendMessage(message);
        mp.getServer().broadcastMessageToEveryone("BCST " + mp.getName() + " " + BroadcastMessage, mp.getName());
    }
}
