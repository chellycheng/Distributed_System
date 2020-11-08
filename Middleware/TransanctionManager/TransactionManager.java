package TransanctionManager;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import Common.Trace;
import Exception.TransactionAbortedException;
import Exception.InvalidTransactionException;
import MwServer.MwImp;

import javax.sound.midi.Track;

public class TransactionManager {

    // TIMEOUT
    public static final long TRANSANCTION_TIMEOUT = 1000000;
    public static final long RESPONSE_TIMEOUT = 1000;

//    public Hashtable<String, Transaction> active_transanction = new Hashtable<>();
//    public Hashtable<String, Transaction> wait_transanction = new Hashtable<>();

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
        Trace.info("Start the transaction" + tid);
        transactions.put(tid,new Transaction());
        return tid;
    }

    public boolean commit(int transactionId)
            throws RemoteException,TransactionAbortedException, InvalidTransactionException{
        Transaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new InvalidTransactionException(transactionId, "Non-Exist");
        }

        //chekc transanction satus
        if (transaction.status != TransactionStatus.IN_PREPARE && transaction.status != TransactionStatus.IN_COMMIT) {
            throw new InvalidTransactionException(
                    transactionId, "Cannot commit this transaction (status: " + transaction.status + ").");
        }
        boolean success = true;
//        ExecutorService executor = Executors.newSingleThreadExecutor();
        int i = 1;

//
//        try {
//            for (String rm : transaction.rms) {
//                Future<Boolean> future = executor.submit(new Callable<Boolean>() {
//                    public Boolean call() throws Exception {
//                        Trace.info("Sending prepare request to " + rm);
//                        mw.prepare(rm, transactionId);
//                        return mw.prepare(rm, transactionId);
//                    }
//                });
//
//                // One resource manager voted NO
//                if (!future.get(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS)) {
//                    log("Negative response received. Returning false...");
//                    success = false;
//                    break;
//                }
//                i++;
//            }
//        } catch (InterruptedException e) {
//            // One resource manager crashed
//            success = false;
//        }

        return false;
    }

    public void abort(int transactionId) throws RemoteException,
            InvalidTransactionException{

    }
    public boolean shutdown() throws RemoteException{
        return false;
    }
}
