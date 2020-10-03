// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package TCPServer.Common;

import TCPServer.Interface.IResourceManager;

import java.util.*;
import java.rmi.RemoteException;

public class ResourceManager implements IResourceManager
{
	protected String m_name = "";
	protected TCPServer.Common.RMHashMap m_data = new TCPServer.Common.RMHashMap();

	public ResourceManager(String p_name)
	{
		m_name = p_name;
	}

	// Reads a data item
	protected TCPServer.Common.RMItem readData(int xid, String key)
	{
		synchronized(m_data) {
			TCPServer.Common.RMItem item = m_data.get(key);
			if (item != null) {
				return (TCPServer.Common.RMItem)item.clone();
			}
			return null;
		}
	}

	// Writes a data item
	protected void writeData(int xid, String key, RMItem value)
	{
		synchronized(m_data) {
			m_data.put(key, value);
		}
	}

	// Remove the item out of storage
	protected void removeData(int xid, String key)
	{
		synchronized(m_data) {
			m_data.remove(key);
		}
	}

	// Deletes the encar item
	protected boolean deleteItem(int xid, String key)
	{
		TCPServer.Common.Trace.info("RM::deleteItem(" + xid + ", " + key + ") called");
		TCPServer.Common.ReservableItem curObj = (TCPServer.Common.ReservableItem)readData(xid, key);
		// Check if there is such an item in the storage
		if (curObj == null)
		{
			TCPServer.Common.Trace.warn("RM::deleteItem(" + xid + ", " + key + ") failed--item doesn't exist");
			return false;
		}
		else
		{
			if (curObj.getReserved() == 0)
			{
				removeData(xid, curObj.getKey());
				TCPServer.Common.Trace.info("RM::deleteItem(" + xid + ", " + key + ") item deleted");
				return true;
			}
			else
			{
				TCPServer.Common.Trace.info("RM::deleteItem(" + xid + ", " + key + ") item can't be deleted because some customers have reserved it");
				return false;
			}
		}
	}

	// Query the number of available seats/rooms/cars
	protected int queryNum(int xid, String key)
	{
		TCPServer.Common.Trace.info("RM::queryNum(" + xid + ", " + key + ") called");
		TCPServer.Common.ReservableItem curObj = (TCPServer.Common.ReservableItem)readData(xid, key);
		int value = 0;  
		if (curObj != null)
		{
			value = curObj.getCount();
		}
		TCPServer.Common.Trace.info("RM::queryNum(" + xid + ", " + key + ") returns count=" + value);
		return value;
	}    

	// Query the price of an item
	protected int queryPrice(int xid, String key)
	{
		TCPServer.Common.Trace.info("RM::queryPrice(" + xid + ", " + key + ") called");
		TCPServer.Common.ReservableItem curObj = (TCPServer.Common.ReservableItem)readData(xid, key);
		int value = 0; 
		if (curObj != null)
		{
			value = curObj.getPrice();
		}
		TCPServer.Common.Trace.info("RM::queryPrice(" + xid + ", " + key + ") returns cost=$" + value);
		return value;        
	}

	// Reserve an item
	protected boolean reserveItem(int xid, int customerID, String key, String location)
	{
		TCPServer.Common.Trace.info("RM::reserveItem(" + xid + ", customer=" + customerID + ", " + key + ", " + location + ") called" );
		// Read customer object if it exists (and read lock it)
		TCPServer.Common.Customer customer = (TCPServer.Common.Customer)readData(xid, TCPServer.Common.Customer.getKey(customerID));
		if (customer == null)
		{
			TCPServer.Common.Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ")  failed--customer doesn't exist");
			return false;
		} 

