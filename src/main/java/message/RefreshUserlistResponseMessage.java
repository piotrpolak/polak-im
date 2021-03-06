package message;

import generic.UserList;

import java.io.Serializable;

/**
 * Refresh userlist response message
 * 
 * The respose to RefreshUserlistRequestMessage holding the latest version of
 * the userlist. This message is generated by the server and sent back towards
 * the client.
 *
 * @author Piotr Polak
 *
 */
public class RefreshUserlistResponseMessage extends ServerResponseMessage implements Serializable {

    protected UserList userlist;

    static final long serialVersionUID = 547346736;

    public RefreshUserlistResponseMessage(UserList userlist) {
        this.userlist = userlist;
    }

    public UserList getUserlist() {
        return this.userlist;
    }
}
