package message;

import java.io.Serializable;

public class SignoutRequestMessage extends Message implements Serializable {
	
	private String username;
	static final long serialVersionUID = 967546;
	
	public SignoutRequestMessage( String username )
	{
		this.username = username;
	}
	
	public String getUsername()
	{
		return this.username;
	}

}