		// Check if the item is available
		TCPServer.Common.ReservableItem item = (TCPServer.Common.ReservableItem)readData(xid, key);
		if (item == null)
		{
			TCPServer.Common.Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") failed--item doesn't exist");
			return false;
		}
		else if (item.getCount() == 0)
		{
			TCPServer.Common.Trace.warn("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") failed--No more items");
			return false;
		}
		else
		{            
			customer.reserve(key, location, item.getPrice());        
			writeData(xid, customer.getKey(), customer);

			// Decrease the number of available items in the storage
			item.setCount(item.getCount() - 1);
			item.setReserved(item.getReserved() + 1);
			writeData(xid, item.getKey(), item);

			TCPServer.Common.Trace.info("RM::reserveItem(" + xid + ", " + customerID + ", " + key + ", " + location + ") succeeded");
			return true;
		}        
	}

	// Create a new flight, or add seats to existing flight
	// NOTE: if flightPrice <= 0 and the flight already exists, it maintains its current price
	public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException
	{
		TCPServer.Common.Trace.info("RM::addFlight(" + xid + ", " + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
		TCPServer.Common.Flight curObj = (TCPServer.Common.Flight)readData(xid, TCPServer.Common.Flight.getKey(flightNum));
		if (curObj == null)
		{
			// Doesn't exist yet, add it
			TCPServer.Common.Flight newObj = new TCPServer.Common.Flight(flightNum, flightSeats, flightPrice);
			writeData(xid, newObj.getKey(), newObj);
			TCPServer.Common.Trace.info("RM::addFlight(" + xid + ") created new flight " + flightNum + ", seats=" + flightSeats + ", price=$" + flightPrice);
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
			TCPServer.Common.Trace.info("RM::addFlight(" + xid + ") modified existing flight " + flightNum + ", seats=" + curObj.getCount() + ", price=$" + flightPrice);
		}
		return true;
	}

	// Create a new car location or add cars to an existing location
	// NOTE: if price <= 0 and the location already exists, it maintains its current price
	public boolean addCars(int xid, String location, int count, int price) throws RemoteException
	{
		TCPServer.Common.Trace.info("RM::addCars(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
		TCPServer.Common.Car curObj = (TCPServer.Common.Car)readData(xid, TCPServer.Common.Car.getKey(location));
		if (curObj == null)
		{
			// Car location doesn't exist yet, add it
			TCPServer.Common.Car newObj = new TCPServer.Common.Car(location, count, price);
			writeData(xid, newObj.getKey(), newObj);
			TCPServer.Common.Trace.info("RM::addCars(" + xid + ") created new location " + location + ", count=" + count + ", price=$" + price);
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
			TCPServer.Common.Trace.info("RM::addCars(" + xid + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
		}
		return true;
	}

	// Create a new room location or add rooms to an existing location
	// NOTE: if price <= 0 and the room location already exists, it maintains its current price
	public boolean addRooms(int xid, String location, int count, int price) throws RemoteException
	{
		TCPServer.Common.Trace.info("RM::addRooms(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
		TCPServer.Common.Room curObj = (TCPServer.Common.Room)readData(xid, TCPServer.Common.Room.getKey(location));
		if (curObj == null)
		{
			// Room location doesn't exist yet, add it
			TCPServer.Common.Room newObj = new TCPServer.Common.Room(location, count, price);
			writeData(xid, newObj.getKey(), newObj);
			TCPServer.Common.Trace.info("RM::addRooms(" + xid + ") created new room location " + location + ", count=" + count + ", price=$" + price);
		} else {
			// Add count to existing object and update price if greater than zero
			curObj.setCount(curObj.getCount() + count);
			if (price > 0)
			{
				curObj.setPrice(price);
			}
			writeData(xid, curObj.getKey(), curObj);
			TCPServer.Common.Trace.info("RM::addRooms(" + xid + ") modified existing location " + location + ", count=" + curObj.getCount() + ", price=$" + price);
		}
		return true;
	}

	// Deletes flight
	public boolean deleteFlight(int xid, int flightNum) throws RemoteException
	{
		return deleteItem(xid, TCPServer.Common.Flight.getKey(flightNum));
	}

	// Delete cars at a location
	public boolean deleteCars(int xid, String location) throws RemoteException
	{
		return deleteItem(xid, TCPServer.Common.Car.getKey(location));
	}

	// Delete rooms at a location
	public boolean deleteRooms(int xid, String location) throws RemoteException
	{
		return deleteItem(xid, TCPServer.Common.Room.getKey(location));
	}

	// Returns the number of empty seats in this flight
	public int queryFlight(int xid, int flightNum) throws RemoteException
	{
		return queryNum(xid, TCPServer.Common.Flight.getKey(flightNum));
	}

	// Returns the number of cars available at a location
	public int queryCars(int xid, String location) throws RemoteException
	{
		return queryNum(xid, TCPServer.Common.Car.getKey(location));
	}

	// Returns the amount of rooms available at a location
	public int queryRooms(int xid, String location) throws RemoteException
	{
		return queryNum(xid, TCPServer.Common.Room.getKey(location));
	}

	// Returns price of a seat in this flight
	public int queryFlightPrice(int xid, int flightNum) throws RemoteException
	{
		return queryPrice(xid, TCPServer.Common.Flight.getKey(flightNum));
	}

	// Returns price of cars at this location
	public int queryCarsPrice(int xid, String location) throws RemoteException
	{
		return queryPrice(xid, TCPServer.Common.Car.getKey(location));
	}

	// Returns room price at this location
	public int queryRoomsPrice(int xid, String location) throws RemoteException
	{
		return queryPrice(xid, TCPServer.Common.Room.getKey(location));
	}

	public String queryCustomerInfo(int xid, int customerID) throws RemoteException
	{
		TCPServer.Common.Trace.info("RM::queryCustomerInfo(" + xid + ", " + customerID + ") called");
		TCPServer.Common.Customer customer = (TCPServer.Common.Customer)readData(xid, TCPServer.Common.Customer.getKey(customerID));
		if (customer == null)
		{
			TCPServer.Common.Trace.warn("RM::queryCustomerInfo(" + xid + ", " + customerID + ") failed--customer doesn't exist");
			// NOTE: don't change this--WC counts on this value indicating a customer does not exist...
			return "";
		}
		else
		{
			TCPServer.Common.Trace.info("RM::queryCustomerInfo(" + xid + ", " + customerID + ")");
			System.out.println(customer.getBill());
			return customer.getBill();
		}
	}

	public int newCustomer(int xid) throws RemoteException
	{
        	TCPServer.Common.Trace.info("RM::newCustomer(" + xid + ") called");
		// Generate a globally unique ID for the new customer
		int cid = Integer.parseInt(String.valueOf(xid) +
			String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND)) +
			String.valueOf(Math.round(Math.random() * 100 + 1)));
		TCPServer.Common.Customer customer = new TCPServer.Common.Customer(cid);
		writeData(xid, customer.getKey(), customer);
		TCPServer.Common.Trace.info("RM::newCustomer(" + cid + ") returns ID=" + cid);
		return cid;
	}

	public boolean newCustomer(int xid, int customerID) throws RemoteException
	{
		TCPServer.Common.Trace.info("RM::newCustomer(" + xid + ", " + customerID + ") called");
		TCPServer.Common.Customer customer = (TCPServer.Common.Customer)readData(xid, TCPServer.Common.Customer.getKey(customerID));
		if (customer == null)
		{
			customer = new TCPServer.Common.Customer(customerID);
			writeData(xid, customer.getKey(), customer);
			TCPServer.Common.Trace.info("RM::newCustomer(" + xid + ", " + customerID + ") created a new customer");
			return true;
		}
		else
		{
			TCPServer.Common.Trace.info("INFO: RM::newCustomer(" + xid + ", " + customerID + ") failed--customer already exists");
			return false;
		}
	}

	public boolean deleteCustomer(int xid, int customerID) throws RemoteException
	{
		TCPServer.Common.Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") called");
		TCPServer.Common.Customer customer = (TCPServer.Common.Customer)readData(xid, Customer.getKey(customerID));
		if (customer == null)
		{
			TCPServer.Common.Trace.warn("RM::deleteCustomer(" + xid + ", " + customerID + ") failed--customer doesn't exist");
			return false;
		}
		else
		{            
			// Increase the reserved numbers of all reservable items which the customer reserved. 
 			RMHashMap reservations = customer.getReservations();
			for (String reservedKey : reservations.keySet())
			{        
				ReservedItem reserveditem = customer.getReservedItem(reservedKey);
				TCPServer.Common.Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") has reserved " + reserveditem.getKey() + " " +  reserveditem.getCount() +  " times");
				TCPServer.Common.ReservableItem item  = (ReservableItem)readData(xid, reserveditem.getKey());
				TCPServer.Common.Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") has reserved " + reserveditem.getKey() + " which is reserved " +  item.getReserved() +  " times and is still available " + item.getCount() + " times");
				item.setReserved(item.getReserved() - reserveditem.getCount());
				item.setCount(item.getCount() + reserveditem.getCount());
				writeData(xid, item.getKey(), item);
			}

			// Remove the customer from the storage
			removeData(xid, customer.getKey());
			Trace.info("RM::deleteCustomer(" + xid + ", " + customerID + ") succeeded");
			return true;
		}
	}

	// Adds flight reservation to this customer
	public boolean reserveFlight(int xid, int customerID, int flightNum) throws RemoteException
	{
		return reserveItem(xid, customerID, Flight.getKey(flightNum), String.valueOf(flightNum));
	}

	// Adds car reservation to this customer
	public boolean reserveCar(int xid, int customerID, String location) throws RemoteException
	{
		return reserveItem(xid, customerID, Car.getKey(location), location);
	}

	// Adds room reservation to this customer
	public boolean reserveRoom(int xid, int customerID, String location) throws RemoteException
	{
		return reserveItem(xid, customerID, Room.getKey(location), location);
	}

	// Reserve bundle 
	public boolean bundle(int xid, int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException
	{
		return false;
	}

	public String getName() throws RemoteException
	{
		return m_name;
	}
}
 
