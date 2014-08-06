package view.client;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.Date;
import model.Utilities;

public class FileTransferDialog extends JFrame {

	private long filesize;
	private long transfered = 0;
	private JLabel transferLabel;
	private JLabel percentageLabel;
	private int counter = 0;
	private long timestamp = (new Date()).getTime();
	private long lastTransfered = 0;
	
	public FileTransferDialog(String filename, long filesize)
	{
		super("File transfer 0 B/s");
		this.setBounds(0, 0, 300, 200);
		this.filesize = filesize;	
				
		JLabel filenameLabel = new JLabel( "File: "+filename );
		filenameLabel.setBounds(10, 10, 280, 20);
		this.add(filenameLabel);
		
		JLabel filesizeLabel = new JLabel( "Size: "+Utilities.fileSizeUnits(filesize)+"b" );
		filesizeLabel.setBounds(10, 30, 280, 20);
		this.add(filesizeLabel);		
		
		this.transferLabel = new JLabel();
		this.transferLabel.setBounds(10, 50, 280, 20);
		this.add(this.transferLabel);
		
		
		this.percentageLabel = new JLabel();
		this.percentageLabel.setBounds(10, 70, 280, 20);
		this.add(this.percentageLabel);
		
		this.redraw();
		this.setLayout(null);
		this.setVisible(true);
		this.setResizable(false);
	}
	
	public void setTransfered( long transfered )
	{
		this.transfered = transfered;
		this.redraw();
	}
	
	private void redraw()
	{
		long percentage = 0;
		
		if( this.transfered >0)
		{
			percentage = this.transfered*100/this.filesize;
			
		}
		
		this.transferLabel.setText("Transferred: "+Utilities.fileSizeUnits(this.transfered));
		this.percentageLabel.setText("Progres: "+percentage+"%");
		
		if( ++this.counter > 500 )
		{
			long currentTimestamp 		= (new Date()).getTime();
			long differenceTime 		= currentTimestamp - this.timestamp;
			long differenceTransfered 	= this.transfered - this.lastTransfered;
			
			this.timestamp 				= currentTimestamp;
			this.lastTransfered 		= this.transfered;
			this.counter				= 0;
			
			long rate = 0;
			if( differenceTime > 0 )
			{
				rate = differenceTransfered/differenceTime*1000;
			}
			
			this.setTitle("File transfer: " + Utilities.fileSizeUnits(rate) + "/s");
			
		}
	}
	
}
