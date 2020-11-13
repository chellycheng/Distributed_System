package Client;

import MwServer.MwInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

import java.util.*;
import java.lang.*;
import java.io.*;

public class Tester extends Client implements Runnable
{
	private static String s_serverHost = "localhost";
	private static int s_serverPort = 1018;
	private static String s_serverName = "MwServer";
    private static int success1;
    private static int success2;    
    private static int numC;
    private static String s_rmiPrefix = "group_18_";
    private static float[] t1 = new float[6];
    private static float[] t2 = new float[6];
    private static String args1;
    private static String args2;

    @Override
    public void run(){
		try {
			Tester client = new Tester();
			client.connectServer();
			client.start(Double.parseDouble(args1)/numC, Integer.parseInt(args2)/numC);
		} 
		catch (Exception e) {    
			System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
    }
	public static void main(String args[])
	{	

		// Set the security policy
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
        }
        args1 = args[1];
        args2 = args[2];
        numC = Integer.parseInt(args[0]);
        Thread[] tL = new Thread[numC];
        success1 = 0;
        success2 = 0;
        try{
            for(int i = 0; i < numC; i++){
                tL[i] = new Thread(new Tester());
            }
            for(int i = 0; i < numC; i++){
                tL[i].start();
            }
            for(int i = 0; i < numC; i++){
                tL[i].join();
            }
            for(int i = 0; i < 6; i++){
                t1[i] = t1[i]/(float)success1;
                t2[i] = t2[i]/(float)success2;
            }
            System.out.println();
            System.out.println("Client Number: " + args[0]);
            System.out.println("Transaction per Second: " + args[1]);
            System.out.println("Total Transactions: " + args[2]);
            System.out.println("Time Cost per Transaction: ([communication, RM, LM, TM, total])");
            System.out.println("One RM: " + Arrays.toString(t1));
            System.out.println("All RMs: " + Arrays.toString(t2));
            System.out.println("Success transaction oneRM: " + success1);
            System.out.println("Success transaction allRM: " + success2);
        }
        catch(Exception e){

        }
		// Get a reference to the RMIRegister

	}

	public Tester()
	{
		super();
	}

	public void connectServer()
	{
		connectServer(s_serverHost, s_serverPort, s_serverName);
	}

