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
 * This class provides a thread safe output, all methods are synchronized.
 */
class ThreadSafeStdOut {

    /**
     * Prints the specified output to <code>System.out</code>,
     *
     * @param output The output to print.
     */
    synchronized void print(String output) {
        System.out.print(output);
    }

    /**
     * Prints the specified output, plus a line break, to
     * <code>System.out</code>,
     *
     * @param output The output to print.
     */
    synchronized void println(String output) {
        System.out.println(output);
    }
}
