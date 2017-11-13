/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.net;

/**
 *
 * @author Relax2954
 */
/**
 * Handles broadcast messages from server.
 */
public interface OutputHandler {

    /**
     * Called when a broadcast message from the server has been received. That
     * message originates from one of the clients.
     *
     * @param msg The message from the server.
     */
    public void handleMsg(String msg);
}
