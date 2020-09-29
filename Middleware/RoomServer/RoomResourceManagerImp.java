package RoomServer;


import Common.RMHashMap;
import Common.RMItem;
import Common.ReservableItem;
import Common.Trace;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import java.rmi.RemoteException;

public class RoomResourceManagerImp implements RoomResourceManager {

    protected String room_name = "Room_server/1018";
    protected RMHashMap room_data = new RMHashMap();

    public static void main(String[] args) {
        //default port
        int port = 1018;

        //take in a registry port
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length != 0) {
            System.err.println("ou have enter any port for the server");
            System.exit(1);
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry;
        try {
            // Initialize the RoomResourceMangeerImp and its poxy_object
            RoomResourceManagerImp obj = new RoomResourceManagerImp();
            RoomResourceManager proxyObj = (RoomResourceManager) UnicastRemoteObject.exportObject(obj, 0);
            try{
                registry = LocateRegistry.createRegistry(port);
            }
            catch (RemoteException e)
            {
                System.out.println("Trying to connect to an external registry");
                registry = LocateRegistry.getRegistry(port);
            }
            // bind the proxyObject with the registry
            String registry_name = "room_server18";
            registry.rebind(registry_name, proxyObj);
            System.out.println("Room Server with name " + registry_name + " is ready at port " + port);
        } catch (Exception e) {
            System.err.println("Room Server exception: " + e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public boolean addRooms(int xid, String location, int count, int price) throws RemoteException
    {
        Trace.info("RM::addRooms(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
        Room curObj = (Room)readData(xid, Room.getKey(location));
        if (curObj == null)
        {
            // Room location doesn't exist yet, add it
            Room newObj = new Room(location, count, price);
            writeData(xid, newObj.getKey(), newObj);
            Trace.info("RM::addRooms(" + xid + ") created new room location " + location + ", count=" + count + ", price=$" + price);
        } else {
            // Add count to existing object and update price if greater than zero
            curObj.setCount(curObj.getCount() + count);
            if (price > 0)
            {
                curObj.setPrice(price);
            }
            writeData(xid, curObj.getKey(), curObj);
            Trace.info("RM::addRooms(" + xid + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
        }
        return true;
    }

    @Override
    public boolean deleteRooms(int xid, String location) throws RemoteException
    {
        return deleteItem(xid, Room.getKey(location));
    }

    @Override
    public int queryRooms(int xid, String location) throws RemoteException
    {
        return queryNum(xid, Room.getKey(location));
    }

    @Override
    // Returns room price at this location
    public int queryRoomsPrice(int xid, String location) throws RemoteException
    {
        return queryPrice(xid, Room.getKey(location));
    }

    @Override
    public boolean reserveRoom(int var1, int var2, String var3) throws RemoteException {
        Trace.info("Have not implemented");
        return false;
    }

    @Override
    public boolean bundle(int var1, int var2, Vector<String> var3, String var4, boolean var5, boolean var6) throws RemoteException {
        return false;
    }

    @Override
    public String getName() throws RemoteException
    {
        return room_name;
    }

    protected RMItem readData(int xid, String key)
    {
        synchronized(room_data) {
            RMItem item = room_data.get(key);
            if (item != null) {
                return (RMItem)item.clone();
            }
            return null;
        }
    }

    // Writes a data item
    protected void writeData(int xid, String key, RMItem value)
    {
        synchronized(room_data) {
            room_data.put(key, value);
        }
    }

    // Remove the item out of storage
    protected void removeData(int xid, String key)
    {
        synchronized(room_data) {
            room_data.remove(key);
        }
    }

    // Deletes the encar item
    protected boolean deleteItem(int xid, String key)
    {
        Trace.info("RM::deleteItem(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        // Check if there is such an item in the storage
        if (curObj == null)
        {
            Trace.warn("RM::deleteItem(" + xid + ", " + key + ") failed--item doesn't exist");
            return false;
        }
        else
        {
            if (curObj.getReserved() == 0)
            {
                removeData(xid, curObj.getKey());
                Trace.info("RM::deleteItem(" + xid + ", " + key + ") item deleted");
                return true;
            }
            else
            {
                Trace.info("RM::deleteItem(" + xid + ", " + key + ") item can't be deleted because some customers have reserved it");
                return false;
            }
        }
    }

    // Query the number of available seats/rooms/cars
    protected int queryNum(int xid, String key)
    {
        Trace.info("RM::queryNum(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        int value = 0;
        if (curObj != null)
        {
            value = curObj.getCount();
        }
        Trace.info("RM::queryNum(" + xid + ", " + key + ") returns count=" + value);
        return value;
    }

    // Query the price of an item
    protected int queryPrice(int xid, String key)
    {
        Trace.info("RM::queryPrice(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        int value = 0;
        if (curObj != null)
        {
            value = curObj.getPrice();
        }
        Trace.info("RM::queryPrice(" + xid + ", " + key + ") returns cost=$" + value);
        return value;
    }
}