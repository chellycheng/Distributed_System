
package FlightServer;

import Common.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import java.rmi.RemoteException;

public class FlightResourceManagerImp implements FlightResourceManager {


    protected String flight_name = "Flight_server";
    protected RMHashMap flight_data = new RMHashMap();

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
            // Initialize the FlightResourceMangeerImp and its poxy_object
            FlightResourceManagerImp obj = new FlightResourceManagerImp();
            FlightResourceManager proxyObj = (FlightResourceManager) UnicastRemoteObject.exportObject(obj, 0);
            try{
                registry = LocateRegistry.createRegistry(port);
            }
            catch (RemoteException e)
            {
                System.out.println("Trying to connect to an external registry at port:" + port);
                registry = LocateRegistry.getRegistry(port);
            }
            // bind the proxyObject with the registry
            String registry_name = "flight_server18";
            registry.rebind(registry_name, proxyObj);
            System.out.println("FlightServer with name " + registry_name + " is ready at port " + port +" ");
        } catch (Exception e) {
            System.err.println("Flight Server exception: " + e.toString());
            e.printStackTrace();
        }

    }
    // Create a new flight, or add seats to existing flight
    // NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
    @Override
    public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException
    {
        Trace.info("FlightRM::addFlight(" + xid + ", " + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
        Flight curObj = (Flight)readData(xid, Flight.getKey(flightNum));
        if (curObj == null)
        {
            // Doesn't exist yet, add it
            Flight newObj = new Flight(flightNum, flightSeats, flightPrice);
            writeData(xid, newObj.getKey(), newObj);
            Trace.info("FlightRM::addFlight(" + xid + ") created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
        }
        else
        {
            // Add seats to existing flight and update the price if greater than zero
            curObj.setCount(curObj.getCount() + flightSeats);
            if (flightPrice > 0)
            {
                curObj.setPrice(flightPrice);
            }
            writeData(xid, curObj.getKey(), curObj);
            Trace.info("FlightRM::addFlight(" + xid + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice);
        }
        return true;
    }

    @Override
    public boolean deleteFlight(int xid, int flightNum) throws RemoteException
    {
        Trace.info("FlightRM::deleteFlgihts(" + xid + ") delete a flight " + flightNum);
        return deleteItem(xid, Flight.getKey(flightNum));
    }

    @Override
    public int queryFlight(int xid, int flightNum) throws RemoteException
    {
        Trace.info("FlightRM::queryFlight(" + xid + ") query a flight " + flightNum);
        return queryNum(xid, Flight.getKey(flightNum));
    }

    @Override
    public int queryFlightPrice(int xid, int flightNum) throws RemoteException
    {
        Trace.info("FlightRM::queryFlightPrice(" + xid + ") query a flight price " + flightNum);
        return queryPrice(xid, Flight.getKey(flightNum));
    }

    @Override
    public boolean reserve_check(int xid, int flightNum) throws RemoteException {
        String key = Flight.getKey(flightNum);
        Flight item = (Flight)readData(xid, key);
        if (item == null)
        {
            Trace.warn("FlightRM::reserve_check(" + xid + ", " + key + ", " + flightNum + ") failed--item doesn't exist");
            return false;
        }
        else if (item.getCount() == 0) {
            Trace.warn("FlightRM::reserve_check(" + xid + ", " + key + ", " + flightNum + ") failed--No more items");
            return false;
        }
        return true;
    }

    @Override
    public boolean reserve_cancel(int xid, int customerID, int count, String key) throws RemoteException {

        try {
            Trace.info("FlightRM::reserve_cancel(" + xid + ", " + customerID + ") has reserved " + key + " " + count + " times");
            Flight item = (Flight) readData(xid, key);
            Trace.info("TEST-location::"+key);
            if (item == null)
            {
                Trace.warn("FlightRM::reserve_cancel(" + xid +  ", " + key + ") failed--item doesn't exist");
                return false;
            }
            Trace.info("FlightRM::reserve_cancel(" + xid + ", " + customerID + ") has reserved " + key + " which is reserved " + item.getReserved() + " times and is still available " + item.getCount() + " times");
            item.setReserved(item.getReserved() - count);
            item.setCount(item.getCount() + count);
            writeData(xid, item.getKey(), item);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            Trace.info("FlightRM::reserve_cancel(" + xid + ", Error when process" + customerID + ") with reservations on " + key );
            return false;
        }
    }

    @Override
    public boolean reserveFlight(int xid, int flightNum) throws RemoteException {
        String key = Flight.getKey(flightNum);
        String location = String.valueOf(flightNum);
        Flight item = (Flight)readData(xid, key);
        if (item == null)
        {
            Trace.warn("FlightRM::reserveItem(" + xid + ", " + key + ", " + location + ") failed--item doesn't exist");
            return false;
        }
        else if (item.getCount() == 0)
        {
            Trace.warn("FlightRM::reserveItem(" + xid  + ", " + key + ", " + location + ") failed--No more items");
            return false;
        }else{
            // Decrease the number of available items in the storage
            Trace.warn("FlightRM::reserveFlight(Current Count: " + item.getCount());
            item.setCount(item.getCount() - 1);
            item.setReserved(item.getReserved() + 1);
            writeData(xid, item.getKey(), item);
            Trace.warn("FlightRM::reserveFlight(Update Count: " + item.getCount());
        }

        return true;
    }

    @Override
    public String getName() throws RemoteException
    {
        return flight_name;
    }


    protected RMItem readData(int xid, String key)
    {
        synchronized(flight_data) {
            RMItem item = flight_data.get(key);
            if (item != null) {
                return (RMItem)item.clone();
            }
            return null;
        }
    }

    // Writes a data item
    protected void writeData(int xid, String key, RMItem value)
    {
        synchronized(flight_data) {
            flight_data.put(key, value);
        }
    }

    // Remove the item out of storage
    protected void removeData(int xid, String key)
    {
        synchronized(flight_data) {
            flight_data.remove(key);
        }
    }

    // Deletes the flight item
    protected boolean deleteItem(int xid, String key)
    {
        Trace.info("FlightRM::deleteItem(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        // Check if there is such an item in the storage
        if (curObj == null)
        {
            Trace.warn("FlightRM::deleteItem(" + xid + ", " + key + ") failed--item doesn't exist");
            return false;
        }
        else
        {
            if (curObj.getReserved() == 0)
            {
                removeData(xid, curObj.getKey());
                Trace.info("FlightRM::deleteItem(" + xid + ", " + key + ") item deleted");
                return true;
            }
            else
            {
                Trace.info("FlightRM::deleteItem(" + xid + ", " + key + ") item can't be deleted because some customers have reserved it");
                return false;
            }
        }
    }

    // Query the number of available seats/rooms/cars
    protected int queryNum(int xid, String key)
    {
        Trace.info("FlightRM::queryNum(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        int value = 0;
        if (curObj != null)
        {
            value = curObj.getCount();
        }
        Trace.info("FlightRM::queryNum(" + xid + ", " + key + ") returns count=" + value);
        return value;
    }

    // Query the price of an item
    protected int queryPrice(int xid, String key)
    {
        Trace.info("FlightRM::queryPrice(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        int value = 0;
        if (curObj != null)
        {
            value = curObj.getPrice();
        }
        Trace.info("FlightRM::queryPrice(" + xid + ", " + key + ") returns cost=$" + value);
        return value;
    }

    @Override
    public boolean commit(int xid) throws RemoteException {
        return false;
    }

    @Override
    public void abort(int xid) throws RemoteException {

    }

    @Override
    public boolean shutdown() throws RemoteException {
        System.exit(0);
        return true;
    }
}