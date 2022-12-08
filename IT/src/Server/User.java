package Server;

public class User {
    private String name;
    protected MessageProcessor messageProcessor;

    public User(String clientName) {
        this.name=clientName;
    }
}
