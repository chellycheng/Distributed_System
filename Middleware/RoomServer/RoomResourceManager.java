package RoomServer;

import java.rmi.RemoteException;
import ResourceManager.*;
import java.util.Vector;

public interface RoomResourceManager extends ResourceManager{

    boolean addRooms(int var1, String var2, int var3, int var4) throws RemoteException;

    boolean deleteRooms(int var1, String var2) throws RemoteException;

    int queryRooms(int var1, String var2) throws RemoteException;

    int queryRoomsPrice(int var1, String var2) throws RemoteException;

    boolean reserveRoom(int var1, int var2, String var3) throws RemoteException;

    boolean bundle(int var1, int var2, Vector<String> var3, String var4, boolean var5, boolean var6) throws RemoteException;

    String getName() throws RemoteException;
}