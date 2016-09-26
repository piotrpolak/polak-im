package message;

import java.io.Serializable;

/**
 * Refresh userlist request message
 * 
 * This type of the message is used to request the latest version of the
 * userlist. This message is issued by a client towards the server.
 *
 * @author Piotr Polak
 *
 */
public class RefreshUserlistRequestMessage extends ServerResponseMessage implements Serializable {

    static final long serialVersionUID = 25457;

}
