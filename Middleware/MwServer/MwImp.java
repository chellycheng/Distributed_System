package MwServer;
import Common.*;
import CustomerServer.*;
import CarServer.CarResourceManager;
import FlightServer.FlightResourceManager;
import LockManager.*;
import LockManager.LockManager;
import LockManager.TransactionLockObject;
import RoomServer.RoomResourceManager;
import CustomerServer.CustomerResourceManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Vector;
import ResourceManager.*;
import TransactionManager.TransactionManager;
import Exceptions.*;

import javax.swing.tree.TreeCellRenderer;


public class MwImp implements MwInterface {

    private CarResourceManager cm;
    private FlightResourceManager fm;
    private RoomResourceManager rm;
    private CustomerResourceManager ctm;
    private Hashtable<String, ResourceManager> rms;
    //constants
    private final String cm_name = "car_server18";
    private final String fm_name = "flight_server18";
    private final String rm_name = "room_server18";
    //in case the need of set up customer as remote one
    private final String ctm_name = "customer_server18";
    private static String s_serverName = "MwServer";

    //Transaction Manager components
    private TransactionManager tm;
    private LockManager lm;



    public static void main(String[] args) throws Exception{

        if (args.length < 3) {
            System.out.println("Help: input format [carhost:port] [flighthost:port] [roomhost:port] [port]");
            return;
        }

        //collecting network required information
        int port = args.length > 3 ? Integer.parseInt(args[3]) : 1018;
        String carServer = args[0]+":"+port;
        String flightServer = args[1]+":"+port;
        String roomServer = args[2]+":"+port;

        try {

            // Create a new server object and dynamically generate the stub (client proxy)
            MwImp obj = new MwImp(carServer, flightServer, roomServer);
            MwInterface proxyObj = (MwInterface) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry;
            String registry_name = "group_18_" + s_serverName;

            // Bind the registry
            try{
                registry = LocateRegistry.createRegistry(port);
            }
            catch (RemoteException e)
            {
                registry = LocateRegistry.getRegistry(port);
                System.out.println("Trying to connect to an external registry at port:" + port);
            }
            registry.rebind(registry_name, proxyObj);
            System.out.println("MiddlewareServer with name " + registry_name + " is ready at port " + port );

        } catch (Exception e) {
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
    }

    public MwImp( String cm_host, String fm_host, String rm_host){

        //connect to the remote server resource
        try {
            Trace.info(cm_host);
            this.cm = (CarResourceManager) connectRM(cm_host, cm_name);
            this.fm = (FlightResourceManager) connectRM(fm_host, fm_name);
            this.rm = (RoomResourceManager) connectRM(rm_host, rm_name);
        }
        catch (Exception e){
            e.printStackTrace();
            Trace.info("Fail to connect to one or more remote server");
        }

        //initialize the customer server resource
        this.ctm = new CustomerResourceManagerImp();

        //Transaction Manager components
        tm = new TransactionManager(cm, rm, fm, ctm);

        //TODO: Initialize the lock manager
        lm = new LockManager();
    }

    private ResourceManager connectRM(String address, String bind_name){
        ResourceManager rm = null;
        String[] info = address.split(":");
        if(info.length != 2){
            throw new IllegalArgumentException("Invalid host address");
        }
        String host = info[0];
        int port = Integer.parseInt(info[1]);
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            rm = (ResourceManager) registry.lookup(bind_name);

        } catch (Exception e) {
            e.printStackTrace();
            Trace.info("Unable to connect to RM " + bind_name +" with address" + address);
            System.exit(0);
        }

        return rm;

    }

    @Override
    public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException,InvalidTransactionException {
        //Using this function as example for D2
        //TODO: Get the lock
        //TODO: Query the parameter
        //TODO: track of related manager
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist or non active");
        }

