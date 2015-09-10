package view.server;

import generic.Config;
import controller.IMServerController;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;

/**
 * Server window
 *
 * @author Piotr Polak
 *
 */
public class ServerWindow extends JFrame {

    protected int width = 500;
    protected int height = 300;
    protected static JTextPane output;
    protected JButton start;
    protected JButton stop;
    protected JScrollPane scrollPane;
    protected JTextField port;
    protected IMServerController controller;

    public ServerWindow(IMServerController controller) {
        super("Polak! IM Server");

        this.controller = controller;

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        this.setBounds(getGraphicsConfiguration().getBounds().width / 2 - width / 2, getGraphicsConfiguration().getBounds().height / 2 - height / 2, width, height);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                System.exit(0);
            }
        });

        this.initializeComponents();
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    public void initializeComponents() {
        output = new JTextPane();
        output.setEditable(false);
        scrollPane = new JScrollPane(output);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setAutoscrolls(true);
        scrollPane.setBounds(10, 10, 350, 240);

        this.add(scrollPane);

        JLabel portLabel = new JLabel("Listen port");
        portLabel.setBounds(380, 10, 100, 25);
        this.add(portLabel);

        port = new JTextField("" + Config.SERVER_LISTEN);
        port.setBounds(380, 30, 100, 25);
        this.add(port);

        start = new JButton("Start");
        start.setBounds(380, 80, 100, 25);
        this.add(start);

        start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start.setVisible(false);
                stop.setVisible(true);
                port.setEditable(false);
                controller.start((new Integer(port.getText()).intValue()));
            }
        });

        stop = new JButton("Stop");
        stop.setBounds(380, 80, 100, 25);
        stop.setVisible(false);
        this.add(stop);

        stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start.setVisible(true);
                stop.setVisible(false);
                port.setEditable(true);
                controller.stop();
            }
        });
    }

    public static void logPrintln(String text) {
        output.setText(output.getText() + text + "\n");
        output.setCaretPosition(output.getText().length());
    }
}
