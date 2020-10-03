package MwServer;
import MwServer.MwInterface;
import Common.*;
import CustomerServer.*;
import CarServer.CarResourceManager;
import FlightServer.FlightResourceManager;
import RoomServer.RoomResourceManager;
import CustomerServer.CustomerResourceManager;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Vector;
import ResourceManager.*;


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


    public static void main(String[] args) throws Exception{

        // use case
        // user need to give the host and port for respectively
        // car_server, flight_server, room_server
        if (args.length < 3) {
            System.out.println("Help: input format [carhost:port] [flighthost:port] [roomhost:port] [port]");
            return;
        }

        //collecting network required information
        String carServer = args[0];
        String flightServer = args[1];
        String roomServer = args[2];
        int port = args.length > 3 ? Integer.parseInt(args[3]) : 1018;

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
            this.cm = (CarResourceManager) connectRM(cm_host, cm_name);
            this.fm = (FlightResourceManager) connectRM(fm_host, fm_name);
            this.rm = (RoomResourceManager) connectRM(rm_host, rm_name);
        }
        catch (Exception e){
            Trace.info("Fail to connect to one or more remote server");
        }

        //initialize the customer server resource
        this.ctm = new CustomerResourceManagerImp();
//      rms.put(customerManager.getClass().getInterfaces()[0].getName(), customerManager);

    }

    //test purpose
    public MwImp(){

        //initialize the customer server resource
        this.ctm = new CustomerResourceManagerImp();
//      rms.put(customerManager.getClass().getInterfaces()[0].getName(), customerManager);

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

            //why?
            // rms.put(rm.getClass().getInterfaces()[0].getName(), rm);

        } catch (Exception e) {
            e.printStackTrace();
            Trace.info("Unable to connect to RM " + bind_name +" with address" + address);
            System.exit(0);
        }

        return rm;

    }

    @Override
    public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        try{
            return fm.addFlight(xid,flightNum,flightSeats,flightPrice);
        }
        catch (Exception e){
            throw new RemoteException("Fail to add Flight");
        }
    }

    @Override
    public boolean addCars(int xid, String location, int count, int price) throws RemoteException {
        try{
            return cm.addCars(xid, location, count, price);
        }
        catch (Exception e){
            throw new RemoteException("Fail to add Car");
        }
    }

    @Override
    public boolean addRooms(int xid, String location, int count, int price) throws RemoteException {
        try{
            return rm.addRooms(xid, location, count, price);
        }
        catch (Exception e){
            throw new RemoteException("Fail to add Room");
        }
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        try{
            return ctm.newCustomer(id);
        }
        catch (Exception e){
            throw new RemoteException("Fail to call new customer1");
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        try{
            return ctm.newCustomer(id, cid);
        }
        catch (Exception e){
            throw new RemoteException("Fail to call new customer2");
        }
    }

    @Override
    public boolean deleteFlight(int xid, int flightNum) throws RemoteException {
        try{
            return fm.deleteFlight(xid, flightNum);
        }
        catch (Exception e){
            throw new RemoteException("Fail to delete flight");
        }
    }

    @Override
    public boolean deleteCars(int xid, String location) throws RemoteException {
        try{
            return cm.deleteCars(xid, location);
        }
        catch (Exception e){
            throw new RemoteException("Fail to delete car");
        }
    }

    @Override
    public boolean deleteRooms(int xid, String location) throws RemoteException {
        try{
            return rm.deleteRooms(xid, location);
        }
        catch (Exception e){
            throw new RemoteException("Fail to delete room");
        }
    }

    @Override
    public boolean deleteCustomer(int xid, int customerID) throws RemoteException {
        try{
            return ctm.deleteCustomer(xid, customerID);
        }
        catch (Exception e){
            throw new RemoteException("Fail to delete customer");
        }
    }

    @Override
    public int queryFlight(int xid, int flightNum) throws RemoteException {
        try{
            return fm.queryFlight(xid, flightNum);
        }
        catch (Exception e){
            throw new RemoteException("Fail to query flight");
        }
    }

    @Override
    public int queryCars(int xid, String location) throws RemoteException {
        try{
            return cm.queryCars(xid, location);
        }
        catch (Exception e){
            throw new RemoteException("Fail to query cars");
        }
    }

    @Override
    public int queryRooms(int xid, String location) throws RemoteException {
        try{
            return rm.queryRooms(xid, location);
        }
        catch (Exception e){
            throw new RemoteException("Fail to query rooms");
        }
    }

    @Override
    public String queryCustomerInfo(int xid, int customerID) throws RemoteException {
        try{
            return ctm.queryCustomerInfo(xid, customerID);
        }
        catch (Exception e){
            throw new RemoteException("Fail to query customers");
        }
    }

    @Override
    public int queryFlightPrice(int xid, int flightNum) throws RemoteException {
        try{
            return fm.queryFlightPrice(xid, flightNum);
        }
        catch (Exception e){
            throw new RemoteException("Fail to query price of flight");
        }
    }

    @Override
    public int queryCarsPrice(int xid, String location) throws RemoteException {
        try{
            return cm.queryCarsPrice(xid, location);
        }
        catch (Exception e){
            throw new RemoteException("Fail to query price of car");
        }
    }

    @Override
    public int queryRoomsPrice(int xid, String location) throws RemoteException {
        try{
            return rm.queryRoomsPrice(xid, location);
        }
        catch (Exception e){
            throw new RemoteException("Fail to query price of room");
        }
    }

    @Override
    public boolean reserveFlight(int xid, int customerID, int flightNum) throws RemoteException {
        try{
            int price = -1;
            String key = "flight-" + flightNum;
            key.toLowerCase();
            try{
                //if the flight is available
                if(fm.reserveFlight(xid, flightNum)){
                    price = fm.queryFlightPrice(xid, flightNum);
                }
            }
            catch(Exception e){
                throw new RemoteException("Fail to access the info of flight");
            }

            return ctm.reserveFlight(xid, customerID, key, String.valueOf(flightNum), price);
        }
        catch (Exception e){
            throw new RemoteException("Fail to reserve for the flight");
        }
    }

    @Override
    public boolean reserveCar(int xid, int customerID, String location) throws RemoteException {
        try{
            int price = -1;
            String key = "car-" + location;
            key.toLowerCase();
            try{
                //if the flight is available
                if(cm.reserveCar(xid, location)){
                    price = cm.queryCarsPrice(xid, location);
                }
            }
            catch(Exception e){
                throw new RemoteException("Fail to access the info of car");
            }

            return ctm.reserveCar(xid, customerID, key, location, price);
        }
        catch (Exception e){
            throw new RemoteException("Fail to reserve for the car");
        }
    }

    @Override
    public boolean reserveRoom(int xid, int customerID, String location) throws RemoteException {
        try{
            int price = -1;
            String key = "room-" + location;
            key.toLowerCase();
            try{
                //if the flight is available
                if(rm.reserveRoom(xid, location)){
                    price = rm.queryRoomsPrice(xid, location);
                }
            }
            catch(Exception e){
                throw new RemoteException("Fail to access the info of room");
            }

            return ctm.reserveCar(xid, customerID, key, location, price);
        }
        catch (Exception e){
            throw new RemoteException("Fail to reserve for the room");
        }
    }

    @Override
    public boolean bundle(int xid, int customerId, Vector<String> flightNumbers, String location, boolean car, boolean room) throws RemoteException {
        boolean room_success = false;
        boolean car_success = false;
        boolean flight_success = false;
        try {
            try {
                String room_key = "room-" + location;
                room_key.toLowerCase();
                if (room && rm.reserveRoom(xid, location)) {
                    int room_price = rm.queryRoomsPrice(xid, location);
                    ctm.reserveRoom(xid, customerId, room_key, location, room_price);
                    Trace.info(xid+" Reserve for the room at " + location + " for " + customerId);
                    room_success = true;
                } else if (!room) {
                    room_success = true;
                }

            } catch (Exception e) {
                throw new RemoteException(xid + " Fail to reserve for the room at " + location + " for " + customerId);
            }

            try {
                String car_key = "car-" + location;
                car_key.toLowerCase();
                if (car && cm.reserveCar(xid, location)) {
                    int car_price = cm.queryCarsPrice(xid, location);
                    ctm.reserveCar(xid, customerId, car_key, location, car_price);
                    Trace.info(xid+" Reserve for the car at " + location + " for " + customerId);
                    car_success = true;

                } else if (!car) {
                    car_success = true;
                }
            } catch (Exception e) {
                throw new RemoteException(xid + " Fail to reserve for the car at " + location + " for " + customerId);
            }
            try {
                String flight_key = "flight-" + location;
                flight_key.toLowerCase();
                for (String flightnumstring : flightNumbers) {
                    int flightNum = Integer.parseInt(flightnumstring);
                    if (reserveFlight(xid, customerId, flightNum)) {
                        int flight_price = fm.queryFlightPrice(xid,flightNum);
                        ctm.reserveFlight(xid, customerId, flight_key, flightnumstring, flight_price);
                        Trace.info(xid+" Reserve for the flight " + flightnumstring + " for " + customerId);

                    }
                }
                flight_success = true;
            } catch (Exception e) {
                throw new RemoteException(xid + " Fail to reserve for the car at " + location + " for " + customerId);
            }

            return car_success && room_success && flight_success;

        }
        catch (Exception e){
            throw new RemoteException("Fail to reserve for the flight");
        }
    }

    @Override
    public String getName() throws RemoteException {
        return "group_18_" + s_serverName;
    }
}