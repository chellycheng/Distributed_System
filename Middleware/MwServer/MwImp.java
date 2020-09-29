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
//        if (args.length < 3) {
//            System.out.println("Help: input format [carhost:port] [flighthost:port] [roomhost:port] [port]");
//            return;
//        }

        //collecting network required information
//        String carServer = args[0];
//        String flightServer = args[1];
//        String roomServer = args[2];
//        int port = args.length > 3 ? Integer.parseInt(args[3]) : 1018;
        int port = args.length > 3 ? Integer.parseInt(args[0]) : 1018;

        try {
            // Parse the arguments
            // Create a new server object and dynamically generate the stub (client proxy)
//            MwImp obj = new MwImp(carServer, flightServer, roomServer);
            //testing purpose
            MwImp obj = new MwImp();
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
            rms.put(rm.getClass().getInterfaces()[0].getName(), rm);

        } catch (Exception e) {
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
    public boolean deleteFlight(int var1, int var2) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteCars(int var1, String var2) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteRooms(int var1, String var2) throws RemoteException {
        return false;
    }

    @Override
    public boolean deleteCustomer(int var1, int var2) throws RemoteException {
        return false;
    }

    @Override
    public int queryFlight(int var1, int var2) throws RemoteException {
        return 0;
    }

    @Override
    public int queryCars(int var1, String var2) throws RemoteException {
        return 0;
    }

    @Override
    public int queryRooms(int var1, String var2) throws RemoteException {
        return 0;
    }

    @Override
    public String queryCustomerInfo(int var1, int var2) throws RemoteException {
        return null;
    }

    @Override
    public int queryFlightPrice(int var1, int var2) throws RemoteException {
        return 0;
    }

    @Override
    public int queryCarsPrice(int var1, String var2) throws RemoteException {
        return 0;
    }

    @Override
    public int queryRoomsPrice(int var1, String var2) throws RemoteException {
        return 0;
    }

    @Override
    public boolean reserveFlight(int var1, int var2, int var3) throws RemoteException {
        return false;
    }

    @Override
    public boolean reserveCar(int var1, int var2, String var3) throws RemoteException {
        return false;
    }

    @Override
    public boolean reserveRoom(int var1, int var2, String var3) throws RemoteException {
        return false;
    }

    @Override
    public boolean bundle(int var1, int var2, Vector<String> var3, String var4, boolean var5, boolean var6) throws RemoteException {
        return false;
    }

    @Override
    public String getName() throws RemoteException {
        return null;
    }
}