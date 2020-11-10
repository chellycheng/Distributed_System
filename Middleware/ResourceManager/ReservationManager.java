package ResourceManager;
import Common.*;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map.*;
import java.util.Timer;
import java.util.TimerTask;

public abstract class ReservationManager<T extends RMItem> {

    protected RMHashMap resource_data = new RMHashMap();
    protected Hashtable<Integer, RMHashMap> resource_buffer = new Hashtable<>();


    public boolean commit(int xid) throws RemoteException{
        try{
            //if other resource manager fail then we need the transaction to abort.
            //we can only delete the transaction after server confirm all successfully commit
//            synchronized (resource_buffer){
//                resource_buffer.remove(xid);
//            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public void abort(int xid) throws RemoteException{
        try{
            synchronized (resource_buffer){
                RMHashMap rhm =  resource_buffer.get(xid);
                if(rhm == null){
                    //no operation has been down
                    return;
                }
                if(!rhm.isEmpty()) {
                    //Nothing needs to be reverted
                    for (Entry<String, RMItem> e : rhm.entrySet()) {
                        String key = e.getKey();
                        RMItem value = e.getValue();
                        if(value==null){
                            removeData(xid, key);
                        }
                        else{
                            writeData(xid, key, value);
                        }
                    }
                }
            }
        }
        catch(Exception e){
            Trace.info("Debug");
        }
    }

    public void enlist(int xid, String key, RMItem item){
        if(!resource_buffer.containsKey(xid)){
            RMHashMap new_map = new RMHashMap();
            new_map.put(key,item);
            resource_buffer.put(xid, new_map);
        }
        else{
            if(!resource_buffer.get(xid).containsKey(key)){
                resource_buffer.get(xid).put(key,item);
            }
            //else: we have record the first state
        }
    }


    protected RMItem readData(int xid, String key)
    {
        synchronized(resource_data) {
            RMItem item = resource_data.get(key);
            if (item != null) {
                return (RMItem)item.clone();
            }
            return null;
        }
    }

    // Writes a data item
    protected void writeData(int xid, String key, RMItem value)
    {
        synchronized(resource_data) {
            resource_data.put(key, value);
        }
    }

    // Remove the item out of storage
    protected void removeData(int xid, String key)
    {
        synchronized(resource_data) {
            resource_data.remove(key);
        }
    }

    protected boolean deleteItem(int xid, String key)
    {
        Trace.info("RM::deleteItem(" + xid + ", " + key + ") called");
        ReservableItem curObj = (ReservableItem)readData(xid, key);
        // Check if there is such an item in the storage
        if (curObj == null)
        {
            return false;
        }
        else
        {
            if (curObj.getReserved() == 0)
            {
                enlist(xid, key, (RMItem) curObj.clone());
                removeData(xid, curObj.getKey());
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    // Query the number of available seats/rooms/cars
    protected int queryNum(int xid, String key)
    {
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

    protected boolean selfDestroy(int status) {

        Timer shutdownTimer = new Timer();
        shutdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(status);
            }
        }, 1000);

        return true;
    }



}
