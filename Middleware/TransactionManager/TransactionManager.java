package TransactionManager;
import java.rmi.RemoteException;
import java.util.*;
import Common.Trace;
import Exceptions.TransactionAbortedException;
import Exceptions.InvalidTransactionException;
import MwServer.MwImp;
import ResourceManager.ResourceManager;

public class TransactionManager {

    // TIMEOUT
    public static final long TRANSACTION_TIMEOUT = 1000;

    private int tid;
    private Hashtable<Integer, Transaction> transactions;
    private Hashtable<Integer, Timer> timers;
    public Hashtable<String, ResourceManager> mapping;

    public TransactionManager(ResourceManager rm1,ResourceManager rm2, ResourceManager rm3, ResourceManager rm4 ){
        transactions =  new Hashtable<>();
        timers = new Hashtable<>();
        this.tid = 0;
        mapping = new Hashtable<String, ResourceManager>();
        mapping.put("Car_server", rm1);
        mapping.put("Room_server", rm2);
        mapping.put("Flight_server", rm3);
        mapping.put("Customer_server", rm4);

    }
    public int start() throws RemoteException{
        tid ++;
        Trace.info("Transaction Manger: Start the transaction" + tid);
        transactions.put(tid,new Transaction());
        resetTimeout(tid);
        return tid;
    }

    public boolean commit(int transactionId)
            throws RemoteException,TransactionAbortedException, InvalidTransactionException{
        if(!verifyTransactionId(transactionId)){
            throw new InvalidTransactionException(transactionId," Non-exist or not active");
        }
        Transaction t = transactions.get(transactionId);

        if (t.status != TransactionStatus.ACTIVE) {
            throw new InvalidTransactionException(
                    transactionId, "Cannot commit this transaction (status: " + t.status + ").");
        }
        Trace.info("Transaction Manger: Commit starts");
        t.status = TransactionStatus.IN_COMMIT;
        boolean success = true;
        for (String rm_name : t.rms) {
            Trace.info("Transaction Manger: Commit is running for"+ rm_name);
            ResourceManager rm = mapping.get(rm_name);
            success &= rm.commit(transactionId);
        }
        if(!success){
            //TODO: commit failure
            throw new TransactionAbortedException(transactionId, "Aborted");
        }
        t.status = TransactionStatus.COMMITTED;
        return success;
    }

    public void abort(int transactionId) throws RemoteException,
            InvalidTransactionException{
        if(!transactions.containsKey(transactionId)){
            throw new InvalidTransactionException(transactionId," Non exist");
        }
        else{
            Transaction t = transactions.get(transactionId);
            if(t.status != TransactionStatus.ACTIVE){
                throw new InvalidTransactionException(transactionId, "now status-"+ t.status);
            }
            t.status = TransactionStatus.IN_ABORT;
            for(ResourceManager rm: mapping.values()){
                Trace.info("Transaction Manger: Shutdown is running for"+ rm.getName());
                rm.abort(transactionId);
                Trace.info("Transaction Manger: Abort successfully.");
            }
            t.status = TransactionStatus.ABORTED;
        }


    }
    public boolean shutdown() throws RemoteException{
        for(Transaction t: transactions.values()){
            if(t.status == TransactionStatus.ACTIVE || t.status == TransactionStatus.IN_COMMIT || t.status == TransactionStatus.IN_ABORT){
                Trace.info("Transaction Manger: Fail to shutdown because of active/in_commit transaction");
                return false;
            }
        }
        try {
            for(ResourceManager rm: mapping.values()){
                if(!rm.getName().equals("Customer_server")){
                    Trace.info("Transaction Manger: Shutdown is running for "+ rm.getName());
                    rm.shutdown();
                    Trace.info("Transaction Manger: Shutdown successfully.");
                }
            }
            mapping.get("Customer_server").shutdown();
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            Trace.info("Transaction Manger: Shutdown fail for resource managers.");
            return false;
        }
    }

    public void enlist(int id, ResourceManager rm) throws RemoteException {
        String rm_name = rm.getName();
        Trace.info("Enlisting " + rm_name + " for transaction " + id);
        transactions.get(id).rms.add(rm_name);
        resetTimeout(id);
    }

    public boolean verifyTransactionId(int xid){
        if(transactions.containsKey(xid)){
            if(transactions.get(xid).status != TransactionStatus.ACTIVE){
                return false;
            }
            return true;
        }
        else{
            return false;
        }
    }
    //TOOD: purge somewhere
    public void resetTimeout(int xid) {
        if (verifyTransactionId(xid)) {
            // Cancel the previous timer
            TransactionStatus status = transactions.get(xid).status;
            if (timers.containsKey(xid)) {
                timers.get(xid).cancel();
            }

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            if (status == TransactionStatus.ACTIVE) {
                                Trace.info("Timeout. Aborting transaction " + xid);
                                abort(xid);
                            }
                            else{
                                Trace.info("Timer is useless "+ xid);
                                timers.remove(timer);
                            }
                        } catch (Exception e) {
                            // Ignore.
                        }
                    }
                }, TRANSACTION_TIMEOUT);

                timers.put(xid, timer);
        }
    }

    private boolean selfDestroy(int status) {

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
