package CarServer;

import Common.*;
import FlightServer.Flight;
import ResourceManager.ReservationManager;

import java.util.Vector;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.server.UnicastRemoteObject;

public class CarResourceManagerImp extends ReservationManager<Car>  implements CarResourceManager{

    // Create a new car location or add cars to an existing location
    // NOTE: if price <= 0 and the location already exists, it maintains its current price
    protected String car_name = "Car_server";
    protected RMHashMap car_data = new RMHashMap();

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
            CarResourceManagerImp obj = new CarResourceManagerImp();
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
            //LOG
            enlist(xid, newObj.getKey(), null);
            writeData(xid, newObj.getKey(), newObj);
            Trace.info("CarRM::addCars(" + xid + ") created new location " + location + ", count=" + count + ", price=$" + price);
        }
        else
        {
            // Add count to existing car location and update price if greater than zero
            enlist(xid, curObj.getKey(), (RMItem) curObj.clone());
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
            Trace.warn("CarRM::reserveCar(" + xid + ", " + key + ", " + location + ") failed--item doesn't exist");
            return false;
        }
        else if (item.getCount() == 0)
        {
            Trace.warn("CarRM::reserveCar(" + xid  + ", " + key + ", " + location + ") failed--No more items");
            return false;
        }else{
            // Decrease the number of available items in the storage
            enlist(xid, key, (RMItem) item.clone());
            Trace.warn("CarRM::reserveCar(Current Count: " + item.getCount());
            item.setCount(item.getCount() - 1);
            item.setReserved(item.getReserved() + 1);
            writeData(xid, item.getKey(), item);
            Trace.warn("CarRM::reserveCar(Update Count: " + item.getCount());
        }

        return true;
    }


    @Override
    public String getName() throws RemoteException
    {
        return car_name;
    }

    @Override
    public boolean reserve_cancel(int xid, int customerID, int count, String location) throws RemoteException {

        try {
            Trace.info("CarRM::reserve_cancel(" + xid + ", " + customerID + ") has reserved " + location + " " + count + " times");
            Car item = (Car) readData(xid, location);
            //LOG
            enlist(xid, location, (RMItem) item.clone());
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

    @Override
    public boolean shutdown() throws RemoteException {
        return selfDestroy(0);
    }
}