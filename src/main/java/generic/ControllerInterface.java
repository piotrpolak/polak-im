package generic;

import java.net.Socket;

/**
 * Controller interface, forces the controller to implement the interprate
 * incoming message method
 *
 * @author Piotr Polak
 *
 */
public interface ControllerInterface {

    /**
     * Handles incoming messages
     * 
     * @param m
     * @param socket 
     */
    public void handleIncomingMessage(Object m, Socket socket);

    /**
     * Prints to the output
     *
     * @param line
     */
    public void println(String line);

    /**
     * Notifies about socket error etc
     * 
     * @param e 
     */
    public void notify(Exception e);
}
