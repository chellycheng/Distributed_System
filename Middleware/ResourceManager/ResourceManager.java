package ResourceManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ResourceManager extends Remote{

    boolean commit(int var1) throws RemoteException;

    void abort(int var1) throws RemoteException;

    boolean shutdown() throws RemoteException;

    String getName() throws RemoteException;

}