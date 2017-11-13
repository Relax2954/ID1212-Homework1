/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets.webserver;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.File;


/**
 *
 * @author Relax2954
 */
public class HttpServer {
    
    private static final int LINGER_TIME = 5000;
    private File rootDir = new File("www");
    private int portNo = 8080;

    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                clientSocket.setSoLinger(true, LINGER_TIME);
                Thread handler = new Thread(new RequestHandler(clientSocket, rootDir));
                handler.setPriority(Thread.MAX_PRIORITY);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }

    /**
     * @param args The first command line argument is the directory where the server will look for
     *             requested files, the default value is <code>www</code>. The second argument is
     *             the port number on which the server will listen, the default is
     *             <code>8080</code>.
     */
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.parseArguments(args);
        server.serve();
    }

    private void parseArguments(String[] arguments) {
        if (arguments.length > 0) {
            rootDir = new File(arguments[0]);
        }
        if (arguments.length > 1) {
            try {
                portNo = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default.");
            }
        }
    }
    
}
