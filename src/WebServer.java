import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer extends Thread {

    private Socket connection;
    int portNum;

    public WebServer(int port) {
        connection = new Socket();
        portNum = port;

    }

    public void run() {
        System.out.println("This thread is running...");

        ServerSocket servSocket;
        try {
            servSocket = new ServerSocket(portNum);
            while (true) {
                Socket socket = servSocket.accept();


            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void shutdown() {

    }


}

