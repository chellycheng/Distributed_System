import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPMw {
    private static int port = 1098;
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("server ready");

        while(true){
            Socket socket =  serverSocket.accept();
            new TCPMwThread(socket).start();
        }

    }
}
