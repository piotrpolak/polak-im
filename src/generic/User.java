package generic;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * User representation
 * 
 * @author Piotr Polak
 *
 */
public class User implements Serializable {

	protected String 		username;
	protected InetAddress 	ip;
	protected int 			port;
	static final long 		serialVersionUID = 6762346;
	
	public User( String username, InetAddress ip, int port )
	{
		this.username 	= username;
		this.ip 		= ip;
		this.port 		= port;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public InetAddress getIp()
	{
		return ip;
	}
	
	public void setInetAddress( InetAddress ip )
	{
		this.ip = ip;
	}
}
