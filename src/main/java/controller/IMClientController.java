package controller;

import java.io.IOException;
import java.net.Socket;
import generic.*;
import view.client.ClientWindow;
import message.*;
import model.MessageListener;
import model.AsyncObjectSender;
import model.AsyncFileSender;
import java.io.File;
import model.AsyncFileReader;
import model.SyncObjectSender;
import view.client.FileTransferDialog;

/**
 * Client controller, to be used as a Singletone
 *
 * Creates GUI, listener, contains interpret method for incoming messages,
 * actions of gui
 *
 * @author Piotr Polak
 *
 */
public class IMClientController extends AbstractController {

    /**
     * Singletone instance
     */
    protected static IMClientController instance = null;

    /**
     * Defines possible application states
     */
    private enum ApplicationStages {

        LOGIN_FORM, SIGNIN_IN_PROGRESS, SIGNED_IN
    };

    private MessageListener messageListener;
    private UserList userList;
    private ClientWindow view;
    private String serverIp;
    private ApplicationStages applicationStage;
    private User user;

    /**
     * Singletone getInstance method
     *
     * @return
     */
    public static IMClientController getInstance() {
        if (IMClientController.instance == null) {
            IMClientController.instance = new IMClientController();
        }

        return IMClientController.instance;
    }

    /**
     * Private controller for the singletone
     */
    private IMClientController() {
        this.view = new ClientWindow();
        this.println("Initializing IMClientController\n");
    }

    /**
     * The app starter method
     *
     * @param args
     */
    public static void main(String args[]) {
        // Initializes the instance of the client controller
        IMClientController.getInstance();
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
        if (m instanceof ServerSignInSuccessMessage) {

            // This is valid only when the user is SIGNIN_IN_PROGRESS
            if (this.applicationStage != ApplicationStages.SIGNIN_IN_PROGRESS) {
                return;
            }
            // Logging
            this.println("Received ServerSignInSuccessMessage");
            // Changing the view state
            this.view.signedIn();
            // Changing state of the controller
            this.applicationStage = ApplicationStages.SIGNED_IN;
            // Requesting a userlist
            this.sendObjectToServer(new RefreshUserlistRequestMessage());

        } else if (m instanceof ServerSignInFailureMessage) {

            // This is valid only when the user is SIGNIN_IN_PROGRESS
            if (this.applicationStage != ApplicationStages.SIGNIN_IN_PROGRESS) {
                return;
            }
            // Logging
            this.println("Received ServerSignInFailureMessage");
            // Changing the view state
            this.view.signinRollback("Username already taken.");
            // Stopping the message listener
            this.messageListener.stopListener();
            // Changing state of the controller
            this.applicationStage = ApplicationStages.LOGIN_FORM;

        } else if (m instanceof PingRequestMessage) {

            // Logging
            this.println("Received PingRequestMessage");
            // Responding for the ping message
            new AsyncObjectSender(new PingResponseMessage(), socket, this, false);

        } else if (m instanceof RefreshUserlistResponseMessage) {

            // Logging
            this.println("Received RefreshUserlistResponseMessage");
            // Closing the incoming socket
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Extracting the user list
            this.userList = ((RefreshUserlistResponseMessage) m).getUserlist();
            // Logging
            this.println(user.getUsername() + " Userlist received, size: " + this.userList.size());
            // Redrawing the userlist
            this.view.redrawUserlist();

        } else if (m instanceof TextMessage) {

            // Logging
            this.println("Received TextMessage");

            // Closing the incoming socket
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Extracting the message
            TextMessage tm = (TextMessage) m;
            // Displaying GUI window with the conversation
            this.view.displayConversationWindow(tm.getFrom(), tm.getBody());

        } else if (m instanceof FileTransferRequestMessage) {

            // Logging
            this.println("Received FileTransferRequestMessage");

            // Starting a file receive thread
            new AsyncFileReader(socket, ((FileTransferRequestMessage) m).getFileName(), ((FileTransferRequestMessage) m).getFileSize(), new FileTransferDialog(((FileTransferRequestMessage) m).getFileName(), ((FileTransferRequestMessage) m).getFileSize()));

        } else {
            // Logging
            this.println("Wrong request, unknown message type");
        }
    }

    /**
     * Notify method is called from the ParallelObjectSender on failure
     *
     * @param e
     */
    public void notify(Exception e) {

        // TODO Make it deterministic
        if (this.applicationStage == ApplicationStages.SIGNIN_IN_PROGRESS) {
            this.view.signinRollback("Server not listening.");
            this.messageListener.stopListener();
        }
        //e.printStackTrace();
    }

    /**
     * Returns the userlist
     *
     * @return
     */
    public UserList getUserlist() {
        return this.userList;
    }

    /**
     * Returns GUI window
     *
     * @return
     */
    public ClientWindow getView() {
        return this.view;
    }

    /**
     * Returns the current username
     *
     * @return
     */
    public String getUsername() {
        return this.user.getUsername();
    }

    // ------------------------------------------------------------
    //
    // Actions of the controller
    //
    // ------------------------------------------------------------
    /**
     * Singnin action
     *
     * @param user
     * @param serverIp
     */
    public void signin(User user, String serverIp) {
        // Changing the state of the application
        this.applicationStage = ApplicationStages.SIGNIN_IN_PROGRESS;
        // Saving the server IP
        this.serverIp = serverIp;
        // Starting the message listener
        this.messageListener = new MessageListener(this, user.getPort());
        this.messageListener.startListener();

        // Sending the signin message
        this.sendObjectToServer(new SigninRequestMessage(user));
        this.view.setTitle("Polak IM - " + user.getUsername());
        this.user = user;
    }

    /**
     * Signout action
     */
    public void signout() {
        // Changing the state of the application
        this.applicationStage = ApplicationStages.SIGNIN_IN_PROGRESS;

        // Stopping the message listener if it was previously defined
        if (this.messageListener != null) {
            this.messageListener.stopListener();
        }

        // Sending signout request and then quiting
        try {
            (new SyncObjectSender(this.serverIp, Config.SERVER_LISTEN)).send(new SignoutRequestMessage(user.getUsername()));
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /**
     * Helper for sending an object to the server
     *
     * @param o
     * @return
     */
    private boolean sendObjectToServer(Object o) {
        try {
            new AsyncObjectSender(o, this.serverIp, Config.SERVER_LISTEN, this, true);
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Helper for sending an object to the user
     *
     * @param username
     * @param message
     */
    public void sendMessageToUser(String username, String message) {

        // Getting the username
        User user = this.userList.get(username);

        // Wrong username
        if (user == null) {
            return;
        }

        // Sending message in an asynchronous manner
        try {
            new AsyncObjectSender(new TextMessage(this.user.getUsername(), username, message), user.getIp().getHostAddress(), user.getPort(), this, false);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /**
     * Helper for sending an file to the user
     *
     * @param username
     * @param file
     */
    public void sendFileToUser(String username, File file) {

        // Getting the username
        User user = this.userList.get(username);

        // Wrong username
        if (user == null) {
            return;
        }

        // Sending file in an asynchronous manner
        try {
            new AsyncFileSender(file, user.getIp().getHostAddress(), user.getPort(), new FileTransferDialog(file.getName(), file.length()));
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

}
