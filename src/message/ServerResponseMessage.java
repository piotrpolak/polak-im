package message;

import java.io.Serializable;


public abstract class ServerResponseMessage extends Message implements Serializable {

    static final long serialVersionUID = 15467346;
    public int code = 200;
    public String body = "ok";
}
