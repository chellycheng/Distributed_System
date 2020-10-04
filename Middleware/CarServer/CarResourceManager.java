package CarServer;
import java.rmi.RemoteException;
import ResourceManager.*;
import java.util.Vector;

public interface CarResourceManager extends ResourceManager{

    boolean addCars(int var1, String var2, int var3, int var4) throws RemoteException;

    boolean deleteCars(int var1, String var2) throws RemoteException;

    int queryCars(int var1, String var2) throws RemoteException;

    int queryCarsPrice(int var1, String var2) throws RemoteException;

    boolean reserve_check(int var1, String var2) throws RemoteException;

    boolean reserve_cancel(int var1, int var2, int var3, String var4) throws RemoteException;

    boolean reserveCar(int var1, String var2) throws RemoteException;

    String getName() throws RemoteException;
}