package message;

import java.io.Serializable;

public class FileTransferRequestMessage extends Message implements Serializable {

    static final long serialVersionUID = 4355544;
    private String filename;
    private String from;
    private long filesize;

    public FileTransferRequestMessage(String from, String filename, long filesize) {
        this.filename = filename;
        this.filesize = filesize;
        this.from = from;
    }

    public String getFileName() {
        return this.filename;
    }

    public String getFrom() {
        return this.from;
    }

    public long getFileSize() {
        return this.filesize;
    }
}
