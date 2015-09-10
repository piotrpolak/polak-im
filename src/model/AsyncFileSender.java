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
import view.client.FileTransferDialog;
import message.FileTransferRequestMessage;

public class AsyncFileSender extends Thread {

    private File file = null;
    private Socket socket = null;
    private String ip = null;
    private int port = -1;
    private FileTransferDialog notifier = null;

    public AsyncFileSender(File file, String ip, int port, FileTransferDialog notifier) {
        this.file = file;
        this.ip = ip;
        this.port = port;
        this.notifier = notifier;

        this.start(); // Starts this thread
    }

    /**
     * Thread main method
     */
    public void run() {
        if (this.socket == null) {
            if (this.ip != null && this.port > -1) {
                try {
                    if (Config.USE_SSL) {
                        this.socket = SSLSocketFactory.getDefault().createSocket(this.ip, this.port);
                        ((SSLSocket) socket).setEnabledCipherSuites(((SSLSocket) socket).getSupportedCipherSuites());
                        ((SSLSocket) socket).startHandshake();
                    } else {
                        this.socket = new Socket(this.ip, this.port);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                return;
            }
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new FileTransferRequestMessage(IMClientController.getInstance().getUsername(), this.file.getName(), this.file.length()));

            FileInputStream file_input = null;
            int buffer_read_n_bytes = 0;
            long total_transfered = 0;
            byte[] buffer = new byte[generic.Config.FILE_TRANSFER_BUFFER_SIZE];

            OutputStream outStream = socket.getOutputStream();
            try {

                file_input = new FileInputStream(file);

                try {

                    while ((buffer_read_n_bytes = file_input.read(buffer)) != -1) { // While reading from file
                        outStream.write(buffer, 0, buffer_read_n_bytes); // Writing to buffer

                        outStream.flush(); // Flushing the buffer
                        total_transfered += buffer_read_n_bytes;
                        this.notifyNotifier(total_transfered);
                    }
                    outStream.flush();  // Flushing remaining buffer

                } catch (IOException e) {
                }

                try {
                    file_input.close();
                } // Closing file input stream
                catch (IOException e) {
                }

            } catch (FileNotFoundException e) {
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Notifies notifier, if specified
     *
     * @param e
     */
    private void notifyNotifier(long transfered) {
        if (this.notifier != null) {
            this.notifier.setTransfered(transfered);
        }
    }
}
