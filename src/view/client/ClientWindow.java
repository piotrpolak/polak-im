package view.client;

import generic.*;
import javax.swing.JFrame;
import javax.swing.UIManager;
import java.util.HashMap;
import controller.IMClientController;

/**
 * Client main window
 *
 * @author Piotr Polak
 *
 */
public class ClientWindow extends JFrame {

    protected LoginPanel loginPanel;
    protected SigningInPanel signingInPanel;
    protected UserListPanel userListPanel;
    protected HashMap<String, ConversationWindow> conversationWindows;

    /**
     * Default controller
     *
     * @param guidriver
     */
    public ClientWindow() {
        super("Polak! IM");

        conversationWindows = new HashMap<String, ConversationWindow>();

        // Setting look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        // Creating panels
        this.loginPanel = new LoginPanel(this);
        this.add(this.loginPanel);

        this.signingInPanel = new SigningInPanel();
        this.signingInPanel.setVisible(false);
        this.add(this.signingInPanel);

        this.userListPanel = new UserListPanel();
        this.userListPanel.setVisible(false);
        this.add(this.userListPanel);

        // Close listener
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                IMClientController.getInstance().signout();
            }
        });

        this.setLayout(null);
        this.setResizable(false);

        // Displaying
        this.setBounds(getGraphicsConfiguration().getBounds().width / 2 - 100, getGraphicsConfiguration().getBounds().height / 2 - 300, 200, 600);
        this.setVisible(true);
    }

    // ----------------------------------------
    //
    // GUI Controll actions
    //
    // ----------------------------------------
    public void signin() {
        this.loginPanel.setVisible(false);
        this.signingInPanel.setVisible(true);

        IMClientController.getInstance().signin(new User(this.loginPanel.login.getText(), null, new Integer(this.loginPanel.listenPort.getText()).intValue()), this.loginPanel.serverIP.getText()); // Sending OK message)
    }

    public void signinRollback(String text) {
        this.loginPanel.errorLabel.setText(text);
        this.loginPanel.setVisible(true);
        this.signingInPanel.setVisible(false);
        this.loginPanel.login.requestFocus();
    }

    public void signedIn() {
        this.signingInPanel.setVisible(false);
        this.userListPanel.setVisible(true);
    }

    public void redrawUserlist() {
        this.userListPanel.redrawUserlist(IMClientController.getInstance().getUserlist());
    }

    public void displayConversationWindow(String username) {
        ConversationWindow cw = conversationWindows.get(username);
        if (cw == null) {
            cw = new ConversationWindow(username);
            conversationWindows.put(username, cw);
        } else {
            cw.setVisible(true);
        }
    }

    public void displayConversationWindow(String username, String body) {
        ConversationWindow cw = conversationWindows.get(username);
        if (cw == null) {
            cw = new ConversationWindow(username, body);
        } else {
            cw.writeMessageLine(username, body);
        }
        cw.setFocusableWindowState(true);
        conversationWindows.put(username, cw);
    }
}
