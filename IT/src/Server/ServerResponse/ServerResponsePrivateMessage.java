package Server.ServerResponse;
;
import Server.MessageProcessor;
import Server.Utils.ServerUtils;

public class ServerResponsePrivateMessage implements ServerResponse{
    private MessageProcessor mp;
    public ServerResponsePrivateMessage(MessageProcessor mp) {
        this.mp=mp;
    }
    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if there are any arguments after header
            mp.sendMessage("FAIL00 Unkown command");
        }else {
            if (mp.checkClientLoggedIn()) {
                sendPrivateMessage(response[1], ServerUtils.combinedMessage(2, response));
            }
        }
    }

    private void sendPrivateMessage(String receiver, String privateMessage) {
        mp.sendMessage("PRV_BCST_OK "+ privateMessage);
        String message = "PRV_BCST " + mp.getName() + " " + privateMessage;
        mp.getServer().messageClient(receiver, message);
    }

}
