package model;

import java.util.Iterator;
import java.util.TimerTask;
import controller.IMServerController;
import generic.UserList;
import generic.User;
import model.SyncObjectSender;
import message.*;
import java.io.IOException;


/**
 * Periodically called pinger
 * Checks if users are still online and resends userlist if necesarely
 * 
 * @author Piotr Polak
 *
 */
public class PingerTask extends TimerTask {

	/**
	 * Variable indicating if the userlist has been modified since last ping
	 */
	private static boolean userlist_modified = false;
	
	
	public static void markUserlistModified()
	{
		PingerTask.userlist_modified = true;
	}
	
	public void run()
	{
		
		//IMServerController.getInstance().println(" ** Pinger running ** ");
		// "pings" all the users
		// refreshes userlist
		// sends refreshed userlist to all the users
		
		UserList userlist = IMServerController.getInstance().getUserList();

		SyncObjectSender syncOS = null;
		
		Iterator<User> it;
		
		synchronized( userlist )
		{			
			it = userlist.values().iterator();
			for(int i = 0; i< userlist.size(); i++)
			{
				User u;
				try {
					u = it.next();
				}
				catch( java.util.ConcurrentModificationException cme ) { return; }
				
				
				
				
				syncOS = new SyncObjectSender( u.getIp().getHostAddress(), u.getPort() );
				try {
					Object response = syncOS.sendAndReceive( new PingRequestMessage() );
					if( !(response instanceof PingResponseMessage) )
					{
						PingerTask.userlist_modified = true;
						userlist.remove( u.getUsername() ); // Wrong response - bye bye
						//IMServerController.getInstance().println("    Removing "+u.getUsername());
					}
				}
				catch( IOException e )
				{
					PingerTask.userlist_modified = true;
					
					userlist.remove( u.getUsername() ); // Pingout - bye bye
					//IMServerController.getInstance().println("    Removing "+u.getUsername());
				}
			}
		
		
			if( PingerTask.userlist_modified )
			{
				//IMServerController.getInstance().println("    Resending userlist");
				it = userlist.values().iterator();
				for(int i = 0; i< userlist.size(); i++)
				{
					User u = it.next();
					syncOS = new SyncObjectSender( u.getIp().getHostAddress(), u.getPort() );
					//IMServerController.getInstance().println("    Sending userlist("+userlist.size()+") to "+u.getUsername());
					try {
						syncOS.send( new RefreshUserlistResponseMessage( userlist) );
					}
					catch( IOException e )
					{
						//IMServerController.getInstance().println("    IOException  "+u.getUsername());
					}
				}
			}
			
			PingerTask.userlist_modified = false;
		}
	}
}
