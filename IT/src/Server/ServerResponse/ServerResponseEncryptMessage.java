package Server.ServerResponse;

import Server.MessageProcessor;
import Server.Utils.ServerUtils;

public class ServerResponseEncryptMessage implements ServerResponse{
    private MessageProcessor mp;
    public ServerResponseEncryptMessage(MessageProcessor mp) {
        this.mp=mp;
    }
    @Override
    public void respond(String request) {
        String[] response = request.split(" ");
        if (response.length<2){//check if there are any arguments after header
            mp.sendMessage("FAIL00 Unkown command");
        }else {
            if (mp.checkClientLoggedIn()) {
                EncryptedMessageHandler(response);
            }
        }
    }
    private void EncryptedMessageHandler(String[] response) {
        //Requesting of public keys
        if (response[1].equals("PUBLIC")){
            if (mp.getServer().checkIfClientExists(response[2])) {
                mp.getServer().messageClient(response[2],"ENCRYPT_PUBLIC " + mp.getName());
            }else {
                mp.sendMessage("FAIL07 usernames are incorrect.");
            }
        }
        //Sending Public key
        if (response[1].equals("PUBLIC_OK")){
            if (mp.getServer().checkIfClientExists(response[2])) {
                mp.getServer().messageClient(response[2],"ENCRYPT_PUBLIC_OK " + mp.getName()+" "+response[3]);
            }else {
                mp.sendMessage("FAIL07 usernames are incorrect.");
            }
        }
        //Exchange of session keys
        if (response[1].equals("REQUEST")){
            if (mp.getServer().checkIfClientExists(response[2])) {
                mp.sendMessage("ENCRYPT_REQUEST_OK");
                String message="ENCRYPT_REQUEST "+mp.getName()+" "+response[3];
                mp.getServer().messageClient(response[2], message);
            }else {
                mp.sendMessage("FAIL07 usernames are incorrect.");
            }
        }
        //sending messages
        if (response[1].equals("MSG")) {
            mp.sendMessage("ENCRYPT_MSG_OK");
            String message = "ENCRYPT_MSG " + response[2] + " "+ mp.getName();
            mp.getServer().messageClient(response[3], message);
        }

    }
}
