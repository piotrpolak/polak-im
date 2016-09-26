/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.client;

/**
 *
 * @author pepis
 */
public interface TransferNotifierInterface {
    
    /**
     * Receives notification about the number of bytes received
     * 
     * @param transfered
     * @return success
     */
    public boolean setTransfered(long transfered);
    
    /**
     * Returns the save path
     * 
     * @return 
     */
    public String getTransferPath();
}
