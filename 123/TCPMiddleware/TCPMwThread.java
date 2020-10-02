import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMwThread extends Thread{
    Socket socket;

    public TCPMwThread(Socket client){
        this.socket = client;
    }
    public void run()
    {
        try
        {
            BufferedReader inFromClient= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outToClient = new PrintWriter(socket.getOutputStream(), true);
            String message = null;
            while ((message = inFromClient.readLine())!=null)
            {
                System.out.println("message:"+message);



                outToClient.println("hello client from server THREAD, your result is: " + message );
            }
            System.out.println("has quited");
            socket.close();
        }
        catch (IOException e) {}

    }

}
