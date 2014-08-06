package view.client;

import javax.swing.*;
import generic.*;
import controller.IMClientController;
import java.util.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserListPanel extends JPanel {

	public 		JLabel 				label, hintLabel;
	public 		JList 				list;
	public 		DefaultListModel 	listModel;
	private  	JScrollPane 		listScrollPane;
	
	
	
	public UserListPanel()
	{
		super();
		
		
		this.label = new JLabel("Buddy list");
		this.label.setBounds(20, 20, 160, 20);
		this.add(label);
		
		this.hintLabel = new JLabel("<html>Double click on user's name<P> to start the conversation");
		this.hintLabel.setBounds(20, 510, 160, 60);
		this.add(hintLabel);		
		
		
		listModel = new DefaultListModel();

		this.list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //list.setSelectedIndex(0);
		//list.setVisibleRowCount(5);
		
        listScrollPane = new JScrollPane(list);
        listScrollPane.setBounds(20, 50, 160, 400);

        list.addMouseListener(new ActionJList(list));


		this.add(listScrollPane);
		
		this.setLayout(null);
		this.setBounds(0, 0, 200, 600);
	}
	
	public void redrawUserlist(UserList userlist)
	{
		synchronized( listModel )
		{
			listModel.clear();			
			IMClientController.getInstance().println("Redrawing " + userlist.size()+ "; Capacity of userlist: "+listModel.capacity());
			
			Iterator<User> it = userlist.values().iterator();
			for(int i = 0; i< userlist.size(); i++)
			{
				User u = it.next();
				IMClientController.getInstance().println("Adding " + u.getUsername());
				listModel.addElement(u.getUsername());
			}
			
			this.list.setVisible(true);
		}
		
	}
	
	class ActionJList extends MouseAdapter
	{
		// Piece of code from http://www.rgagnon.com/javadetails/java-0219.html
		protected JList list;

		public ActionJList(JList l)
		{
			list = l;
		}

		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount() == 2)
			{
				int index = list.locationToIndex(e.getPoint());
				ListModel dlm = list.getModel();
				Object item = dlm.getElementAt(index);;
				list.ensureIndexIsVisible(index);
				
				if( !IMClientController.getInstance().getUsername().equals((String) item ))
					IMClientController.getInstance().getView().displayConversationWindow( (String) item);
			}
		}
	}

}
