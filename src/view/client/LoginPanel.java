package view.client;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.*;

public class LoginPanel extends JPanel {

	public ClientWindow parent;
	private JButton 	loginButton;
	public JTextField 	serverIP;
	public JTextField 	listenPort;
	public JTextField 	login;
	public JLabel 		errorLabel;
	
	public LoginPanel(ClientWindow parent)
	{
		super();
		this.parent = parent;
		
		JLabel loginLabel 		= new JLabel("Login");
		JLabel serverIPLabel 	= new JLabel("Userlist server IP");
		JLabel listenPortLabel 	= new JLabel("Client listen port");
		this.errorLabel 		= new JLabel( "", JLabel.CENTER );
		this.loginButton 		= new JButton("Sign In");
		
		
		login 					= new JTextField();
		serverIP 				= new JTextField();
		listenPort 				= new JTextField();
		
		
		int y = 120;
		
		
		loginLabel.setBounds(20, y, 160, 20);
		y += 20;
		login.setBounds(20, y, 160, 20);
		login.setRequestFocusEnabled(true);

		y += 30;
		serverIPLabel.setBounds(20, y, 160, 20);
		y += 20;
		serverIP.setBounds(20, y, 160, 20);
		serverIP.setText("localhost");
		
		y += 30;
		listenPortLabel.setBounds(20, y, 160, 20);
		y += 20;
		listenPort.setBounds(20, y, 160, 20);
		listenPort.setText( ""+((int)(Math.random()*1000) + 6000));
		
		
		y += 30;
		loginButton.setBounds(50, y, 100, 20);
		loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	loginButtonActionPerformed(evt);
            }
        });
		
		y += 30;
		errorLabel.setBounds(20, y, 160, 20);
		
		this.add(loginLabel);
		this.add(login);
		this.add(serverIPLabel);
		this.add(serverIP);
		this.add(listenPortLabel);
		this.add(listenPort);
		this.add(loginButton);
		this.add(errorLabel);
		
		this.setLayout(null);
		this.setBounds(0, 0, 200, 600);
		
		// Listeners
		login.addKeyListener( new EnterKeyEventListener( parent ) );
		serverIP.addKeyListener( new EnterKeyEventListener( parent ) );
		listenPort.addKeyListener( new EnterKeyEventListener( parent ) );
		
	}
	
	private void loginButtonActionPerformed(java.awt.event.ActionEvent evt)
	{
		parent.signin();
	}
	
	class EnterKeyEventListener implements KeyListener
	{
		private ClientWindow parent;
		
		public EnterKeyEventListener( ClientWindow parent )
		{
			this.parent = parent;
		}

		public void keyTyped(KeyEvent e) {

		}

		public void keyPressed(KeyEvent e)
		{
			if( e.getKeyChar() == '\n' )
			{
				parent.signin();
			}
		}

		public void keyReleased(KeyEvent e) {

		}
	}

}
