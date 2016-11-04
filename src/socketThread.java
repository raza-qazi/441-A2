import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class socketThread extends Thread {
    // Internal socket
    private Socket socket;
    // String pattern checker - assume that all files must be in root directory
    private String pattern = "(GET)( )(\\/)([a-zA-Z]+)(\\.|)([a-zA-Z]+)( )(HTTP)(\\/)(1\\.0|1\\.1)";
    // format for date
    private SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");

    public socketThread(Socket sc) {
            socket = sc;
    }

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
                        // COnvert file to byte array
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
                    out.println(response);
                    System.out.println("Sending out: " + objFile[0]);

                    // Send object to client
                    socket.getOutputStream().write(data);
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

    public String generateCurrentDate() {
        //SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
        Date d = new Date();
        return sdf.format(d);
    }
}

