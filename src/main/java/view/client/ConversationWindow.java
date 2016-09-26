package view.client;

import controller.IMClientController;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class ConversationWindow extends JFrame { //implements KeyListener {

    protected int width = 500;
    protected int height = 400;
    protected JTextPane output;
    protected JTextPane input;
    protected JButton sendButton;
    protected JButton sendFileButton;
    protected JScrollPane outputScrollPane;
    protected JScrollPane inputScrollPane;
    protected String username;
    protected StringBuilder conversation;
    private Timer scroller;

    public ConversationWindow(String username, String body) {
        this(username);
        this.writeMessageLine(username, body);
    }

    public ConversationWindow(String username) {
        super("Conversation with " + username);

        this.conversation = new StringBuilder();
        this.username = username;

        this.setBounds(getGraphicsConfiguration().getBounds().width / 2 - width / 2, getGraphicsConfiguration().getBounds().height / 2 - height / 2, width, height);

        this.initializeComponents();
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
        input.grabFocus();
    }

    public void initializeComponents() {
        output = new JTextPane();
        output.setContentType("text/html");
        output.setEditable(false);
        outputScrollPane = new JScrollPane(output);
        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        outputScrollPane.setAutoscrolls(true);
        outputScrollPane.setBounds(10, 30, this.width - 30, 220);
        this.add(outputScrollPane);

        input = new JTextPane();
        input.addKeyListener(new EnterKeyEventListener(this));

        inputScrollPane = new JScrollPane(input);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        inputScrollPane.setAutoscrolls(true);
        inputScrollPane.setBounds(10, 280, this.width - 150, 80);
        this.add(inputScrollPane);

        sendButton = new JButton("Send");
        sendButton.setBounds(380, 270, 100, 40);
        this.add(sendButton);

        sendFileButton = new JButton("Send File");
        sendFileButton.setBounds(380, 320, 100, 40);
        this.add(sendFileButton);

        sendButton.addActionListener(new SendButtonListener(this));
        sendFileButton.addActionListener(new SendFileButtonListener(this));

        this.scroller = new Timer(50, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                outputScrollPane.getVerticalScrollBar().setValue(outputScrollPane.getVerticalScrollBar().getMaximum());
            }
        });

    }

    public void writeMessageLine(String from, String message) {
        if (message.length() == 0) {
            return;
        }
        this.conversation.append("<font face='Tahoma' size='2' color='#666666'><b>" + from + "</b>:</font> <font face='Tahoma' size='2'>" + message + "</font><br />");
        output.setText(this.conversation.toString());
        //output.setCaretPosition(output.getText().length());
    }

    public String getInputText() {
        return input.getText();
    }

    protected void sendMessage(String message) {
        if (message.length() < 1) {
            return;
        }

        this.writeMessageLine("Me", message);
        IMClientController.getInstance().sendMessageToUser(this.username, message);
        this.input.setText(null);
        this.input.setCaretPosition(0);
    }

    protected class SendButtonListener implements java.awt.event.ActionListener {

        ConversationWindow cw;

        public SendButtonListener(ConversationWindow cw) {
            this.cw = cw;
        }

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            cw.sendMessage(cw.getInputText());
        }
    }

    protected class SendFileButtonListener implements java.awt.event.ActionListener {

        ConversationWindow cw;

        public SendFileButtonListener(ConversationWindow cw) {
            this.cw = cw;
        }

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            int returnVal = fc.showOpenDialog(this.cw);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                output.setText(output.getText() + "** Sending file " + file.getName() + "\n");
				//output.setCaretPosition(output.getText().length());

                IMClientController.getInstance().sendFileToUser(cw.username, file);
            }

        }
    }

    class EnterKeyEventListener implements KeyListener {

        ConversationWindow cw;

        public EnterKeyEventListener(ConversationWindow cw) {
            this.cw = cw;
        }

        public void keyTyped(KeyEvent e) {

        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                cw.sendMessage(cw.getInputText());
                e.consume();
            }
        }

        public void keyReleased(KeyEvent e) {

        }
    }

}
