/**
 * @author  Raza Qazi
 * @version 1.0, 11/4/2016
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class WebServer extends Thread {

    private volatile boolean shutdown = false;
    private Socket socket;
    private ServerSocket sc = null;
    private int portNum;

    /**
     * Default constructor to initialize port number
     *
     * @param port
     */
    public WebServer(int port) {
        // Initialization
        portNum = port;

    }

    /**
     * Establishes a TCP connection to client by waiting for
     * a client to accept a connection. Spawns new worker threads
     * to handle multiple GET requests simultaneously
     *
     * Note: overrides run() from Thread
     */
    public void run() {
        // Open server socket
        try {
            // Wait for a client to connect
            sc = new ServerSocket(portNum);
            // Set socket timeout
            sc.setSoTimeout(1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!shutdown) {
            try {
                // Wait for a client to accept, else timeout
                socket = sc.accept();

                // Spawn and start a new thread to handle connection
                Thread tcpCon = new socketThread(socket);
                tcpCon.start();

            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * When executes, stop the Main thread from accepting connections
     * and closes all sockets.
     */
    public void shutdown() {
        // Flag to stop the while loop
        shutdown = true;
        try {
            // Close all sockets
            sc.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}

