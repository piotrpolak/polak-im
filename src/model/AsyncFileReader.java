package model;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;

import view.client.FileTransferDialog;

public class AsyncFileReader extends Thread {

    private Socket socket;
    private String filename;
    private long filesize;
    private FileTransferDialog notifier = null;

    public AsyncFileReader(Socket socket, String filename, long filesize, FileTransferDialog notifier) {
        this.socket = socket;
        this.filename = filename;
        this.filesize = filesize;
        this.notifier = notifier;

        this.start(); // Starts this thread		
    }

    /**
     * Thread main method
     */
    public void run() {
        String fileSavePath = "";
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select destination directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(notifier) == JFileChooser.APPROVE_OPTION) {
            fileSavePath = chooser.getSelectedFile().getAbsolutePath() + java.io.File.pathSeparator + this.filename;

        } else {
            notifier.setVisible(false);
            notifier = null;
            return;
        }

        File file = new File(fileSavePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
        }

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        InputStream in;
        try {
            in = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        int bytesRead = 0;
        int allBytesRead = 0;
        byte[] buffer = new byte[generic.Config.FILE_TRANSFER_BUFFER_SIZE];

        try {
            while (allBytesRead < this.filesize) {
                bytesRead = in.read(buffer, 0, buffer.length);
                allBytesRead += bytesRead;
                this.notifyNotifier(allBytesRead);

                try {
                    fos.write(buffer, 0, bytesRead);
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }

        try {
            fos.close();
        } catch (IOException e) {
            //e.printStackTrace();
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
