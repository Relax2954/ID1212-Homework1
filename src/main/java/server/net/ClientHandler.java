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
//import Protocol.common.Constants;
import Protocol.common.Message;
import Protocol.common.MessageException;
import Protocol.common.MsgType;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.List;
import java.util.Random;
import server.net.ChatServer;

/**
 *
 * @author Relax2954
 */
/**
 * Handles all communication with one particular chat client.
 */
class ClientHandler implements Runnable {

    private static final String JOIN_MESSAGE = " joined conversation.";
    private static final String LEAVE_MESSAGE = " left conversation.";
    private static final String USERNAME_DELIMETER = ": ";
    private final ChatServer server;
    private final Socket clientSocket;
    private final String[] conversationWhenStarting;
    private ObjectInputStream fromClient;
    private ObjectOutputStream toClient;
    private String username = "anonymous";
    private String guess = null;
//    private String testttt="lolanje";
    String pocetni = "Start game";
    private boolean connected;
    private Allwords myallwords;
    private int score = 0; //the total current score;
    private String chosenword;
    private volatile String checkerString;   //SHOULD IT BE VOLATILE, MAYBE ATOMIC?  WHY ARE THESE PRIVATE?????????
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
    ClientHandler(ChatServer server, Socket clientSocket, String[] conversation) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.conversationWhenStarting = conversation;
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
        if(theword==null)
            return "Please start the game before guessing";
        String capguess = myguess.toLowerCase();
        String capword = theword.toLowerCase();
        /*char[] capguessarray = capguess.toLowerCase().toCharArray();   NOT NEEDED -DEL
        char[] capwordarray = capword.toLowerCase().toCharArray();
        int minLength = Math.min(capguessarray.length, capwordarray.length);*/
        char[] capwordarray = capword.toLowerCase().toCharArray();
        
        
        if(remaining==0){
            return "Please start a new game.";
        }
        else if (capguess.length() != capword.length() && capguess.length() != 1) {
            remaining--;
            checkerString = String.valueOf(checker);
            return checkerString + "\nRemaining attempts left: " + remaining;
        } else if (capguess.equals(capword)) {
            score++;
            remaining = 0;
            return capword + "\nYour total score is " + score;
        } else if (!capword.contains(capguess)) {
            remaining--;
            checkerString = String.valueOf(checker);
            return checkerString + "\nRemaining attempts left: " + remaining;
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
            return checkerString + "\nRemaining attempts left: " + remaining;
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
        for (String entry : conversationWhenStarting) {
            sendMsg(entry);
        }
        while (connected) {
            try {
                Message msg = (Message) fromClient.readObject();
                switch (msg.getType()) {
                    /*case USER:
                    username = msg.getBody();                   NOT IMPLEMENTING THIS WAY, FREE DEL
                    server.broadcast(username + JOIN_MESSAGE);
                    break;*/
                    /*case Startgame:           NOT IMPLEMENTING THIS WAY, FREE DEL
                        testttt=msg.getBody();
                        String mojarijec= chooseWord();
                        sendMsg("WOOOOOOOT: "+mojarijec);*/
                    case ENTRY:  //game logic implemented here
                        String gameentry = msg.getBody();
                        if (gameentry.toLowerCase().contains(pocetni.toLowerCase())) {
                            chosenword = chooseWord();
                            remaining = chosenword.length();
                            checker = new char[chosenword.length()];
                            Arrays.fill(checker, '_');
                            checkerString = String.valueOf(checker);
                            sendMsg(checkerString+" "+ chosenword);
                        } else {
                           sendMsg("Please start a new game or guess the word");
                        }
                        break;

                    case GUESS:
                        guess = msg.getBody();
                        tempor = gamelogic(chosenword, guess);
                        sendMsg(tempor);

                        break;
                    case DISCONNECT:
                        sendMsg("You are now disconnected.");
                        disconnectClient();
                        /*server.broadcast(username + LEAVE_MESSAGE);*/
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
            Message msg = new Message(MsgType.BROADCAST, msgBody);
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
