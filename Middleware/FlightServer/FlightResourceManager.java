package FlightServer;
import java.rmi.RemoteException;
import ResourceManager.*;
import java.util.Vector;

public interface FlightResourceManager extends ResourceManager{
    boolean addFlight(int var1, int var2, int var3, int var4) throws RemoteException;

    boolean deleteFlight(int var1, int var2) throws RemoteException;

    int queryFlight(int var1, int var2) throws RemoteException;

    int queryFlightPrice(int var1, int var2) throws RemoteException;

    boolean reserve_check(int var1, int var2) throws RemoteException;

    boolean reserve_cancel(int var1, int var2, int var3, String var4) throws RemoteException;

    boolean reserveFlight(int var1, int var2) throws RemoteException;

    String getName() throws RemoteException;
}