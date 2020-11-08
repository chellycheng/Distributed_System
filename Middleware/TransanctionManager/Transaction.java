package TransanctionManager;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 2084803164578661556L;
    public Set<String> rms; // The resource managers involved in the transaction
    public TransactionStatus status;

    public Transaction() {
        this.rms = new HashSet<>();
        this.status = TransactionStatus.ACTIVE;
    }
}
