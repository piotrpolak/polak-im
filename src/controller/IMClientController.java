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
 * Client controller
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

    private MessageListener messageListener;
    private UserList userList;
    private ClientWindow view;
    private String serverIp;

    private enum ApplicationStages {
        LOGIN, SIGNIN, SIGNED_IN
    };
    
    private ApplicationStages applicationStage;
    private User user;


    public static IMClientController getInstance() {
        if (IMClientController.instance == null) {
            IMClientController.instance = new IMClientController();
        }

        return IMClientController.instance;
    }

    private IMClientController() {
        this.view = new ClientWindow();
        this.println("Initializing IMClientController\n");
    }

    public static void main(String args[]) {
        IMClientController.getInstance();
    }

    /**
     * Method for handeling incoming messages
     *
     * @param mx
     */
    public void handleIncomingMessage(Object m, Socket socket) {
        this.println("Handeling incoming request");

        if (m instanceof ServerSignInSuccessMessage) {
            if (this.applicationStage != ApplicationStages.SIGNIN) {
                return;
            }

            this.println("Received ServerSignInSuccessMessage");

            this.view.signedIn();

            this.applicationStage = ApplicationStages.SIGNED_IN;
            this.sendObjectToServer(new RefreshUserlistRequestMessage());
        } else if (m instanceof ServerSignInFailureMessage) {
            if (this.applicationStage != ApplicationStages.SIGNIN) {
                return;
            }

            this.println("Received ServerSignInFailureMessage");

            this.view.signinRollback("Username already taken.");
            this.messageListener.stopListener();

            this.applicationStage = ApplicationStages.LOGIN;
            //this.sendObjectToServer( new RefreshUserlistRequestMessage() );
        } else if (m instanceof PingRequestMessage) {
            this.println("Received PingRequestMessage");
            new AsyncObjectSender(new PingResponseMessage(), socket, this, false);
        } else if (m instanceof RefreshUserlistResponseMessage) {
            this.println("Received RefreshUserlistResponseMessage");

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.userList = ((RefreshUserlistResponseMessage) m).getUserlist();

            this.println(user.getUsername() + " Userlist received, size: " + this.userList.size());

            this.view.redrawUserlist();
        } else if (m instanceof TextMessage) {
            this.println("Received TextMessage");

            TextMessage tm = (TextMessage) m;
            this.view.displayConversationWindow(tm.getFrom(), tm.getBody());
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (m instanceof FileTransferRequestMessage) {
            this.println("Received FileTransferRequestMessage");

            new AsyncFileReader(socket, ((FileTransferRequestMessage) m).getFileName(), ((FileTransferRequestMessage) m).getFileSize(), new FileTransferDialog(((FileTransferRequestMessage) m).getFileName(), ((FileTransferRequestMessage) m).getFileSize()));
        } else {
            this.println("Wrong request");
        }
    }

    /**
     * Notify method is called from the ParallelObjectSender on failure
     */
    public void notify(Exception e) {
        if (this.applicationStage == ApplicationStages.SIGNIN) {
            this.view.signinRollback("Server not listening.");
            this.messageListener.stopListener();

        }
        e.printStackTrace();
    }

    /**
     * Returns userlist
     *
     * @return
     */
    public UserList getUserlist() {
        return this.userList;
    }

    /**
     * Returns gui window
     *
     * @return
     */
    public ClientWindow getView() {
        return this.view;
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    // ------------------------------------------------------------
    //
    // Actions of the controller
    //
    // ------------------------------------------------------------
    public void signin(User user, String serverIp) {
        this.applicationStage = ApplicationStages.SIGNIN;
        this.serverIp = serverIp;
        this.messageListener = new MessageListener(this, user.getPort());

        this.messageListener.startListener();
        this.sendObjectToServer(new SigninRequestMessage(user));
        this.view.setTitle("Polak IM - " + user.getUsername());
        this.user = user;
    }

    public void signout() {
        this.applicationStage = ApplicationStages.SIGNIN;
        
        if( this.messageListener != null )
        {
            this.messageListener.stopListener();
        }
        
        try {
            (new SyncObjectSender(this.serverIp, Config.SERVER_LISTEN)).send(new SignoutRequestMessage(user.getUsername()));
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            System.exit(0);
        }
        
        
    }

    /**
     * Sends an object to the server
     *
     * @param o
     * @return
     */
    private boolean sendObjectToServer(Object o) {
        try {
            new AsyncObjectSender(o, this.serverIp, Config.SERVER_LISTEN, this, true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void sendMessageToUser(String username, String message) {
        User user = this.userList.get(username);

        if (user == null) {
            return;
        }

        try {
            new AsyncObjectSender(new TextMessage(this.user.getUsername(), username, message), user.getIp().getHostAddress(), user.getPort(), this, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendFileToUser(String username, File file) {
        User user = this.userList.get(username);

        if (user == null) {
            return;
        }

        try {
            new AsyncFileSender(file, user.getIp().getHostAddress(), user.getPort(), new FileTransferDialog(file.getName(), file.length()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
