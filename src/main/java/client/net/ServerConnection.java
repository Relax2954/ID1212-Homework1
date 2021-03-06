package client.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
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
     * Creates a new instance and connects to the specified server. Also starts
     * a listener thread receiving broadcast messages from server.
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
     * Closes the connection with the server.
     *
     * @throws IOException If failed to close socket.
     */
    public void disconnect() throws IOException {
        sendMsg(MsgType.DISCONNECT, null);
        socket.close();
        socket = null;
        connected = false;
    }

    public void sendGuess(String guess) throws IOException {
        sendMsg(MsgType.GUESS, guess);
    }

    public void sendStartgame(String start) throws IOException {
        sendMsg(MsgType.START, start);
    }

    public void sendWrongInput() throws IOException {
        sendMsg(MsgType.WRONGINPUT, "");
    }

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
            if (msg.getType() != MsgType.NETWORKING) {
                throw new MessageException("Received corrupt message: " + msg);
            }
            return msg.getBody();
        }
    }
}
