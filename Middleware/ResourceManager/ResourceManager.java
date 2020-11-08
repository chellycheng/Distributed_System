package ResourceManager;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ResourceManager extends Remote{
    boolean commit() throws RemoteException;

    boolean abort() throws RemoteException;



}