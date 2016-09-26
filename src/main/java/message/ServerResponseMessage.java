package message;

import java.io.Serializable;

/**
 * Parent class of any message that the server sends back to client
 *
 * @author Piotr Polak
 *
 */
public abstract class ServerResponseMessage extends Message implements Serializable {

    static final long serialVersionUID = 15467346;
}
