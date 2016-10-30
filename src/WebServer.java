import java.io.*;
import java.net.*;
import java.util.*;

public class WebServer extends Thread {

    private Socket connection;

    public WebServer(Socket socket) {
        this.connection = socket;

    }

    public void run() {
        System.out.println("This thread is running...");

        ServerSocket servSocket;
        try {
            servSocket = new ServerSocket(8888);
            while (true) {
                Socket socket = servSocket.accept();


            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void start() {

    }

}

