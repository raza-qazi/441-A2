/**
 * @author  Raza Qazi
 * @version 1.0, 11/4/2016
 */

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class socketThread extends Thread {
    // Internal socket
    private Socket socket;
    // String pattern checker - assume that all files must be in root directory
    private String pattern = "(GET)( )(\\/)([a-zA-Z0-9]+)(\\.|)([a-zA-Z0-9]+)( )(HTTP)(\\/)(1\\.0|1\\.1)";
    // format for date
    private SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");

    /**
     * Default constructor to initialize socket passed in from WebServer
     *
     * @param sc
     */
    public socketThread(Socket sc) {
            socket = sc;
    }

    /**
     * Worker thread responsible for receiving header information
     * from client, interpreting the data, and sending a response
     * back with a file (if required)
     *
     * Note: overrides run() from Thread
     */
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // Receive first line of request header
            String header = in.readLine();

            // Generate current date and store into date
            String date = generateCurrentDate();

            // Flag true if header is well formed
            // Uses regex to determine validity of header content.
            boolean wellFormed = Pattern.matches(pattern, header);

            if (wellFormed) {
                // Determine if file exists
                String[] objFileO = header.split(" /");
                String[] objFile = objFileO[1].split(" ");

                // Specify as Type File
                File file = new File(objFile[0]);

                // If file exists - 200 OK
                if (file.isFile()) {
                    Path path = Paths.get(objFile[0]);
                    byte[] data = new byte[0];
                    try {
                        // Convert file to byte array
                        data = Files.readAllBytes(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Format header response
                    String response =
                            "HTTP/1.0 200 OK\r\n" +
                            "Server: win10PC\r\n" +
                            "Date: " + date + "\r\n" +
                            "Last-Modified: " + sdf.format(file.lastModified())+ "\r\n" +
                            "Content-Length: " + data.length + "\r\n" +
                            "Connection: closed\r\n";

                    // Send to client
                    out.print(response);
                    out.println();
                    System.out.println("Sending out: " + objFile[0]);

                    // Send object to client
                    socket.getOutputStream().write(data);
                    System.out.println("Sent: "+ objFile[0]);
                } else {
                    // File not found - 404 Not Found
                    String response =
                            "HTTP/1.0 404 Not Found\r\n" +
                            "Server: win10PC\r\n" +
                            "Date: " + date + "\r\n" +
                            "Connection: close\r\n";
                    out.println(response);
                }
            }
            else {
                // Server sent a bad request - 400 Bad Request
                String response =
                        "HTTP/1.0 400 Bad Request\r\n" +
                        "Server: win10PC\r\n" +
                        "Date: " + date + "\r\n" +
                        "Connection: close\r\n";
                out.println(response);
                out.println();
            }

            out.close();
            socket.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates current date using standard format clients expect
     *
     * @return String of Current date
     */
    public String generateCurrentDate() {
        //SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
        Date d = new Date();
        return sdf.format(d);
    }
}

