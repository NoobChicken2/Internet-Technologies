package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerResponseLogin  implements ServerResponse{

    private MessageProcessor mp;

    public ServerResponseLogin(MessageProcessor mp) {
        this.mp=mp;
    }

    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if name is null
            mp.sendMessage("FAIL02 Username has an invalid format or length");
        }else {
            if (checkClientName(response[1])) {
                register(response[1]);
            }
        }
    }


    private boolean checkClientName(String clientName) {
        if (mp.getName() !=null){
            mp.sendMessage("FAIL04 User cannot login twice");
            return false;
        }
        if (Server.clients.containsKey(clientName)){
            mp.sendMessage("FAIL01 User already logged in");
            return false;
        }
        if (clientName.length()<3){
            mp.sendMessage("FAIL02 Username has an invalid format or length");
            return false;
        }
        if (clientName.length()>=15){
            mp.sendMessage("FAIL02 Username has an invalid format or length");
            return false;
        }
        for (int i = 0; i < clientName.length(); i++) {
            if ((Character.isLetterOrDigit(clientName.charAt(i)) == false)) {
                mp.sendMessage("FAIL02 Username has an invalid format or length");
                return false;
            }
        }
        return true;
    }
    private void register(String clientName){
        Server.clients.put(clientName, mp);
        mp.setName(clientName);
        String message = "IDENT_OK " + clientName;
        mp.sendMessage(message);
        Server.broadcastMessage("JOINED " + clientName, mp.getName());
    }
}
