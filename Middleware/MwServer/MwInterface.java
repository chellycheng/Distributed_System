package MwServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;
import Exceptions.*;

public interface MwInterface extends Remote{

    /*
        Implemented the same interface as IResourceManager
        Let Client know what are the available method to call
     */
    long[] addFlight(int var1, int var2, int var3, int var4) throws RemoteException,InvalidTransactionException;

    long[] addCars(int var1, String var2, int var3, int var4) throws RemoteException,InvalidTransactionException;

    long[] addRooms(int var1, String var2, int var3, int var4) throws RemoteException,InvalidTransactionException;

    int newCustomer(int var1) throws RemoteException,InvalidTransactionException;

    long[] newCustomer(int var1, int var2) throws RemoteException,InvalidTransactionException;

    boolean deleteFlight(int var1, int var2) throws RemoteException,InvalidTransactionException;

    boolean deleteCars(int var1, String var2) throws RemoteException,InvalidTransactionException;

    boolean deleteRooms(int var1, String var2) throws RemoteException,InvalidTransactionException;

    boolean deleteCustomer(int var1, int var2) throws RemoteException,InvalidTransactionException;

    int queryFlight(int var1, int var2) throws RemoteException,InvalidTransactionException;

    int queryCars(int var1, String var2) throws RemoteException,InvalidTransactionException;

    int queryRooms(int var1, String var2) throws RemoteException,InvalidTransactionException;

    String queryCustomerInfo(int var1, int var2) throws RemoteException,InvalidTransactionException;

    int queryFlightPrice(int var1, int var2) throws RemoteException,InvalidTransactionException;

    long[] queryCarsPrice(int var1, String var2) throws RemoteException,InvalidTransactionException;

    int queryRoomsPrice(int var1, String var2) throws RemoteException,InvalidTransactionException;

    boolean reserveFlight(int var1, int var2, int var3) throws RemoteException,InvalidTransactionException;

    long[] reserveCar(int var1, int var2, String var3) throws RemoteException,InvalidTransactionException;

    boolean reserveRoom(int var1, int var2, String var3) throws RemoteException,InvalidTransactionException;

    boolean bundle(int var1, int var2, Vector<String> var3, String var4, boolean var5, boolean var6) throws RemoteException,InvalidTransactionException;

    String getName() throws RemoteException,InvalidTransactionException;

    int start() throws RemoteException,InvalidTransactionException;

    long[] commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException;

    void abort(int xid) throws RemoteException,InvalidTransactionException;

    boolean shutdown() throws RemoteException,InvalidTransactionException;



}