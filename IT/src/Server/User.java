package Server;

public class User {
    private String name;
    protected MessageProcessor messageProcessor;

    public User(String clientName,MessageProcessor messageProcessor) {
        this.name=clientName;
        this.messageProcessor=messageProcessor;
    }

    public String getName() {
        return name;
    }
}
