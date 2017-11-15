/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Protocol.common;

/**
 *
 * @author Relax2954
 */
public enum MsgType {
    
    //start the actual game
    START,
    //startgame check
    GUESS,
    //The information that the server returns about the certain input
    GIVE,
    /*
    The client is about to disconnect and all the server resources related to
    the client should be released
     */
    DISCONNECT

}
