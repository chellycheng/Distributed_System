package MwServer;
import MwServer.MwInterface;
import CarServer.CarResourceManager;
import FlightServer.FlightResourceManager;
import RoomServer.RoomResourceManager;
import CustomerServer.CustomerResourceManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;


public class MwImp implements MwInterface {


    private CarResourceManager carManager;
    private FlightResourceManager flightManager;
    private RoomResourceManager hotelManager;
    private CustomerResourceManager customerManager;

    public static void mian(String[] args) throws Exception{

    }

    @Override
    public boolean addFlight(int var1, int var2, int var3, int var4) throws RemoteException {
        return false;
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