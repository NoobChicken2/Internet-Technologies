package Server.ServerResponse;

public class ServerResponseManager implements ServerResponse{
    private ServerResponse serverResponse;
    @Override
    public void respond(String request) {
        serverResponse.respond(request);
    }

    public ServerResponse getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }
}
