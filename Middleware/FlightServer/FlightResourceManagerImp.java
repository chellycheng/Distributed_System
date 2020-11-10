
package FlightServer;

import Common.*;
import CustomerServer.Customer;
import ResourceManager.ReservationManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import java.rmi.RemoteException;

public class FlightResourceManagerImp extends ReservationManager<Flight> implements FlightResourceManager {


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
            //LOG
            enlist(xid, newObj.getKey(), null);
            writeData(xid, newObj.getKey(), newObj);
            Trace.info("FlightRM::addFlight(" + xid + ") created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
        }
        else
        {
            // Add seats to existing flight and update the price if greater than zero
            enlist(xid, curObj.getKey(), (RMItem) curObj.clone());
            curObj.setCount(curObj.getCount() + flightSeats);
            if (flightPrice > 0)
            {
                curObj.setPrice(flightPrice);
            }
            //LOG
            writeData(xid, curObj.getKey(), curObj);
            Trace.info("FlightRM::addFlight(" + xid + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice);
        }

        return true;
    }

    @Override
    public boolean deleteFlight(int xid, int flightNum) throws RemoteException
    {
        Trace.info("FlightRM::deleteFlights(" + xid + ") delete a flight " + flightNum);
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
            if (item == null)
            {
                Trace.warn("FlightRM::reserve_cancel(" + xid +  ", " + key + ") failed--item doesn't exist");
                return false;
            }
            //LOG
            enlist(xid, key, (RMItem) item.clone());
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
            enlist(xid, key, (RMItem) item.clone());
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

    @Override
    public boolean shutdown() throws RemoteException {
        return selfDestroy(0);
    }



}