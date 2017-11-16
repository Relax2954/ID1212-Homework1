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

/**
 *
 * @author Relax2954
 */
/**
 * This controller decouples the view from the network layer. All methods,
 * except <code>disconnect</code>, submit their task to the common thread pool,
 * provided by <code>ForkJoinPool.commonPool</code>, and then return
 * immediately.
 */
public class Controller {

    private final ServerConnection serverConnection = new ServerConnection();

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
     * @see ServerConnection#disconnect() Blocks until disconnection is
     * completed.
     */
    public void disconnect() throws IOException {
        serverConnection.disconnect();
    }

    public void sendGuess(String guess) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendGuess(guess);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    //Send startgame                               
    public void sendStartgame(String start) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendStartgame(start);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
    
    public void sendWrongInput() {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendWrongInput();
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
    
    
}
