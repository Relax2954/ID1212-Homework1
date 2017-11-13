/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import server.controller.Controller;

/**
 *
 * @author Relax2954
 */
/**
 * Receives chat messages and broadcasts them to all chat clients. All
 * communication to/from any chat node pass this server.
 */
public class ChatServer {

    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private final Controller contr = new Controller();
    private final List<ClientHandler> clients = new ArrayList<>();
    private int portNo = 8080;
    
    /**
     * @param args Takes one command line argument, the number of the port on
     * which the server will listen, the default is <code>8080</code>.
     */
    public static void main(String[] args)  {
        ChatServer server = new ChatServer();
        server.parseArguments(args);
        server.serve();
//        File allwords = new File("words.txt");
//        ArrayList<String> names = new ArrayList<String>();
//        Scanner in = new Scanner(allwords);
//        while (in.hasNextLine()) {
//            names.add(in.nextLine());
//        }
//        Collections.sort(names);
    }

    //the game logic
    void thegamelogic(String msg) {

        contr.appendEntry(msg);

    }

    /**
     * Sends the specified message to all connected clients
     *
     * @param msg The message to broadcast.
     */
    /*void broadcast(String msg) {                  NOT NEEDED- FREE DEL
    contr.appendEntry(msg);
    synchronized (clients) {
    clients.forEach((client) -> client.sendMsg(msg + " sta e"));
    // client.
    }
    }*/

    /**
     * The chat client handled by the specified <code>ClientHandler</code> has
     * disconnected from the server, and shall not participate in any future
     * communication.
     *
     * @param handler The handler of the disconnected client.
     */
    void removeHandler(ClientHandler handler) {
        synchronized (clients) {
            clients.remove(handler);
        }
    }

    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }

    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(this, clientSocket, contr.getConversation());
        synchronized (clients) {
            clients.add(handler);
        }
        Thread handlerThread = new Thread(handler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }

    private void parseArguments(String[] arguments) {
        if (arguments.length > 0) {
            try {
                portNo = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default.");
            }
        }
    }
}
