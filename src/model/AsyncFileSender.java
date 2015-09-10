package model;

import generic.Config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.File;

import javax.net.ssl.*;

import controller.IMClientController;
import message.FileTransferRequestMessage;
import view.client.TransferNotifierInterface;

/**
 * Asynchronous file sender
 *
 * Enables sending incoming file transfers in a separate nonblocking thread
 *
 * @author Piotr Polak
 *
 */
public class AsyncFileSender extends Thread {

    private File file = null;
    private Socket socket = null;
    private String ip = null;
    private int port = -1;
    private TransferNotifierInterface notifier = null;

    /**
     * Default constructor
     * 
     * @param file
     * @param ip
     * @param port
     * @param notifier 
     */
    public AsyncFileSender(File file, String ip, int port, TransferNotifierInterface notifier) {
        this.file = file;
        this.ip = ip;
        this.port = port;
        this.notifier = notifier;

        this.start(); // Starts this thread
    }

    /**
     * Thread main method
     */
    @Override
    public void run() {
        // Initializing socket only if it was not created before
        if (this.socket == null) {
            // Checking ip and port
            if (this.ip != null && this.port > -1) {
                // Initializing desired socket
                try {
                    if (Config.USE_SSL) {
                        this.socket = SSLSocketFactory.getDefault().createSocket(this.ip, this.port);
                        ((SSLSocket) socket).setEnabledCipherSuites(((SSLSocket) socket).getSupportedCipherSuites());
                        ((SSLSocket) socket).startHandshake();
                    } else {
                        this.socket = new Socket(this.ip, this.port);
                    }
                } catch (Exception e) {
                    // Unable to connect
                    // TODO set notification message
                    return;
                }
            } else {
                // Wrong IP of port
                return;
            }
        }
        
        ObjectOutputStream out;
        FileInputStream fis;
        
        // Sending the file transfer envelope
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new FileTransferRequestMessage(IMClientController.getInstance().getUsername(), this.file.getName(), this.file.length()));
            
        } catch (IOException e) {
            return;
        }
        
        
        try {
            int bytesRead = 0;
            long totalBytesTransfered = 0;
            
            // Creating the buffer with the length specified in the config
            byte[] buffer = new byte[generic.Config.FILE_TRANSFER_BUFFER_SIZE];

            // Getting the socket output stream
            OutputStream outStream = socket.getOutputStream();
            
            try {

                // Creating input for reading the file
                fis = new FileInputStream(file);

                try {
                    // Reading from file in a loop
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        // Writing to buffer
                        outStream.write(buffer, 0, bytesRead); 
                        // Flushing the buffer
                        outStream.flush();
                        // Some statistics
                        totalBytesTransfered += bytesRead;
                        
                        // Set notification
                        this.notifyNotifier(totalBytesTransfered);
                    }
                    
                    // Flushing remaining buffer, just in case
                    outStream.flush();  

                } catch (IOException e) {
                    // TODO set notification message
                }

                try {
                    fis.close();
                } // Closing file input stream
                catch (IOException e) {
                    // TODO set notification message
                }

            } catch (FileNotFoundException e) {
                // TODO set notification message
            }

        } catch (IOException e) {
            // TODO set notification message
        }

    }

    /**
     * Notifies the notifier object about the number of bytes transfered
     * 
     * @param transfered 
     */
    private void notifyNotifier(long transfered) {
        if (this.notifier != null) {
            this.notifier.setTransfered(transfered);
        }
    }
}
