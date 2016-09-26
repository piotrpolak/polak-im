package message;

import java.io.Serializable;

/**
 * Ping request message
 * 
 * This message is used to monitor the state of the user (online or offline).
 * When this message is received, the client must respond with a
 * PingResponseMessage to prevent being removed from the client list.
 *
 * @author Piotr Polak
 *
 */
public class PingRequestMessage extends Message implements Serializable {

    static final long serialVersionUID = 837;
}
