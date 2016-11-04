import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class WebServer extends Thread {


    private volatile boolean shutdown = false;
    ExecutorService executor;
    Socket socket;
    ServerSocket sc = null;
    int portNum;

    public WebServer(int port) {
        // Initialization
        portNum = port;

    }

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
                socket = sc.accept();

                Thread tcpCon = new socketThread(socket);
                tcpCon.start();

            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        shutdown = true;
        try {
            sc.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}

