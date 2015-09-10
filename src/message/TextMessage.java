package message;

import message.Message;

/**
 * Text message structure
 *
 * @author Piotr Polak
 *
 */
public class TextMessage extends Message {

    static final long serialVersionUID = 15467346;
    protected String from;
    protected String to;
    protected String body;

    public TextMessage(String from, String to, String body) {
        this.from = from;
        this.body = body;
        this.to = to;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
