/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.startup;

import client.view.NonBlockingInterpreter;
/**
 *
 * @author Relax2954
 */

/**
 * Starts the client.
 */
public class Main {
    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        new NonBlockingInterpreter().start();
    }
}
