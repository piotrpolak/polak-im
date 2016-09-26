package model;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

import view.client.TransferNotifierInterface;

/**
 * Asynchronous file reader
 *
 * Enables reading incoming file transfers in a separate nonblocking thread
 *
 * @author Piotr Polak
 *
 */
public class AsyncFileReader extends Thread {

    private final Socket socket;
    private final String filename;
    private final long filesize;
    private TransferNotifierInterface notifier = null;

    /**
     * Default constructor
     * 
     * @param socket
     * @param filename
     * @param filesize
     * @param notifier
     */
    public AsyncFileReader(Socket socket, String filename, long filesize, TransferNotifierInterface notifier) {
        this.socket = socket;
        this.filename = filename;
        this.filesize = filesize;
        this.notifier = notifier;

        this.start(); // Starts this thread		
    }

    /**
     * Thread main method
     * 
     *
     */
    @Override
    public void run() {
        
        // Get file save path
        String fileSavePath = notifier.getTransferPath();
        
        // Abort file transfer
        if( fileSavePath == null )
        {
            notifier = null;
            return;
        }
        
        // Append filename previously received in the envelope
        fileSavePath += this.filename;

        // TODO prevent file overwrite, display confirmation popup
        
        // Attempt to create a new file
        File file = new File(fileSavePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO set notification message
            return;
        }

        // Attempt to create a file output stream
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            // TODO set notification message
            return;
        }
        
        // Attempt to create a socket input stream
        InputStream in;
        try {
            in = socket.getInputStream();
        } catch (IOException e) {
            // TODO set notification message
            return;
        }

        int bytesRead = 0;
        int allBytesRead = 0;
        
        // Creating the buffer with the length specified in the config
        byte[] buffer = new byte[generic.Config.FILE_TRANSFER_BUFFER_SIZE];

        try {
             // Reading from socket in a loop
            while (allBytesRead < this.filesize) {
                // Reading data from the stream
                bytesRead = in.read(buffer, 0, buffer.length);
                // Some statistics
                allBytesRead += bytesRead;
                
                // Set notification
                this.notifyNotifier(allBytesRead);

                // Redirecting socket stream to the file
                try {
                    fos.write(buffer, 0, bytesRead);
                    fos.flush();
                } catch (IOException e) {
                    // TODO set notification message
                }
            }
        } catch (IOException e) {
            // TODO set notification message
        }

        // Closing the file output stream
        try {
            fos.close();
        } catch (IOException e) {
            // Do nothing            
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
