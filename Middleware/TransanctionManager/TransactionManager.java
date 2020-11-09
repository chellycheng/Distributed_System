package TransanctionManager;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import Common.Trace;
import Exception.TransactionAbortedException;
import Exception.InvalidTransactionException;
import MwServer.MwImp;
import ResourceManager.ResourceManager;


public class TransactionManager {

    // TIMEOUT
    public static final long TRANSANCTION_TIMEOUT = 1000000;
    public static final long RESPONSE_TIMEOUT = 1000;

    private MwImp mw;
    private int tid;
    private Hashtable<Integer, Transaction> transactions;

    public TransactionManager(MwImp mw){
        this.mw = mw;
        transactions =  new Hashtable<>();
        this.tid = 0;

    }
    public int start() throws RemoteException{
        tid ++;
        Trace.info("Transaction Manger: Start the transaction" + tid);
        transactions.put(tid,new Transaction());
        return tid;
    }

    public boolean commit(int transactionId)
            throws RemoteException,TransactionAbortedException, InvalidTransactionException{
        if(!transactions.containsKey(transactionId)){
            throw new InvalidTransactionException(transactionId," Non-exist");
        }
        Transaction t = transactions.get(transactionId);

        if (t.status != TransactionStatus.ACTIVE) {
            throw new InvalidTransactionException(
                    transactionId, "Cannot commit this transaction (status: " + t.status + ").");
        }
        //TODO: commit failure
        Trace.info("Transaction Manger: Commit starts");
        t.status = TransactionStatus.IN_COMMIT;
        boolean success = true;
        for (String rm_name : t.rms) {
            Trace.info("Transaction Manger: Commit is running for"+ rm_name);
            ResourceManager rm = this.mw.mapping.get(rm_name);
            success &= this.commit(transactionId);
        }
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
            for(ResourceManager rm: this.mw.mapping.values()){
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
            for(ResourceManager rm: this.mw.mapping.values()){
                Trace.info("Transaction Manger: Shutdown is running for"+ rm.getName());
                rm.shutdown();
                Trace.info("Transaction Manger: Shutdown successfully.");
            }
            System.exit(0);
            return true;
        }
        catch(Exception e){
            Trace.info("Transaction Manger: Shutdown fail for resource managers.");
            return false;
        }
    }

    public void enlist(int id, ResourceManager rm) throws RemoteException {
        String rm_name = rm.getName();
        Trace.info("Enlisting " + rm_name + " for transaction " + id);
        transactions.get(id).rms.add(rm_name);
    }

    public boolean verifyTransactionId(int xid){
        if(transactions.contains(xid)){
            return true;
        }
        else{
            return false;
        }
    }
}