	public void connectServer(String server, int port, String name)
	{
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					m_resourceManager = (MwInterface)registry.lookup(s_rmiPrefix + name);
					System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
					break;
				}
				catch (NotBoundException|RemoteException e) {
					if (first) {
						e.printStackTrace();
						System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
						first = false;
					}
				}
				Thread.sleep(500);
			}
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}

    public void start(double tps, int time) {
        try{
            //    float[] otimes = Iter(tps, time);
                float[] atimes = Iter2(tps, time);

        }
        catch(Exception e){

        }
    }
    
    public float[] Iter(double tps, int time){
        float[] times = new float[6];
        ArrayList<long[]> trans = new ArrayList<long[]>();
        int iterT = 0;
        while(iterT < time){
            long start = System.currentTimeMillis();
            trans.add(oneTrans());
            long end = System.currentTimeMillis();
            while(end - start < 1000/tps){
                end = System.currentTimeMillis();
            }
            iterT++;
        }
        for(long[] t: trans){
            times[0] += t[0]/(float)time;
            times[1] += t[1]/(float)time;
            times[2] += t[2]/(float)time;
            times[3] += t[3]/(float)time;
            times[4] += t[4]/(float)time;
            times[5] += t[5]/(float)time;
            t2[0] += t[0];
            t2[1] += t[1];
            t2[2] += t[2];
            t2[3] += t[3];
            t2[4] += t[4];
            t2[5] += t[5];
        }
        return times;
    }
    public float[] Iter2(double tps, int time){
        float[] times = new float[6];
        ArrayList<long[]> trans = new ArrayList<long[]>();
        int iterT = 0;
        while(iterT < time){
            long start = System.currentTimeMillis();
            trans.add(allTrans());
            long end = System.currentTimeMillis();
            while(end - start < 1000/tps){
                end = System.currentTimeMillis();
            }
            iterT++;
        }
        for(long[] t: trans){
            times[0] += t[0]/(float)time;
            times[1] += t[1]/(float)time;
            times[2] += t[2]/(float)time;
            times[3] += t[3]/(float)time;
            times[4] += t[4]/(float)time;
            times[5] += t[5]/(float)time;
            t2[0] += t[0];
            t2[1] += t[1];
            t2[2] += t[2];
            t2[3] += t[3];
            t2[4] += t[4];
            t2[5] += t[5];
        }
        return times;
    }
    public long[] oneTrans() {

        String[] places = new String[100];
        for(int i = 0; i < places.length; i++){
            places[i] = "" + i;
        }
        int index = (int)(Math.random() * places.length);
        int price = (int)(Math.random() * 1000);
        int amount = (int)(Math.random() * 50);
        int customerID = (int)(Math.random() * 1000000);
        int flightNumber = (int)(Math.random() * 200);

        long[] time = new long[6];//[communication, RM, LM, TM]
        try{
            int xid = m_resourceManager.start();
            long start = System.currentTimeMillis();
            long[] newC = m_resourceManager.newCustomer(xid, customerID);//[received point, RM, LM, TM, sent point]
            long[] addC = m_resourceManager.addCars(xid, places[index], 3, price);
            long[] resC = m_resourceManager.addCars(xid, places[index], 3, price);
            long[] qCP = m_resourceManager.addCars(xid, places[index], 3, price);
            long[] com = m_resourceManager.commit(xid);
            long end = System.currentTimeMillis();
            time[0] = newC[0] - start + addC[0] - newC[4] + resC[0] - addC[4] + qCP[0] - resC[4] + com[0] - qCP[4] + end - com[4];
            time[1] = newC[1] + addC[1] + resC[1] + qCP[1] + com[1];
            time[2] = newC[2] + addC[2] + resC[2] + qCP[2] + com[2];
            time[3] = newC[3] + addC[3] + resC[3] + qCP[3] + com[3];
            time[4] = time[0] + time[1] + time[2] + time[3];
            time[5] = end - start;
            success1++;

        }
        catch(Exception e){  
        }

        return time;
    }

    public long[] allTrans() {

        String[] places = new String[100];
        for(int i = 0; i < places.length; i++){
            places[i] = "" + i;
        }
        int index = (int)(Math.random() * places.length);
        int price = (int)(Math.random() * 1000);
        int amount = (int)(Math.random() * 50);
        int customerID = (int)(Math.random() * 1000000);
        int flightNumber = (int)(Math.random() * 200);
        long[] time = new long[6];//[communication, RM, LM, TM]
        try{
            int xid = m_resourceManager.start();
            long start = System.currentTimeMillis();
            long[] newC = m_resourceManager.newCustomer(xid, customerID);//[received point, RM, LM, TM, sent point, communication]
            long[] addC = m_resourceManager.addCars(xid, places[index], 3, price);
            long[] resC = m_resourceManager.addRooms(xid, places[index], 3, price);
            long[] qCP = m_resourceManager.addFlight(xid, flightNumber, 12, price);
            long[] com = m_resourceManager.commit(xid);
            long end = System.currentTimeMillis();
            time[0] = newC[0] - start + addC[0] - newC[4] + resC[0] - addC[4] + qCP[0] - resC[4] + com[0] - qCP[4] + end - com[4];
            time[1] = newC[1] + addC[1] + resC[1] + qCP[1] + com[1];
            time[2] = newC[2] + addC[2] + resC[2] + qCP[2] + com[2];
            time[3] = newC[3] + addC[3] + resC[3] + qCP[3] + com[3];
            time[4] = time[0] + time[1] + time[2] + time[3];
            time[5] = end - start;
            success2++;
        }
        catch(Exception e){
        }


        return time;
    }

}

