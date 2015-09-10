package message;

import java.io.Serializable;

/**
 * Parent class of any message that is exchanged between the users and the
 * server.
 *
 * @author Piotr Polak
 *
 */
public abstract class Message implements Serializable {

    /**
     * Unix timestamp
     */
    protected int timestamp;

    /**
     * Default constructor
     */
    public Message() {
        this.timestamp = 1; // NOW
    }

    /**
     * Returns the timestamp of the message
     * 
     * @return timestamp of the message
     */
    public int getTimestamp() {
        return timestamp;
    }

}
