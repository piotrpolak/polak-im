package message;

import java.io.Serializable;

/**
 * Message structure
 * @author Piotr Polak
 *
 */
public abstract class Message implements Serializable {
	
	protected int timestamp;
	
	public Message()
	{
		this.timestamp = 1; // NOW
	}
	
	public int getTimestamp()
	{
		return timestamp;
	}
	
}
