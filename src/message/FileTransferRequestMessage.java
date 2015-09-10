package message;

import java.io.Serializable;

/**
 * File transfer request message
 * 
 * A file envelope, when this message is received, the client opens a binary 
 * connection for receiving the incoming file
 *
 * @author Piotr Polak
 *
 */
public class FileTransferRequestMessage extends Message implements Serializable {

    static final long serialVersionUID = 4355544;
    private final String filename;
    private final String from;
    private final long filesize;

    /**
     * Default constructor
     * 
     * @param from
     * @param filename
     * @param filesize 
     */
    public FileTransferRequestMessage(String from, String filename, long filesize) {
        this.filename = filename;
        this.filesize = filesize;
        this.from = from;
    }

    /**
     * Returns filename
     * 
     * @return 
     */
    public String getFileName() {
        return this.filename;
    }

    /**
     * Returns sender name
     * 
     * @return 
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Returns declared file size
     * 
     * @return 
     */
    public long getFileSize() {
        return this.filesize;
    }
}
