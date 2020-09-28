package CarServer;

import Common.*;
import java.util.Vector;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.server.UnicastRemoteObject;

public class CarResourceManagerImp implements CarResourceManager{

    // Create a new car location or add cars to an existing location
    // NOTE: if price <= 0 and the location already exists, it maintains its current price
    protected String car_name = "Car_server/1018";
    protected RMHashMap car_data = new RMHashMap();

    public static void main(String[] args) {
        //default port
        int port = 1018;

        //take in a registry port
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length != 0) {
            System.err.println("U have not enter any port number");
            System.exit(1);
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry;
        try {
            // Initialize the CarResourceMangeerImp and its poxy_object
            CarResourceManagerImp obj = new CarResourceManagerImp();
            CarResourceManager proxyObj = (CarResourceManager) UnicastRemoteObject.exportObject(obj, 0);
            try{
                registry = LocateRegistry.createRegistry(port);
            }
            catch (RemoteException e)
            {
                System.out.println("Trying to connect to an external registry");
                registry = LocateRegistry.getRegistry(port);
            }
            // bind the proxyObject with the registry
            registry.rebind("car_server18", proxyObj);
            System.out.println("Car server is ready to work");
        } catch (Exception e) {
            System.err.println("Car Server exception: " + e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public boolean addCars(int xid, String location, int count, int price) throws RemoteException
    {
        Trace.info("RM::addCars(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
        Car curObj = (Car)readData(xid, Car.getKey(location));
        if (curObj == null)
        {
            // Car location doesn't exist yet, add it
            Car newObj = new Car(location, count, price);
            writeData(xid, newObj.getKey(), newObj);
            Trace.info("RM::addCars(" + xid + ") created new location " + location + ", count=" + count + ", price=$" + price);
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
            Trace.info("RM::addCars(" + xid + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
        }
        return true;
    }

    // Delete cars at a location
    @Override
    public boolean deleteCars(int xid, String location) throws RemoteException
    {
        return deleteItem(xid, Car.getKey(location));
    }

    // Returns the number of cars available at a location
    @Override
    public int queryCars(int xid, String location) throws RemoteException
    {
        return queryNum(xid, Car.getKey(location));
    }

    // Returns price of cars at this location
    @Override
    public int queryCarsPrice(int xid, String location) throws RemoteException
    {
        return queryPrice(xid, Car.getKey(location));
    }

    // Adds car reservation to this customer
    @Override
    public boolean reserveCar(int xid, int customerID, String location) throws RemoteException
    {
        Trace.info("Have not implemented");
        return false;
        //return reserveItem(xid, customerID, Car.getKey(location), location);
    }

    // Reserve bundle
    @Override
    public boolean bundle(int xid, int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException
    {
        return false;
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

    // Reserve an item
//    protected boolean reserveItem(int xid, int customerID, String key, String location)
//    {
//        Trace.info("RM::reserveItem(" + xid + ", customer=" + customerID + ", " + key + ", " + location + ") called" );
//        // Read customer object if it exists (and read lock it)
//        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
//        if (customer == null)
//        {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ")  failed--customer doesn't exist");
//            return false;
//        }
//
//        // Check if the item is available
//        ReservableItem item = (ReservableItem)readData(xid, key);
//        if (item == null)
//        {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") failed--item doesn't exist");
//            return false;
//        }
//        else if (item.getCount() == 0)
//        {
//            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") failed--No more items");
//            return false;
//        }
//        else
//        {
//            customer.reserve(key, location, item.getPrice());
//            writeData(xid, customer.getKey(), customer);
//
//            // Decrease the number of available items in the storage
//            item.setCount(item.getCount() - 1);
//            item.setReserved(item.getReserved() + 1);
//            writeData(xid, item.getKey(), item);
//
//            Trace.info("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") succeeded");
//            return true;
//        }
//    }
}