import MwServer.MwInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class MwImp implements MwInterface {
    public static void mian(String[] args) throws Exception{
        ArrayList<String> hostnames = new ArrayList<String>();
        ArrayList<Integer> portnums = new ArrayList<Integer>();

        int portnum = Integer.parseInt(arg[0]);
        boolean listening = true;

        try{
            ServerSocket serverSockect = new ServerSocket(portnum);
            while(listening){
                continue;
            }
        }
        catch(IO Exception e){

        }
    }
}