package Protocol.common;

import java.io.Serializable;

/**
 *
 * @author Relax2954
 */
/**
 * A message between a client and the server.
 */
public class Message implements Serializable {

    private final MsgType type;
    private final String body;

    public Message(MsgType type, String body) {
        this.type = type;
        this.body = body;
    }

    /**
     * @return the message body
     */
    public String getBody() {
        return body;
    }

    /**
     * @return the message type
     */
    public MsgType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Message{" + "type=" + type + ", body=" + body + '}';
    }
}
