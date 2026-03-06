package BusinessLogic;

import Data_Access.BillDAO;
import Model.Bill;

/**
 * Business Logic Layer for handling operations related to Bill entities.
 */
public class BillBLL {
    private final BillDAO billDAO;

    /**
     * Constructs a new BillBLL instance and initializes the BillDAO.
     */
    public BillBLL() {
        this.billDAO = new BillDAO();
    }

    /**
     * Retrieves a bill by the associated order ID.
     *
     * @param orderId the ID of the order
     * @return the corresponding Bill object, or null if not found
     */
    public Bill getBillByOrderId(int orderId) {
        return billDAO.findByOrderId(orderId);
    }

    /**
     * Creates a new bill in the database.
     *
     * @param bill the Bill object to be created
     * @return the inserted Bill object with its generated ID
     */
    public Bill createBill(Bill bill) {
        return billDAO.insert(bill);
    }
}