        try{
            lm.Lock(xid,"flight"+flightNum, TransactionLockObject.LockType.LOCK_WRITE );
            tm.enlist(xid, fm);

            return fm.addFlight(xid,flightNum,flightSeats,flightPrice);
        }catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;

        }
        catch (Exception e){
            throw new RemoteException("Fail to add Flight");
        }
    }

    @Override
    public boolean addCars(int xid, String location, int count, int price) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }

        try{
            lm.Lock(xid, "car"+location, TransactionLockObject.LockType.LOCK_WRITE);
            tm.enlist(xid, cm);
            return cm.addCars(xid, location, count, price);
        } catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;
        }
        catch (Exception e){
            throw new RemoteException("Fail to add Car");
        }
    }

    @Override
    public boolean addRooms(int xid, String location, int count, int price) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid, "room"+location, TransactionLockObject.LockType.LOCK_WRITE);
            tm.enlist(xid, rm);
            return rm.addRooms(xid, location, count, price);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;

        }
        catch (Exception e){
            throw new RemoteException("Fail to add Room");
        }
    }

    @Override
    public int newCustomer(int xid) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        tm.enlist(xid, ctm);
        try{
            return ctm.newCustomer(xid);
        }
        catch (Exception e){
            throw new RemoteException("Fail to call new customer1");
        }
    }

    @Override
    public boolean newCustomer(int xid, int cid) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }

        try{
            lm.Lock(xid,"customer"+cid, TransactionLockObject.LockType.LOCK_WRITE);
            tm.enlist(xid, ctm);
            return ctm.newCustomer(xid, cid);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;

        }
        catch (Exception e){
            throw new RemoteException("Fail to call new customer2");
        }
    }

    @Override
    public boolean deleteFlight(int xid, int flightNum) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid,"flight"+flightNum, TransactionLockObject.LockType.LOCK_WRITE);
            tm.enlist(xid, fm);
            return fm.deleteFlight(xid, flightNum);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;

        }
        catch (Exception e){
            throw new RemoteException("Fail to delete flight");
        }
    }

    @Override
    public boolean deleteCars(int xid, String location) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }

        try{
            lm.Lock(xid, "car"+location, TransactionLockObject.LockType.LOCK_WRITE);
            tm.enlist(xid, cm);
            return cm.deleteCars(xid, location);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;

        }
        catch (Exception e){
            throw new RemoteException("Fail to delete car");
        }
    }

    @Override
    public boolean deleteRooms(int xid, String location) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid,"room"+location, TransactionLockObject.LockType.LOCK_WRITE);
            tm.enlist(xid, rm);
            return rm.deleteRooms(xid, location);
        }catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;

        }
        catch (Exception e){
            throw new RemoteException("Fail to delete room");
        }
    }

    @Override
    public boolean deleteCustomer(int xid, int customerID) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }

        try {
            lm.Lock(xid,"customer"+customerID, TransactionLockObject.LockType.LOCK_WRITE);
            tm.enlist(xid, ctm);
            if (ctm.delete_check(xid, customerID)) {
                    String bill = ctm.queryCustomerInfo(xid, customerID);
                    // Increase the reserved numbers of all reservable items which the customer reserved.
                    String[] reservations = bill.split("\n");
                    for (int i = 1; i < reservations.length; i++) {
                        String[] temp = reservations[i].split(" ");
                        int count = Integer.parseInt(temp[0]);
                        String key = temp[1];

                        String[] key_component = key.split("-");
                        String resourceName = key_component[0];
                        try {
                            switch (resourceName) {
                                case "flight":
                                    lm.Lock(xid,"flight"+key, TransactionLockObject.LockType.LOCK_WRITE);
                                    fm.reserve_cancel(xid, customerID, count, key);
                                    break;
                                case "car":
                                    lm.Lock(xid,"car"+key, TransactionLockObject.LockType.LOCK_WRITE);
                                    cm.reserve_cancel(xid, customerID, count, key);
                                    break;
                                case "room":
                                    lm.Lock(xid,"room"+key, TransactionLockObject.LockType.LOCK_WRITE);
                                    rm.reserve_cancel(xid, customerID, count, key);
                                    break;
                            }
                        } catch (DeadlockException deadlockException){
                            tm.abort(xid);
                            return false;

                        }

                        catch (Exception e) {
                            Trace.error("One of the reserve cancel failure ");
                        }
                    }

                    return ctm.deleteCustomer(xid, customerID);

            }

        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;

        }
        catch (Exception e) {
            throw new RemoteException("Fail to delete customer");
        }
        return false;
    }

    @Override
    public int queryFlight(int xid, int flightNum) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid,"flight"+flightNum, TransactionLockObject.LockType.LOCK_READ);
            tm.enlist(xid, fm);
            return fm.queryFlight(xid, flightNum);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return -1;

        }
        catch (Exception e){
            throw new RemoteException("Fail to query flight");
        }
    }

    @Override
    public int queryCars(int xid, String location) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid, "car"+location, TransactionLockObject.LockType.LOCK_READ);
            tm.enlist(xid, cm);
            return cm.queryCars(xid, location);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return -1;

        }
        catch (Exception e){
            throw new RemoteException("Fail to query cars");
        }
    }

    @Override
    public int queryRooms(int xid, String location) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid,"room"+location, TransactionLockObject.LockType.LOCK_READ);
            tm.enlist(xid, rm);
            return rm.queryRooms(xid, location);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return -1;

        }
        catch (Exception e){
            throw new RemoteException("Fail to query rooms");
        }
    }

    @Override
    public String queryCustomerInfo(int xid, int customerID) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid, "customer"+customerID, TransactionLockObject.LockType.LOCK_READ);
            tm.enlist(xid, ctm);
            return ctm.queryCustomerInfo(xid, customerID);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return "Fail to query customer";

        }

        catch (Exception e){
            throw new RemoteException("Fail to query customers");
        }
    }

    @Override
    public int queryFlightPrice(int xid, int flightNum) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid,"flight"+flightNum, TransactionLockObject.LockType.LOCK_READ);
            tm.enlist(xid, fm);
            return fm.queryFlightPrice(xid, flightNum);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return -1;

        }
        catch (Exception e){
            throw new RemoteException("Fail to query price of flight");
        }
    }

    @Override
    public int queryCarsPrice(int xid, String location) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }

        try{
            lm.Lock(xid, "car"+location, TransactionLockObject.LockType.LOCK_READ);
            tm.enlist(xid, cm);
            return cm.queryCarsPrice(xid, location);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return -1;

        }
        catch (Exception e){
            throw new RemoteException("Fail to query price of car");
        }
    }

    @Override
    public int queryRoomsPrice(int xid, String location) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            lm.Lock(xid, "room"+location, TransactionLockObject.LockType.LOCK_READ);
            tm.enlist(xid, rm);
            return rm.queryRoomsPrice(xid, location);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return -1;

        }
        catch (Exception e){
            throw new RemoteException("Fail to query price of room");
        }
    }

    @Override
    public boolean reserveFlight(int xid, int customerID, int flightNum) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            int price = -1;
            String key = "flight-" + flightNum;
            key.toLowerCase();
            try{
                //if the flight is available
                lm.Lock(xid,"flight"+flightNum, TransactionLockObject.LockType.LOCK_READ);
                lm.Lock(xid,"customer"+customerID, TransactionLockObject.LockType.LOCK_READ);
                if(fm.reserve_check(xid, flightNum) && ctm.reserve_item(xid, customerID)){
                    //LOG
                    tm.enlist(xid, fm);
                    tm.enlist(xid, ctm);
                    price = fm.queryFlightPrice(xid, flightNum);
                }
                else{
                    Trace.info("ReserveFlight request is received, but failed due to lack of flight or customer");
                    return false;
                }
            }
            catch (DeadlockException deadlockException){
                tm.abort(xid);
                return false;

            }
            catch(Exception e){
                throw new RemoteException("Fail to access the info of flight, or the client did not exist");
            }
            lm.Lock(xid,"flight"+flightNum, TransactionLockObject.LockType.LOCK_WRITE);
            lm.Lock(xid,"customer"+customerID, TransactionLockObject.LockType.LOCK_WRITE);
            return fm.reserveFlight(xid, flightNum) && ctm.reserveFlight(xid, customerID, key, String.valueOf(flightNum), price);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;

        }
        catch (Exception e){
            throw new RemoteException("Fail to reserve for the flight");
        }
    }

    @Override
    public boolean reserveCar(int xid, int customerID, String location) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }

        try{
            int price = -1;
            String key = "car-" + location;
            key.toLowerCase();
            try{
                //if the flight is available
                lm.Lock(xid, "customer"+customerID, TransactionLockObject.LockType.LOCK_READ);
                lm.Lock(xid,"car"+location, TransactionLockObject.LockType.LOCK_READ);
                if(cm.reserve_check(xid, location)&& ctm.reserve_item(xid, customerID)){
                    //LOG
                    tm.enlist(xid, cm);
                    tm.enlist(xid, ctm);
                    price = cm.queryCarsPrice(xid, location);
                }
                else{
                    Trace.info("ReserveCar request is received, but failed due to lack of car or customer");
                    return false;
                }
            }
            catch (DeadlockException deadlockException){
                tm.abort(xid);
                return false;

            }
            catch(Exception e){
                throw new RemoteException("Fail to access the info of car, or the client did not exist");
            }
            lm.Lock(xid, "customer"+customerID, TransactionLockObject.LockType.LOCK_WRITE);
            lm.Lock(xid,"car"+location, TransactionLockObject.LockType.LOCK_WRITE);
            return cm.reserveCar(xid, location) && ctm.reserveCar(xid, customerID, key, location, price);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;
        }
        catch (Exception e){
            throw new RemoteException("Fail to reserve for the car");
        }
    }

    @Override
    public boolean reserveRoom(int xid, int customerID, String location) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try{
            int price = -1;
            String key = "room-" + location;
            key.toLowerCase();
            try{
                //if the flight is available
                lm.Lock(xid, "customer"+customerID, TransactionLockObject.LockType.LOCK_READ);
                lm.Lock(xid,"room"+location, TransactionLockObject.LockType.LOCK_READ);
                if(rm.reserve_check(xid, location)&& ctm.reserve_item(xid, customerID)){
                    //LOG
                    tm.enlist(xid, rm);
                    tm.enlist(xid, ctm);
                    price = rm.queryRoomsPrice(xid, location);
                }
                else{
                    Trace.info("ReserveRoom request is received, but failed due to lack of room or customer");
                    return false;
                }
            }
            catch (DeadlockException deadlockException){
                tm.abort(xid);
                return false;
            }
            catch(Exception e){
                throw new RemoteException("Fail to access the info of room, or the client did not exist");
            }
            lm.Lock(xid, "customer"+customerID, TransactionLockObject.LockType.LOCK_READ);
            lm.Lock(xid,"room"+location, TransactionLockObject.LockType.LOCK_READ);
            return rm.reserveRoom(xid, location) && ctm.reserveCar(xid, customerID, key, location, price);
        }
        catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;
        }
        catch (Exception e){
            throw new RemoteException("Fail to reserve for the room");
        }
    }

    @Override
    public boolean bundle(int xid, int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException,InvalidTransactionException {
        if(!tm.verifyTransactionId(xid)){
            throw new InvalidTransactionException(xid, "Non-exist");
        }
        try {
            lm.Lock(xid, "room" + location, TransactionLockObject.LockType.LOCK_READ);
            lm.Lock(xid, "car" + location, TransactionLockObject.LockType.LOCK_READ);
            boolean room_success = room && rm.reserve_check(xid, location);
            boolean car_success = car && cm.reserve_check(xid, location);
            for (String flightnumstring : flightNumbers) {
                int flightNum = Integer.parseInt(flightnumstring);
                lm.Lock(xid, "flight" + flightNum, TransactionLockObject.LockType.LOCK_READ);
                if (!fm.reserve_check(xid, flightNum)) {
                    Trace.info("FlightRM: Not able to reserve flight " + flightNum);
                    return false;
                }
            }
            //LOG
            tm.enlist(xid, ctm);
            tm.enlist(xid, fm);
            lm.Lock(xid, "customer" + customerId, TransactionLockObject.LockType.LOCK_READ);
            boolean client_success = ctm.reserve_item(xid, customerId);
            Trace.info("TEST-room: " + room_success);
            Trace.info("TEST-car: " + car_success);
            boolean final_check = (room_success || !room) && (car_success || !car) && client_success;
            Trace.info("TEST-final: " + final_check);
            if (final_check) {

                try {
                    if (room) {
                        //LOG
                        lm.Lock(xid,"room"+location, TransactionLockObject.LockType.LOCK_WRITE);
                        lm.Lock(xid,"customer"+customerId, TransactionLockObject.LockType.LOCK_WRITE);

                        tm.enlist(xid, rm);

                        String room_key = "room-" + location;
                        room_key = room_key.toLowerCase();
                        int room_price = rm.queryRoomsPrice(xid, location);
                        rm.reserveRoom(xid, location);
                        ctm.reserveRoom(xid, customerId, room_key, location, room_price);
                        Trace.info(xid + " Reserve for the room at " + location + " for " + customerId);
                    }
                }
                catch (DeadlockException deadlockException){
                    tm.abort(xid);
                    return false;
                }
                catch (Exception e) {
                    throw new RemoteException(xid + " Fail to reserve for the room at " + location + " for " + customerId);
                }

                try {
                    String car_key = "car-" + location;
                    car_key = car_key.toLowerCase();
                    if (car) {
                        //LOG
                        lm.Lock(xid,"car"+location, TransactionLockObject.LockType.LOCK_WRITE);
                        lm.Lock(xid,"customer"+customerId, TransactionLockObject.LockType.LOCK_WRITE);
                        tm.enlist(xid, cm);

                        int car_price = cm.queryCarsPrice(xid, location);
                        cm.reserveCar(xid, location);
                        ctm.reserveCar(xid, customerId, car_key, location, car_price);
                        Trace.info(xid + " Reserve for the car at " + location + " for " + customerId);
                    }


                }
                catch (DeadlockException deadlockException){
                    tm.abort(xid);
                    return false;
                }
                catch (Exception e) {
                    throw new RemoteException(xid + " Fail to reserve for the car at " + location + " for " + customerId);
                }

                try {
                    lm.Lock(xid,"customer"+customerId, TransactionLockObject.LockType.LOCK_WRITE);
                    for (String flightnumstring : flightNumbers) {
                        String flight_key = "flight-" + flightnumstring;
                        flight_key = flight_key.toLowerCase();
                        int flightNum = Integer.parseInt(flightnumstring);
                        int flight_price = fm.queryFlightPrice(xid, flightNum);
                        lm.Lock(xid,"flight"+flightNum, TransactionLockObject.LockType.LOCK_WRITE);

                        fm.reserveFlight(xid, flightNum);
                        ctm.reserveFlight(xid, customerId, flight_key, location, flight_price);
                    }
                }
                catch (DeadlockException deadlockException){
                    tm.abort(xid);
                    return false;
                }
                catch (Exception e) {
                    throw new RemoteException(xid + " Fail to reserve for the flight at " + location + " for " + customerId);
                }
                return true;
            } else {
                return false;
            }
        } catch (DeadlockException deadlockException){
            tm.abort(xid);
            return false;
        }
        catch (Exception e){
            throw new RemoteException("Fail to reserve for the room");
        }

    }

    @Override
    public String getName() throws RemoteException {
        return "group_18_" + s_serverName;
    }

    //TODO: Overall todo, implemeneted the timeout stragegy

    @Override
    public int start() throws RemoteException {
        return tm.start();
    }

    @Override
    public boolean commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
        //TODO: Make this method only allow one to commit at a time  synchronize???
        if(tm.commit(xid)){
            return true;
        }
        lm.UnlockAll(xid);
        //TODO: After all successful commit -> release the lock
        return false;
    }

    @Override
    public void abort(int xid) throws RemoteException, InvalidTransactionException {
        tm.abort(xid);
        lm.UnlockAll(xid);
    }

    @Override
    public boolean shutdown() throws RemoteException {
        return tm.shutdown();
    }
}