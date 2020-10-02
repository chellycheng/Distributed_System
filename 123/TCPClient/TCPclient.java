import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPclient {

    private static String serverName = "localhost";
    private static int port = 1098;
    public static void main(String args[]) throws IOException
    {

        Socket socket= new Socket(serverName, port);

        PrintWriter outToServer= new PrintWriter(socket.getOutputStream(),true);
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in));

        while(true) // works forever
        {
            String readerInput=bufferedReader.readLine(); // read user's input
            if(readerInput.equals("quit"))
                break;


            outToServer.println(readerInput); // send the user's input via the output stream to the server
            String res=inFromServer.readLine(); // receive the server's result via the input stream from the server
            System.out.println("result: "+res); // print the server result to the user
        }

        socket.close();
    }

}