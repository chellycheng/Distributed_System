package ResourceManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ResourceManager extends Remote{
    boolean commit(int xid) throws RemoteException;

    void abort(int xid) throws RemoteException;

    boolean shutdown() throws RemoteException;

    String getName() throws RemoteException;

}