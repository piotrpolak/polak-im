package view.client;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SigningInPanel extends JPanel {

	public JLabel signInLabel;
	public SigningInPanel()
	{
		super();		
		this.signInLabel = new JLabel("Signing in...");
		this.signInLabel.setBounds(70, 200, 160, 20);
		this.add(signInLabel);
		this.setLayout(null);
		this.setBounds(0, 0, 200, 600);
	}
}
