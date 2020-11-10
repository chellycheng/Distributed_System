package CustomerServer;

import Common.*;
import ResourceManager.ReservationManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;

import java.rmi.RemoteException;

public class CustomerResourceManagerImp extends ReservationManager<Customer> implements CustomerResourceManager {
    protected String customer_name = "Customer_server";

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
            // Initialize the CustomerResourceManagerImp and its poxy_object
            CustomerResourceManagerImp obj = new CustomerResourceManagerImp();
            CustomerResourceManager proxyObj = (CustomerResourceManager) UnicastRemoteObject.exportObject(obj, 0);
            try{
                registry = LocateRegistry.createRegistry(port);
            }
            catch (RemoteException e)
            {
                System.out.println("Trying to connect to an external registry at port:\" + port");
                registry = LocateRegistry.getRegistry(port);
            }
            String registry_name = "customer_server18";
            // bind the proxyObject with the registry
            registry.rebind(registry_name, proxyObj);
            System.out.println("CustomerServer with name " + registry_name + " is ready at port " + port);
        } catch (Exception e) {
            System.err.println("Customer Server exception: " + e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public int newCustomer(int xid) throws RemoteException
    {
        Trace.info("CustomerRM::newCustomer(" + xid + ") called");
        // Generate a globally unique ID for the new customer
        int cid = Integer.parseInt(String.valueOf(xid) +
                String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
                String.valueOf(Math.round(Math.random() * 100 + 1)));
        Customer customer = new Customer(cid);
        writeData(xid, customer.getKey(), customer);
        //LOG
        enlist(xid, customer.getKey(), null);
        Trace.info("CustomerRM::newCustomer(" + cid + ") returns ID=" + cid);
        return cid;
    }

    @Override
    public boolean newCustomer(int xid, int customerID) throws RemoteException
    {
        Trace.info("CustomerRM::newCustomer(" + xid + ", " + customerID + ") called");
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            customer = new Customer(customerID);
            writeData(xid, customer.getKey(), customer);
            //LOG
            enlist(xid, customer.getKey(), null);
            Trace.info("CustomerRM::newCustomer(" + xid + ", " + customerID + ") created a new customer");
            return true;
        }
        else
        {
            Trace.info("INFO: RM::newCustomer(" + xid + ", " + customerID + ") failed--customer already exists");
            return false;
        }
    }

    @Override
    public boolean delete_check(int xid, int customerID) throws RemoteException
    {
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            Trace.warn("CustomerRM::deleteCustomer(" + xid + ", " + customerID + ") failed--customer doesn't exist");
            return false;
        }
        return true;

    }

    @Override
    public boolean deleteCustomer(int xid, int customerID) throws RemoteException
    {
        Trace.info("CustomerRM::deleteCustomer(" + xid + ", " + customerID + ") called");
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            Trace.warn("CustomerRM::deleteCustomer(" + xid + ", " + customerID + ") failed--customer doesn't exist");
            return false;
        }
        else
        {
            // Remove the customer from the storage
            //LOG
            enlist(xid,customer.getKey(), (RMItem) customer.clone());
            removeData(xid, customer.getKey());
            Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") succeeded");
            return true;
        }
    }

    @Override
    public String queryCustomerInfo(int xid, int customerID) throws RemoteException
    {
        Trace.info("RM::queryCustomerInfo(" + xid + ", " + customerID + ") called");
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            Trace.warn("RM::queryCustomerInfo(" + xid + ", " + customerID + ") failed--customer doesn't exist");
            // NOTE: don't change this--WC counts on this value indicating a customer does not exist...
            return "";
        }
        else
        {
            Trace.info("RM::queryCustomerInfo(" + xid + ", " + customerID + ")");
            System.out.println(customer.getBill());
            return customer.getBill();
        }
    }

    @Override
    public boolean reserveFlight(int xid, int customerID, String flightkey, String value, int price) throws RemoteException
    {
        return reserveItem(xid, customerID, flightkey, value, price);
    }

    @Override
    public boolean reserveCar(int xid, int customerID, String carkey, String location, int price) throws RemoteException
    {
        return reserveItem(xid, customerID, carkey, location, price);
    }

    @Override
    public boolean reserveRoom(int xid, int customerID, String roomkey, String location, int price) throws RemoteException
    {
        return reserveItem(xid, customerID, roomkey, location, price);
    }

    @Override
    public boolean reserve_item(int xid, int customerID) throws RemoteException {
        Trace.info("RM::reserveItem(" + xid + ", customer=" + customerID +  ") called" );
        // Read customer object if it exists (and read lock it)
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + " failed--customer doesn't exist");
            return false;
        }
        return true;
    }

    @Override
    public String getName() throws RemoteException {
        return customer_name;
    }

    // Reserve an item
    protected boolean reserveItem(int xid, int customerID, String key, String location, int price)
    {
        Trace.info("RM::reserveItem(" + xid + ", customer=" + customerID + ", " + key + ", " + location + ") called" );
        // Read customer object if it exists (and read lock it)
        Customer customer = (Customer)readData(xid, Customer.getKey(customerID));
        if (customer == null)
        {
            Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ")  failed--customer doesn't exist");
            return false;
        }
        enlist(xid,customer.getKey(),(RMItem) customer.clone());
        customer.reserve(key, location, price);
        writeData(xid, customer.getKey(), customer);
        Trace.info("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") succeeded");
        return true;

    }

    @Override
    public boolean shutdown() throws RemoteException {
        return selfDestroy(0);
    }
}
