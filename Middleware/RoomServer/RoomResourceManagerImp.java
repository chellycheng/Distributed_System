package RoomServer;


import Common.RMHashMap;
import Common.RMItem;
import Common.ReservableItem;
import Common.Trace;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import ResourceManager.ReservationManager;
import java.rmi.RemoteException;

public class RoomResourceManagerImp extends ReservationManager<Room> implements RoomResourceManager {

    protected String room_name = "Room_server";
    protected RMHashMap room_data = new RMHashMap();

    public static void main(String[] args) {
        //default port
        int port = 1018;

        //take in a registry port
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry;
        try {
            // Initialize the Room_Resource_Manager_Implementation and its poxy_object
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
        Trace.info("RoomRM::addRooms(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
        Room curObj = (Room)readData(xid, Room.getKey(location));
        if (curObj == null)
        {
            // Room location doesn't exist yet, add it
            Room newObj = new Room(location, count, price);
            enlist(xid, newObj.getKey(), null);
            writeData(xid, newObj.getKey(), newObj);
            Trace.info("RoomRM::addRooms(" + xid + ") created new room location " + location + ", count=" + count + ", price=$" + price);
        } else {
            // Add count to existing object and update price if greater than zero
            enlist(xid, curObj.getKey(), (RMItem) curObj.clone());
            curObj.setCount(curObj.getCount() + count);
            if (price > 0)
            {
                curObj.setPrice(price);
            }
            writeData(xid, curObj.getKey(), curObj);
            Trace.info("RoomRM::addRooms(" + xid + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
        }
        return true;
    }

    @Override
    public boolean deleteRooms(int xid, String location) throws RemoteException
    {
        Trace.info("RoomRM::deleteRooms(" + xid + ") delete a room location " + location);
        return deleteItem(xid, Room.getKey(location));
    }

    @Override
    public int queryRooms(int xid, String location) throws RemoteException
    {
        Trace.info("RoomRM::queryRooms(" + xid + ") query room at location " + location);
        return queryNum(xid, Room.getKey(location));
    }

    @Override
    // Returns room price at this location
    public int queryRoomsPrice(int xid, String location) throws RemoteException
    {
        Trace.info("RoomRM::queryRoomsPrice(" + xid + ")  query room price at location " + location);
        return queryPrice(xid, Room.getKey(location));
    }

    @Override
    public boolean reserve_check(int xid, String location) throws RemoteException {
        String key = Room.getKey(location);
        Room item = (Room)readData(xid, key);
        if (item == null)
        {
            Trace.warn("RoomRM::reserve_check(" + xid + ", " + key + ", " + location + ") failed--item doesn't exist");
            return false;
        }
        else if (item.getCount() == 0) {
            Trace.warn("RoomRM::reserve_check(" + xid + ", " + key + ", " + location + ") failed--No more items");
            return false;
        }
        return true;
    }

    @Override
    public boolean reserve_cancel(int xid, int customerID, int count, String location) throws RemoteException {

        try {
            Trace.info("RoomRM::reserve_cancel(" + xid + ", " + customerID + ") has reserved " + location + " " + count + " times");
            Room item = (Room) readData(xid, location);
            //LOG
            enlist(xid, location, (RMItem) item.clone());
            Trace.info("RoomRM::reserve_cancel(" + xid + ", " + customerID + ") has reserved " + location + " which is reserved " + item.getReserved() + " times and is still available " + item.getCount() + " times");
            item.setReserved(item.getReserved() - count);
            item.setCount(item.getCount() + count);
            writeData(xid, item.getKey(), item);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            Trace.info("RoomRM::reserve_cancel(" + xid + ", Error when process" + customerID + ") with reservations on " + location );
            return false;
        }
    }
    @Override
    public boolean reserveRoom(int xid, String location) throws RemoteException {

        String key = Room.getKey(location);
        Room item = (Room) readData(xid, key);
        // Decrease the number of available items in the storage
        if (item == null)
        {
            Trace.warn("RoomRM::reserveItem(" + xid + ", " + key + ", " + location + ") failed--item doesn't exist");
            return false;
        }
        else if (item.getCount() == 0)
        {
            Trace.warn("RoomRM::reserveItem(" + xid  + ", " + key + ", " + location + ") failed--No more items");
            return false;
        }else {
            // Decrease the number of available items in the storage
            enlist(xid, key, (RMItem) item.clone());
            Trace.warn("RoomRM::reserveRoom(Current Count: " + item.getCount());
            item.setCount(item.getCount() - 1);
            item.setReserved(item.getReserved() + 1);
            writeData(xid, item.getKey(), item);
            Trace.warn("RoomRM::reserveRoom(Update Count: " + item.getCount());
        }
        return true;

    }

    @Override
    public String getName() throws RemoteException
    {
        return room_name;
    }

    @Override
    public boolean shutdown() throws RemoteException {
        return selfDestroy(0);
    }
}