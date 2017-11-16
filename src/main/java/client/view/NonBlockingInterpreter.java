/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;
import java.util.Scanner;
import client.controller.Controller;
import client.net.OutputHandler;

/**
 *
 * @author Relax2954
 */
/**
 * Reads and interprets user commands. The command interpreter will run in a separate thread, which
 * is started by calling the <code>start</code> method. Commands are executed in a thread pool, a
 * new prompt will be displayed as soon as a command is submitted to the pool, without waiting for
 * command execution to complete.
 */
public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private boolean receivingCmds = false;
    private Controller contr;
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();

    /**
     * Starts the interpreter. The interpreter will be waiting for user input when this method
     * returns. Calling <code>start</code> on an interpreter that is already started has no effect.
     */
    public void start() {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        contr = new Controller();
        new Thread(this).start();
    }

    /**
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        contr.disconnect();
                        break;
                    case CONNECT:
                        contr.connect(cmdLine.getParameter(0),
                                      Integer.parseInt(cmdLine.getParameter(1)),
                                      new ConsoleOutput());
                        break;
                    case START:     
                        contr.sendStartgame(cmdLine.getParameter(0));
                        break;
                    case GUESS:
                        contr.sendGuess(cmdLine.getParameter(0));
                        break;
                    default:
                        contr.sendWrongInput();
                }
            } catch (Exception e) {
                outMgr.println("Operation failed");
            }
        }
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }

    private class ConsoleOutput implements OutputHandler {
        @Override
        public void handleMsg(String msg) {
            outMgr.println((String) msg);
            outMgr.print(PROMPT);
        }
    }
}
