package CarServer;

import Common.*;
import FlightServer.Flight;

import java.util.Vector;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.server.UnicastRemoteObject;

public class CarResourceManagerImp implements CarResourceManager{

    // Create a new car location or add cars to an existing location
    // NOTE: if price <= 0 and the location already exists, it maintains its current price
    protected String car_name = "Car_server/";
    protected RMHashMap car_data = new RMHashMap();

    public CarResourceManagerImp(int port) {
        this.car_name += port;
    }

    public static void main(String[] args) {
        //default port:1018

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
            // Initialize the CarResourceManagerImp and its poxy_object
            CarResourceManagerImp obj = new CarResourceManagerImp(port);
            CarResourceManager proxyObj = (CarResourceManager) UnicastRemoteObject.exportObject(obj, 0);
            try{
                registry = LocateRegistry.createRegistry(port);
            }
            catch (RemoteException e)
            {
                System.out.println("Trying to connect to an external registry at port:" + port);
                registry = LocateRegistry.getRegistry(port);
            }
            // bind the proxyObject with the registry
            String registry_name = "car_server18";
            registry.rebind(registry_name, proxyObj);
            System.out.println("CarServer with name " + registry_name + " is ready at port " + port);
        } catch (Exception e) {
            System.err.println("Car Server exception: " + e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public boolean addCars(int xid, String location, int count, int price) throws RemoteException
    {
        Trace.info("CarRM::addCars(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
        Car curObj = (Car)readData(xid, Car.getKey(location));
        if (curObj == null)
        {
            // Car location doesn't exist yet, add it
            Car newObj = new Car(location, count, price);
            writeData(xid, newObj.getKey(), newObj);
            Trace.info("CarRM::addCars(" + xid + ") created new location " + location + ", count=" + count + ", price=$" + price);
        }
        else
        {
            // Add count to existing car location and update price if greater than zero
            curObj.setCount(curObj.getCount() + count);
            if (price > 0)
            {
                curObj.setPrice(price);
            }
            writeData(xid, curObj.getKey(), curObj);
            Trace.info("CarRM::addCars(" + xid + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
        }
        return true;
    }

    // Delete cars at a location
    @Override
    public boolean deleteCars(int xid, String location) throws RemoteException
    {
        Trace.info("CarRM::deleteCars(" + xid + ") delete a car " + location);
        return deleteItem(xid, Car.getKey(location));
    }

    // Returns the number of cars available at a location
    @Override
    public int queryCars(int xid, String location) throws RemoteException
    {
        Trace.info("CarRM::queryCars(" + xid + ") query a car " + location);
        return queryNum(xid, Car.getKey(location));
    }

    // Returns price of cars at this location
    @Override
    public int queryCarsPrice(int xid, String location) throws RemoteException
    {
        Trace.info("CarRM::queryCarsPrice(" + xid + ") query a car price" + location);
        return queryPrice(xid, Car.getKey(location));
    }

    @Override
    public boolean reserve_check(int xid, String location) throws RemoteException {
        String key = Car.getKey(location);
        Car item = (Car)readData(xid, key);
        if (item == null)
        {
            Trace.warn("CarRM::reserveItem(" + xid + ", " + key + ", " + location + ") failed--item doesn't exist");
            return false;
        }
        else if (item.getCount() == 0) {
            Trace.warn("CarRM::reserveItem(" + xid + ", " + key + ", " + location + ") failed--No more items");
            return false;
        }
        return true;
    }

    // Adds car reservation to this customer
    @Override
    public boolean reserveCar(int xid, String location) throws RemoteException
    {
        String key = Car.getKey(location);
        Car item = (Car)readData(xid, key);
        if (item == null)
        {
            Trace.warn("CarRM::reserveItem(" + xid + ", " + key + ", " + location + ") failed--item doesn't exist");
            return false;
        }
        else if (item.getCount() == 0)
        {
            Trace.warn("CarRM::reserveItem(" + xid  + ", " + key + ", " + location + ") failed--No more items");
            return false;
        }else{
            // Decrease the number of available items in the storage
            item.setCount(item.getCount() - 1);
            item.setReserved(item.getReserved() + 1);
            writeData(xid, item.getKey(), item);
        }

        return true;
    }


    @Override
    public String getName() throws RemoteException
    {
        return car_name;
    }

    protected RMItem readData(int xid, String key)
    {
        synchronized(car_data) {
            RMItem item = car_data.get(key);
            if (item != null) {
                return (RMItem)item.clone();
            }
            return null;
        }
    }

    // Writes a data item
    protected void writeData(int xid, String key, RMItem value)
    {
        synchronized(car_data) {
            car_data.put(key, value);
        }
    }

    // Remove the item out of storage
    protected void removeData(int xid, String key)
    {
        synchronized(car_data) {
            car_data.remove(key);
        }
    }

    // Deletes the encar item
    protected boolean deleteItem(int xid, String key)
    {
        Trace.info("CarRM::deleteItem(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        // Check if there is such an item in the storage
        if (curObj == null)
        {
            Trace.warn("CarRM::deleteItem(" + xid + ", " + key + ") failed--item doesn't exist");
            return false;
        }
        else
        {
            if (curObj.getReserved() == 0)
            {
                removeData(xid, curObj.getKey());
                Trace.info("CarRM::deleteItem(" + xid + ", " + key + ") item deleted");
                return true;
            }
            else
            {
                Trace.info("CarRM::deleteItem(" + xid + ", " + key + ") item can't be deleted because some customers have reserved it");
                return false;
            }
        }
    }

    @Override
    public boolean reserve_cancel(int xid, int customerID, int count, String location) throws RemoteException {

        try {
            Trace.info("CarRM::reserve_cancel(" + xid + ", " + customerID + ") has reserved " + location + " " + count + " times");
            Car item = (Car) readData(xid, location);
            Trace.info("CarRM::reserve_cancel(" + xid + ", " + customerID + ") has reserved " + location + " which is reserved " + item.getReserved() + " times and is still available " + item.getCount() + " times");
            item.setReserved(item.getReserved() - count);
            item.setCount(item.getCount() + count);
            writeData(xid, item.getKey(), item);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            Trace.info("CarRM::reserve_cancel(" + xid + ", Error when process" + customerID + ") with reservations on " + location );
            return false;
        }
    }
    // Query the number of available seats/rooms/cars
    protected int queryNum(int xid, String key)
    {
        Trace.info("CarRM::queryNum(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        int value = 0;
        if (curObj != null)
        {
            value = curObj.getCount();
        }
        Trace.info("CarRM::queryNum(" + xid + ", " + key + ") returns count=" + value);
        return value;
    }

    // Query the price of an item
    protected int queryPrice(int xid, String key)
    {
        Trace.info("CarRM::queryPrice(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        int value = 0;
        if (curObj != null)
        {
            value = curObj.getPrice();
        }
        Trace.info("CarRM::queryPrice(" + xid + ", " + key + ") returns cost=$" + value);
        return value;
    }
}