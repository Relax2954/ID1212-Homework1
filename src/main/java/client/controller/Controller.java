/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import client.net.ServerConnection;
import client.net.OutputHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Relax2954
 */
/**
 * This controller decouples the view from the network layer. All methods, except
 * <code>disconnect</code>, submit their task to the common thread pool, provided by
 * <code>ForkJoinPool.commonPool</code>, and then return immediately.
 */
public class Controller {
    private final ServerConnection serverConnection = new ServerConnection();

    /**
     * @see ServerConnection#connect(java.lang.String, int,
     * se.kth.id1212.sockets.objprotocolchat.client.net.OutputHandler)
     */
    public void connect(String host, int port, OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(host, port, outputHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> outputHandler.handleMsg("Connected to " + host + ":" + port));
    }

    /**
     * @see ServerConnection#disconnect() Blocks until disconnection is completed.
     */
    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    /**
     * @see ServerConnection#sendUsername(java.lang.String)
     */
    /*public void sendUsername(String username) {
    CompletableFuture.runAsync(() -> {
    try {
    serverConnection.sendUsername(username);
    } catch (IOException ioe) {
    throw new UncheckedIOException(ioe);
    }
    });
    }*/
    
    public void sendGuess(String guess) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendGuess(guess);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
    
    /*//Send startgame                               NOT IMPLEMENTING THIS WAY, FREE DEL
    public void sendStartgame() {
    CompletableFuture.runAsync(() -> {
    try {
    serverConnection.sendStartgame();
    } catch (IOException ioe) {
    throw new UncheckedIOException(ioe);
    }
    });
    }*/

    /**
     * @see ServerConnection#sendChatEntry(java.lang.String)
     */
    public void sendMsg(String msg) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendChatEntry(msg);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
}