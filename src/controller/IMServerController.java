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
 * Creates GUI, listener, contains interpret method for incoming messages, actions of gui
 * @author Piotr Polak
 *
 */
public class IMServerController extends AbstractController {

	/**
	 * Singletone instance
	 */
	protected static IMServerController instance = null;
	
	private MessageListener 	messageListener;
	private UserList			userList;
	private ServerWindow 		view;
	private Timer				pingerTimer;
	
	// ------------------------------------------------------------
	
	
	
	
	/**
	 * Singletone
	 */
	public static IMServerController getInstance()
	{
		if( IMServerController.instance == null )
		{
			IMServerController.instance = new IMServerController();
		}
		
		return IMServerController.instance;
	}
	
	// ------------------------------------------------------------
	
	
	
	/**
	 * Default constructor
	 */
	protected IMServerController()
	{
		this.view 		= new ServerWindow( this );
		
		this.println("Initializing IMServerController\n");
	}
	
	// ------------------------------------------------------------
	
	
	
	
	/**
	 * Static method, starts the server
	 * @param args
	 */
	public static void main( String args[] )
	{
		// Initialize Controller
		IMServerController.getInstance();
	}
	
	// ------------------------------------------------------------
	
	
	
	
	/**
	 * Method for handeling incoming messages
	 * @param mx
	 */
	public void handleIncomingMessage( Object m, Socket socket )
	{
		this.println("Handeling incoming request");
		
		
		if( m instanceof SigninRequestMessage )
		{
			this.println("Received SigninRequestMessage");
			User user = ((SigninRequestMessage) m).getUser();

			// TODO Check if there is any other user from the same IP having the same port

			if ( this.userList.get(user.getUsername()) != null )
			{
				new AsyncObjectSender(new ServerSignInFailureMessage(), socket, this, false ); // Sending FAILURE message
				return;
			}
			
			user.setInetAddress( socket.getInetAddress() );	
			
			synchronized( this.userList )
			{
				this.userList.put(user.getUsername(), user);
				PingerTask.markUserlistModified();
			}

			new AsyncObjectSender(new ServerSignInSuccessMessage(), socket, this, false ); // Sending OK message
		}



		else if( m instanceof SignoutRequestMessage )
		{
			this.println("Received SignoutRequestMessage");
			this.userList.remove(((SignoutRequestMessage) m).getUsername());
			PingerTask.markUserlistModified();
		}

		else if( m instanceof RefreshUserlistRequestMessage )
		{
			this.println("Received RefreshUserlistRequestMessage");
			new AsyncObjectSender( new RefreshUserlistResponseMessage( this.userList ), socket, this, false ); // Sending OK message
		}
		else
		{
			this.println("Received invalid request");
		}
	}
	
	// ------------------------------------------------------------
	
	/**
	 * Notify method is called from the ParallelObjectSender on failure
	 */
	public void notify( Exception e ) {

		this.pingerTimer.cancel();
		
	}
	
	
	/**
	 * Prints to the output
	 * 
	 * @param line
	 */
	public void println( String line )
	{
		ServerWindow.logPrintln( line );
	}
	
	
	public UserList getUserList()
	{
		return this.userList;
	}
	
	
	
	// ------------------------------------------------------------
	//
	// Actions of the controller, for GUI
	//
	// ------------------------------------------------------------
	
	
	/**
	 * Starts the server
	 * @param port
	 */
	public void start( int port )
	{
		// Initialize MessageListener
		this.messageListener 	= new MessageListener( this, port );
		this.userList 			= new UserList();
		this.println("Starting server..");
		this.messageListener.startListener();
		
		
		this.pingerTimer = new Timer();
		PingerTask pTask = new PingerTask();
		this.pingerTimer.schedule(pTask, 1000, 10000);
		
	}
	
	/**
	 * Stopps the listener
	 */
	public void stop()
	{
		this.pingerTimer.cancel();
		this.messageListener.stopListener();
	}
	
	
}
