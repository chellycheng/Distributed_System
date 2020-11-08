package MwServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import Exception.*;

public interface MwInterface extends Remote{

    /*
        Implemented the same interface as IResourceManager
        Let Client know what are the available method to call
     */
    boolean addFlight(int var1, int var2, int var3, int var4) throws RemoteException;

    boolean addCars(int var1, String var2, int var3, int var4) throws RemoteException;

    boolean addRooms(int var1, String var2, int var3, int var4) throws RemoteException;

    int newCustomer(int var1) throws RemoteException;

    boolean newCustomer(int var1, int var2) throws RemoteException;

    boolean deleteFlight(int var1, int var2) throws RemoteException;

    boolean deleteCars(int var1, String var2) throws RemoteException;

    boolean deleteRooms(int var1, String var2) throws RemoteException;

    boolean deleteCustomer(int var1, int var2) throws RemoteException;

    int queryFlight(int var1, int var2) throws RemoteException;

    int queryCars(int var1, String var2) throws RemoteException;

    int queryRooms(int var1, String var2) throws RemoteException;

    String queryCustomerInfo(int var1, int var2) throws RemoteException;

    int queryFlightPrice(int var1, int var2) throws RemoteException;

    int queryCarsPrice(int var1, String var2) throws RemoteException;

    int queryRoomsPrice(int var1, String var2) throws RemoteException;

    boolean reserveFlight(int var1, int var2, int var3) throws RemoteException;

    boolean reserveCar(int var1, int var2, String var3) throws RemoteException;

    boolean reserveRoom(int var1, int var2, String var3) throws RemoteException;

    boolean bundle(int var1, int var2, Vector<String> var3, String var4, boolean var5, boolean var6) throws RemoteException;

    String getName() throws RemoteException;

    int start() throws RemoteException;

    boolean commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException;

    void abort(int xid) throws RemoteException,InvalidTransactionException;

    boolean shutdown() throws RemoteException;



}