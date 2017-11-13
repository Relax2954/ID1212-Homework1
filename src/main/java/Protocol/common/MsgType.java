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

    // all the users playing in the hangman game 
    /*USER,*/
    //start the actual game
    /*Startgame,*/                               //NOT IMPLEMENTING THIS WAY, FREE DEL
    // an entry(guess that a user has made)
    ENTRY,
    //startgame check
    GUESS,
    //The information that the server returns about the entry
    BROADCAST,
    /*
    The client is about to disconnect and all the server resources related to
    the client should be released
     */
    DISCONNECT

}
