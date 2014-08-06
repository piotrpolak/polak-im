package message;

import generic.UserList;

import java.io.Serializable;

public class RefreshUserlistResponseMessage extends ServerResponseMessage implements Serializable {
	
	protected UserList userlist;
	
	static final long serialVersionUID = 547346736;
	
	public RefreshUserlistResponseMessage( UserList userlist )
	{
		this.userlist = userlist;
	}
	
	public UserList getUserlist()
	{
		return this.userlist;
	}
}
