package model;

import generic.AbstractController;
import message.Message;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.io.IOException;

/**
 * Message listener thread for reading individual messages
 *
 * @author Piotr Polak
 *
 */
public class MessageListenerThread extends Thread {

    /**
     * For communtication with the controller
     */
    private AbstractController parent;

    /**
     * TCP socket
     */
    private Socket socket;

	// ------------------------------------------------------------
    /**
     * Default constructor
     *
     * @param parent
     */
    public MessageListenerThread(AbstractController parent, Socket socket) {
        this.parent = parent;
        this.socket = socket;
    }

    /**
     * The run method containing the while listening loop
     */
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            try {
                Object o = in.readObject();
                parent.handleIncomingMessage((Message) o, socket);
            } catch (ClassNotFoundException e) {
                return;
            }
			// TODO in.close();
            //socket.close();
        } catch (IOException e) {
            parent.println("IOException occured while reading from socket");
        }
    }
}
