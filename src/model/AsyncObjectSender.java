package model;

import generic.AbstractController;
import generic.Config;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.IOException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import message.Message;


public class AsyncObjectSender extends Thread {

	
	private Object 				o						= null;	
	private Socket 				socket					= null;
	private String 				ip 						= null;
	private int 				port 					= -1;
	private boolean 			readAndProcessResponse 	= false;
	private AbstractController 	notifier 				= null;
	
	
	public AsyncObjectSender(Object o, Socket socket, AbstractController notifier, boolean readAndProcessResponse )
	{
		this.o 							= o;
		this.socket 					= socket;
		this.notifier 					= notifier;
		this.readAndProcessResponse 	= readAndProcessResponse;
		
		this.start(); // Starts this thread		
	}
	
	public AsyncObjectSender(Object o, String ip, int port, AbstractController notifier, boolean readAndProcessResponse )
	{
		this.o 							= o;
		this.ip 						= ip;
		this.port 						= port;
		this.notifier 					= notifier;
		this.readAndProcessResponse 	= readAndProcessResponse;
		
		this.start(); // Starts this thread		
	}
	

	/**
	 * Thread main method
	 */
	public void run()
	{
		if( this.socket == null )
		{
			if( this.ip != null && this.port > -1)
			{
				try {
					if( Config.USE_SSL )
					{
						this.socket = SSLSocketFactory.getDefault().createSocket(this.ip, this.port);
						((SSLSocket) socket).setEnabledCipherSuites(((SSLSocket) socket).getSupportedCipherSuites());
						((SSLSocket) socket).startHandshake();
					}
					else
					{
						this.socket = new Socket( this.ip, this.port );
					}
				}
				catch(Exception e)
				{
					this.notifyNotifier( e );
					return;
				}
			}
			else
			{
				this.notifyNotifier( new Exception("Unable to connect, ip or port not specified") );
				return;
			}			
		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(o);
//			if( !this.readAndProcessResponse ) // avoiding response close socked problem
//			{
//				out.close(); 
//			}
			
		}
		catch( IOException e ) {
			this.notifyNotifier( e );
		}
		
		
		
		//
		// Processing response, if specified
		//
		if( this.readAndProcessResponse )
		{
			try
			{
				ObjectInputStream in = new ObjectInputStream( socket.getInputStream() );
			
				try
				{
					Object o = in.readObject();
					this.handleIncomingMessage( (Message) o, socket );
				}
				catch( ClassNotFoundException e ) {}
				in.close();
			}
			catch( IOException e )
			{
				this.notifyNotifier( e );
			}
		}
		
	}
	
	/**
	 * Notifies notifier, if specified
	 * @param e
	 */
	private void notifyNotifier( Exception e )
	{
		if( this.notifier != null )
		{
			this.notifier.notify( e );
		}
	}
	
	/**
	 * Handles incoming messafe if notifier specified
	 * @param m
	 * @param socket
	 */
	private void handleIncomingMessage( Message m, Socket socket )
	{
		if( this.notifier != null )
		{
			this.notifier.handleIncomingMessage( m, socket );
		}
	}
}
