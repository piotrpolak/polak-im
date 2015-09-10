package message;

import generic.User;

import java.io.Serializable;

/**
 * Client message that is sent to the user upon sing in request
 *
 * @author Piotr Polak
 *
 */
public class SigninRequestMessage extends Message implements Serializable {

    static final long serialVersionUID = 945;
    
    private final User user;
    

    /**
     * Default constructor
     * 
     * @param user 
     */
    public SigninRequestMessage(User user) {
        this.user = user;
    }

    /**
     * Returns the user
     * 
     * @return 
     */
    public User getUser() {
        return this.user;
    }
}
