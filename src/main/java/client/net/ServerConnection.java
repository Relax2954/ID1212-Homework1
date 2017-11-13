/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
//import Protocol.common.Constants;
import Protocol.common.Message;
import Protocol.common.MessageException;
import Protocol.common.MsgType;

/**
 *
 * @author Relax2954
 */
/**
 * Manages all communication with the server.
 */
public class ServerConnection {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private boolean connected;

    /**
     * Creates a new instance and connects to the specified server. Also starts a listener thread
     * receiving broadcast messages from server.
     *
     * @param host             Host name or IP address of server.
     * @param port             Server's port number.
     * @param broadcastHandler Called whenever a broadcast is received from server.
     * @throws IOException If failed to connect.
     */
    public void connect(String host, int port, OutputHandler broadcastHandler) throws
            IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());
        new Thread(new Listener(broadcastHandler)).start();
    }

    /**
     * Closes the connection with the server and stops the broadcast listener thread.
     *
     * @throws IOException If failed to close socket.
     */
    public void disconnect() throws IOException {
        sendMsg(MsgType.DISCONNECT, null);
        socket.close();
        socket = null;
        connected = false;
    }

    /**
     * Sends the user's username to the server. That username will be prepended to all messages
     * originating from this client, until a new username is specified.
     *
     * @param username The current user's username.
     */
    /*public void sendUsername(String username) throws IOException {
    sendMsg(MsgType.USER, username);
    }*/
    
    public void sendGuess(String guess) throws IOException {
        sendMsg(MsgType.GUESS, guess);
    }

    /**
     * Sends a chat entry to the server, which will broadcast it to all clients, including the
     * sending client.
     *
     * @param msg The message to broadcast.
     */
    public void sendChatEntry(String msg) throws IOException {
        sendMsg(MsgType.ENTRY, msg);
    }
    
    /*public void sendStartgame() throws IOException {     NOT IMPLEMENTING THIS WAY, FREE DEL
    sendMsg(MsgType.Startgame, "hmmm");
    }*/

    private void sendMsg(MsgType type, String body) throws IOException {
        Message msg = new Message(type, body);
        toServer.writeObject(msg);
        toServer.flush();
        toServer.reset();
    }

    private class Listener implements Runnable {
        private final OutputHandler outputHandler;

        private Listener(OutputHandler outputHandler) {
            this.outputHandler = outputHandler;
        }

        @Override
        public void run() {
            try {
                for (;;) {
                    outputHandler.handleMsg(extractMsgBody((Message) fromServer.readObject()));
                }
            } catch (Throwable connectionFailure) {
                if (connected) {
                    outputHandler.handleMsg("Lost connection.");
                }
            }
        }

        private String extractMsgBody(Message msg) {
            if (msg.getType() != MsgType.BROADCAST) {
                throw new MessageException("Received corrupt message: " + msg);
            }
            return msg.getBody();
        }
    }
}
