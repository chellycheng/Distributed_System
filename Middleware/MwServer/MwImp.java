package MwServer;
import MwServer.MwInterface;
import CarServer.CarResourceManager;
import FlightServer.FlightResourceManager;
import RoomServer.RoomResourceManager;
import CustomerServer.CustomerResourceManager;
import java.io.IOException;
import java.net.ServerSocket;
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
    private final String ctm_name = "customer_server18";


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
        int port = args.length > 3 ? Integer.parseInt(args[args.length - 1]) : 1018;

        try {
            // Parse the arguments
            // Create a new server object and dynamically generate the stub (client proxy)
            MwImp obj = new MwImp(carServer, flightServer, roomServer);
            MwInterface proxyObj = (MwInterface) UnicastRemoteObject.exportObject(obj, 0);

            Registry registry;
            String registry_name = "mw_server18";
            // Bind the registry
            try{
                registry = LocateRegistry.createRegistry(port);
            }
            catch (RemoteException e)
            {
                System.out.println("Trying to connect to an external registry at port:" + port);
                registry = LocateRegistry.getRegistry(port);
            }

            registry.rebind(registry_name, proxyObj);
            System.out.println("MiddlewareServer with name \" + registry_name + \" is ready at port \" + port +\" \"");
        } catch (Exception e) {
            System.err.println("Error: " + e.toString());
            e.printStackTrace();
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
    }
    public MwImp( String cm_host, String fm_name, String rm_name){


        Registry


    }

    private void

    @Override
    public boolean addFlight(int var1, int var2, int var3, int var4) throws RemoteException {
        try{

        }
        catch{

        }
        return
    }

    @Override
    public boolean addCars(int var1, String var2, int var3, int var4) throws RemoteException {
        return false;
    }

    @Override
    public boolean addRooms(int var1, String var2, int var3, int var4) throws RemoteException {
        return false;
    }

    @Override
    public int newCustomer(int var1) throws RemoteException {
        return 0;
    }

    @Override
    public boolean newCustomer(int var1, int var2) throws RemoteException {
        return false;
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