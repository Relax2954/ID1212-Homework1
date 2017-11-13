/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

/**
 *
 * @author Relax2954
 */
/**
 * Defines all commands that can be performed by a user of the chat application.
 */
public enum Command {
    /**
     * Specifies a user name.
     */
    USER,
    //start the game
    //Startgame,
    
    //guess
    GUESS,
    /**
     * Establish a connection to the server. The first parameter is IP address (or host name), the
     * second is port number.
     */
    CONNECT,
    /**
     * Leave the application.
     */
    QUIT,
    /**
     * No command was specified. This means the entire command line is interpreted as an entry in
     * the game.
     */
    NO_COMMAND
}
