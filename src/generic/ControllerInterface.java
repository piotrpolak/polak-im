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
     * Method for handeling incoming messages
     *
     * @param mx
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
     * @param line
     */
    public void notify(Exception e);
}
