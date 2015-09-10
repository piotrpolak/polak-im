package generic;

/**
 * Application config variables
 *
 * @author Piotr Polak
 *
 */
public class Config {

    /**
     * Server listen port
     */
    public static final int SERVER_LISTEN = 5003;
    
    /**
     * Buffer size for file transfer
     */
    public static final int FILE_TRANSFER_BUFFER_SIZE = 1024 * 4;
    
    /**
     * Whether to use SSL or no
     */
    public static final boolean USE_SSL = false;
}
