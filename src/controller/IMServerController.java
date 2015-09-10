package controller;

import generic.*;
import message.*;
import model.*;
import view.server.ServerWindow;
import java.net.Socket;
import java.util.Timer;

/**
 * Server controller
 *
 * Creates GUI, listener, contains interpret method for incoming messages,
 * actions of gui
 *
 * @author Piotr Polak
 *
 */
public class IMServerController extends AbstractController {

    /**
     * Singletone instance
     */
    protected static IMServerController instance = null;

    private MessageListener messageListener;
    private UserList userList;
    private ServerWindow view;
    private Timer pingerTimer;

    /**
     * Singletone getInstance method
     *
     * @return
     */
    public static IMServerController getInstance() {
        if (IMServerController.instance == null) {
            IMServerController.instance = new IMServerController();
        }

        return IMServerController.instance;
    }

    /**
     * Private controller for the singletone
     */
    protected IMServerController() {
        this.view = new ServerWindow(this);

        // Logging
        this.println("Initializing IMServerController\n");
    }

    /**
     * The app starter method
     *
     * @param args
     */
    public static void main(String args[]) {
        // Initializes the instance of the server controller
        IMServerController.getInstance();
    }

    /**
     * Method for handeling incoming messages
     *
     * @param m
     * @param socket
     */
    public void handleIncomingMessage(Object m, Socket socket) {

        // Logging
        this.println("Handeling incoming request");

        // Checking the type of the message
        if (m instanceof SigninRequestMessage) {

            // Logging
            this.println("Received SigninRequestMessage");
            // Extracting the user
            User user = ((SigninRequestMessage) m).getUser();

            // TODO Check if there is any other user from the same IP having the same port
            // Checking whether the username is not yet taken
            if (this.userList.get(user.getUsername()) != null) {
                // Sending FAILURE message
                new AsyncObjectSender(new ServerSignInFailureMessage(), socket, this, false);
                return;
            }

            // Sets the incoming user IP address
            user.setInetAddress(socket.getInetAddress());

            // Changing the userlist and triggering the pinger task
            synchronized (this.userList) {
                this.userList.put(user.getUsername(), user);
                PingerTask.markUserlistModified();
            }

            // Sending login confirmation message
            new AsyncObjectSender(new ServerSignInSuccessMessage(), socket, this, false);

        } else if (m instanceof SignoutRequestMessage) {

            // Logging
            this.println("Received SignoutRequestMessage");

            // Removing the user from the userlist
            this.userList.remove(((SignoutRequestMessage) m).getUsername());
            // Triggering the pinger task
            PingerTask.markUserlistModified();

        } else if (m instanceof RefreshUserlistRequestMessage) {

            // Logging
            this.println("Received RefreshUserlistRequestMessage");

            // Sending back the latest version of the userlist
            new AsyncObjectSender(new RefreshUserlistResponseMessage(this.userList), socket, this, false); // Sending OK message

        } else {
            // Logging
            this.println("Wrong request, unknown message type");
        }
    }

    /**
     * Notify method is called from the ParallelObjectSender on failure
     */
    public void notify(Exception e) {

        // TODO Make it deterministic
        // Canceling the timer
        this.pingerTimer.cancel();
    }

    /**
     * Prints to the output
     *
     * @param line
     */
    public void println(String line) {
        ServerWindow.logPrintln(line);
    }

    /**
     * Returns the userlist
     *
     * @return
     */
    public UserList getUserList() {
        return this.userList;
    }

    // ------------------------------------------------------------
    //
    // Actions of the controller, for GUI
    //
    // ------------------------------------------------------------
    /**
     * Starts the server
     *
     * @param port
     */
    public void start(int port) {
        // Initialize MessageListener
        this.messageListener = new MessageListener(this, port);
        // Initialising the userlist
        this.userList = new UserList();
        // Logging
        this.println("Starting server..");
        // Starting message listener
        this.messageListener.startListener();

        // Scheduling the pinger task
        this.pingerTimer = new Timer();
        PingerTask pTask = new PingerTask();
        this.pingerTimer.schedule(pTask, 1000, 10000);

    }

    /**
     * Stopps the listener
     */
    public void stop() {
        // Canceling the timer
        this.pingerTimer.cancel();
        // Stopping the listener
        this.messageListener.stopListener();
    }

}
