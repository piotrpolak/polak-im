package message;

import generic.User;

import java.io.Serializable;

public class SigninRequestMessage extends Message implements Serializable {

    private User user;
    static final long serialVersionUID = 945;

    public SigninRequestMessage(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }
}
