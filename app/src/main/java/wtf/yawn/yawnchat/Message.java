package wtf.yawn.yawnchat;

/**
 * Created by Andrew on 21.06.2016.
 */
public class Message {
    public String message;
    public String user;

    public Message(){}

    public Message(String message, String user) {
        this.message = message;
        this.user = user;
    }
}
