package message;

import java.io.Serializable;

/**
 * Ping response message
 *
 * This message is used to monitor the state of the user (online or offline).
 * When PingRequestMessage message is received, the client must respond with a
 * PingResponseMessage to prevent being removed from the client list.
 *
 * @author Piotr Polak
 *
 */
public class PingResponseMessage extends Message implements Serializable {

    static final long serialVersionUID = 445566;

}
