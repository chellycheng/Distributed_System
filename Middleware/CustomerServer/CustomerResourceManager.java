package CustomerServer;
import java.rmi.Remote;
import java.rmi.RemoteException;
import ResourceManager.*;
import java.util.Vector;

public interface CustomerResourceManager extends ResourceManager {

    /**
     * Add customer.
     *
     * @return Unique customer identifier
     */
    int newCustomer(int id) throws RemoteException;

    /**
     * Add customer with id.
     *
     * @return Success
     */
    boolean newCustomer(int id, int cid) throws RemoteException;

    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    boolean deleteCustomer(int id, int customerID) throws RemoteException;

    /**
     * Check Delete a customer is possible.
     *
     * @return Success
     */
    boolean delete_check(int id, int customerID) throws RemoteException;

    /**
     * Query the customer reservations.
     *
     * @return A formatted bill for the customer
     */
    String queryCustomerInfo(int id, int customerID)
            throws RemoteException;

    /**
     * Reserve a seat on this flight.
     *
     * @return Success
     */
    boolean reserveFlight(int id, int customerID, String key, String location, int price)
            throws RemoteException;

    boolean reserveCar(int id, int customerID, String key, String location, int price)
            throws RemoteException;

    boolean reserveRoom(int id, int customerID, String key, String location, int price)
            throws RemoteException;

    boolean reserve_item(int id, int customerID) throws RemoteException;

    String getName() throws RemoteException;
}