package generic;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Chat user representation
 *
 * @author Piotr Polak
 *
 */
public class User implements Serializable {

    protected String username;
    protected InetAddress ip;
    protected int port;
    static final long serialVersionUID = 6762346;

    /**
     * Default controller
     * 
     * @param username
     * @param ip
     * @param port 
     */
    public User(String username, InetAddress ip, int port) {
        this.username = username;
        this.ip = ip;
        this.port = port;
    }

    /**
     * Returns username
     * 
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns user port
     * 
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns user IP
     * 
     * @return user ip
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * Sets user ip
     * @param ip address of the user 
     */
    public void setInetAddress(InetAddress ip) {
        this.ip = ip;
    }
}
