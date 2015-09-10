package message;

import java.io.Serializable;

/**
 * Client message that is sent to the user upon sing out request
 *
 * @author Piotr Polak
 *
 */
public class SignoutRequestMessage extends Message implements Serializable {

    static final long serialVersionUID = 967546;

    private final String username;

    /**
     * Default constructor
     * 
     * @param username 
     */
    public SignoutRequestMessage(String username) {
        this.username = username;
    }

    /**
     * Returns the username
     * 
     * @return 
     */
    public String getUsername() {
        return this.username;
    }

}
