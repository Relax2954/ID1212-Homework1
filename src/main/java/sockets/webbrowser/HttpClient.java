/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets.webbrowser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Sends an HTTP request and prints the response.
 */

/**
 *
 * @author Relax2954
 */
public class HttpClient {
    
     /**
     * @param args The first command line argument is the host and the second is the port.
     */
    public static void main(String[] args) {
        String httpServer = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int timeoutMillis = 10000;
        String httpRequest = "GET / HTTP/1.1";
        String hostHeader = "Host: " + httpServer;

        try (Socket socket = new Socket(httpServer, serverPort)) {
            socket.setSoTimeout(timeoutMillis);
            PrintWriter toServer = new PrintWriter(socket.getOutputStream());
            toServer.println(httpRequest);
            toServer.println(hostHeader);
            toServer.println();
            toServer.flush();
            BufferedReader fromServer
                    = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str;
            while ((str = fromServer.readLine()) != null) {
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
