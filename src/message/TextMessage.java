package message;

/**
 * Text message type
 *
 * @author Piotr Polak
 *
 */
public class TextMessage extends Message {

    static final long serialVersionUID = 15467346;
    
    private final String from;
    private final String to;
    private final String body;

    /**
     * Default constructor
     * 
     * @param from
     * @param to
     * @param body 
     */
    public TextMessage(String from, String to, String body) {
        this.from = from;
        this.body = body;
        this.to = to;
    }

    /**
     * Returns message body
     * 
     * @return 
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns sender name
     * 
     * @return 
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns receiver name
     * 
     * @return 
     */
    public String getTo() {
        return to;
    }

}
