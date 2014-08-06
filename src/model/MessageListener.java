package model;

import generic.AbstractController;
import java.net.*;
import javax.net.ssl.*;
import generic.*;
import java.io.IOException;

/**
 * Message listener is the "incoming server" that reads the messages and passes them to the 'parent' controller
 * 
 * @author Piotr Polak
 *
 */
public class MessageListener extends Thread {

	private AbstractController 	parent;
	private ServerSocket 		serverSocket 	= null;
	private int 				port 			= 0;	
	private boolean 			doListen		= false;
	
	
	// ------------------------------------------------------------
	
	
	/**
	 * Default constructor
	 * 
	 * @param parent
	 */
	public MessageListener( AbstractController parent, int port )
	{
		this.parent = parent;
		this.port 	= port;
	}
	
	
	/**
	 * Starts the listener thread
	 */
	public void startListener()
	{
		this.doListen = true;
		this.start();
	}

	
	/**
	 * Stopps the listener thread
	 */
	public void stopListener()
	{
		this.doListen = false;
		if( this.serverSocket != null)
		{
			try{this.serverSocket.close();}
			catch(Exception e) {}
		}
	}

	
	/**
	 * The run method containing the while listening loop
	 */
	public void run()
	{		
		try {
			if( Config.USE_SSL )
			{
				this.parent.println("Using SSL");
				serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(this.port);
				((SSLServerSocket) serverSocket).setEnabledCipherSuites(((SSLServerSocket) serverSocket).getSupportedCipherSuites());
			}
			else
			{
				serverSocket = new ServerSocket( this.port );
			}
		}
		catch( IOException e ) {
			parent.notify( e );
			parent.println( "ERROR: Unable to start server: unable to listen on port "+this.port );
			return;
		}
	
		parent.println( "Listener started. Listening on port "+this.port );
		
		while( this.doListen )
		{
			try {
				Socket socket;
				if( Config.USE_SSL )
				{
					socket = (SSLSocket) serverSocket.accept();
					((SSLSocket) socket).startHandshake();
				}
				else
				{
					socket = serverSocket.accept();
				}
				parent.println( "Accepting a new socket" );
				(new MessageListenerThread(	parent, socket ) ).start();		// Starting a new thread
			}
			catch(java.net.SocketException e ){} // Socket closed
			catch(Exception e) { e.printStackTrace(); }
		}
		
		
		
		try {
			serverSocket.close();
			parent.println( "Listener stopped" );
		}
		catch( IOException e ) {}
	}
}
