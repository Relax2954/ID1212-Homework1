/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import Protocol.common.Message;
import Protocol.common.MessageException;
import Protocol.common.MsgType;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Relax2954
 */
/**
 * Handles all communication with one particular client.
 */
class ClientHandler implements Runnable {

    private final TheServer server;
    private final Socket clientSocket;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private String guess = null;
    private boolean connected;
    private Allwords myallwords;
    private int score = 0; //the total current score;
    private String chosenword;
    private volatile String checkerString;   //SHOULD IT BE VOLATILE, MAYBE ATOMIC?  check private?
    private volatile int remaining = 0;  //remaining shots to take
    private char[] checker; //ovdje stavlja  capword cifru lokacije pogodjenog slova
    private String tempor; //this is for just printiing out the current ___f__c__

    /**
     * Creates a new instance, which will handle communication with one specific
     * client connected to the specified socket.
     *
     * @param clientSocket The socket to which this handler's client is
     * connected.
     */
    ClientHandler(TheServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        connected = true;

    }

    public String chooseWord() throws FileNotFoundException { //this is choosing a word from words.txt

        myallwords = new Allwords();
        Random rand = new Random();
        ArrayList<String> names = myallwords.getList();
        String randomword = names.get(rand.nextInt(names.size()));
        return randomword;
    }

    public String gamelogic(String theword, String myguess) { //this is where the game logic magic happens
        if (theword == null) {
            return "Please start the game before guessing";
        }
        String capguess = myguess.toLowerCase();
        String capword = theword.toLowerCase();
        char[] capwordarray = capword.toLowerCase().toCharArray();

        if (remaining == 0) {
            return "Please start a new game.";
        } else if (capguess.length() != capword.length() && capguess.length() != 1) {
            remaining--;
            checkerString = String.valueOf(checker);
            if (remaining == 0) {
                score--;
                return checkerString + "\nRemaining attempts: " + remaining + "\nYour total score is " + score;
            }
            return checkerString + "\nRemaining attempts: " + remaining;
        } else if (capguess.equals(capword)) {
            score++;
            remaining = 0;
            return capword + "\nYour total score is " + score;
        } else if (!capword.contains(capguess)) {
            remaining--;
            checkerString = String.valueOf(checker);
            if (remaining == 0) {
                score--;
                return checkerString + "\nRemaining attempts: " + remaining + "\nYour total score is " + score;
            }
            return checkerString + "\nRemaining attempts: " + remaining;
        } else {
            for (int i = 0; i < capword.length(); i++) {
                if (capguess.charAt(0) == capwordarray[i]) {
                    checker[i] = capguess.charAt(0);  //u ovom praznom array stavlja guess slova
                }
            }
            checkerString = String.valueOf(checker);
            if (!checkerString.contains("_")) {
                score++;
                remaining = 0;
                return capword + "\nYour total score is " + score;
            }
            return checkerString + "\nRemaining attempts: " + remaining;
        }
    }

    /**
     * The run loop handling all communication with the connected client.
     */
    @Override
    public void run() {
        try {
            fromClient = new ObjectInputStream(clientSocket.getInputStream());
            toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        while (connected) {
            try {
                Message msg = (Message) fromClient.readObject();
                switch (msg.getType()) {
                    case START:
                        String gameentry = msg.getBody();
                        if (gameentry.toLowerCase().contains("game".toLowerCase())) {
                            chosenword = chooseWord();
                            remaining = chosenword.length();
                            checker = new char[chosenword.length()];
                            Arrays.fill(checker, '_');
                            checkerString = String.valueOf(checker);
                            sendMsg(checkerString);
                        } else {
                            sendMsg("Please start game or guess the word.");
                        }
                        break;
                    case GUESS:
                        guess = msg.getBody();
                        tempor = gamelogic(chosenword, guess);
                        sendMsg(tempor);
                        break;
                    case WRONGINPUT:
                        sendMsg("Please start game or guess the word.");
                        break;
                    case DISCONNECT:
                        sendMsg("You are now disconnected.");
                        disconnectClient();
                        break;
                    default:
                        throw new MessageException("Received corrupt message: " + msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                disconnectClient();
                throw new MessageException(e);
            }
        }
    }

    /**
     * Sends the specified message to the connected client.
     *
     * @param msgBody The message to send.
     */
    void sendMsg(String msgBody) throws UncheckedIOException {
        try {
            Message msg = new Message(MsgType.NETWORKING, msgBody);
            toClient.writeObject(msg);
            toClient.flush();
            toClient.reset();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private void disconnectClient() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        connected = false;
        server.removeHandler(this);
    }
}
