package BusinessLogic;

import Data_Access.OrderDAO;
import Data_Access.ProductDAO;
import Data_Access.BillDAO;
import Model.Bill;
import Model.Order;
import Model.Product;
import Model.Client;

import java.sql.Date;
import java.util.List;

/**
 * Business Logic Layer for handling operations related to Order entities.
 */
public class OrderBLL {
    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final BillDAO billDAO;

    /**
     * Constructs a new OrderBLL instance and initializes DAOs.
     */
    public OrderBLL() {
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
        this.billDAO = new BillDAO();
    }

    /**
     * Retrieves all orders.
     *
     * @return a list of all Order objects
     */
    public List<Order> getOrders() {
        return orderDAO.findAll();
    }

    /**
     * Creates a new order, updates stock, and generates a bill.
     *
     * @param clientId  the ID of the client placing the order
     * @param productId the ID of the product to be ordered
     * @param quantity  the quantity of the product
     * @return true if the order was successfully created, false otherwise
     */
    public boolean createOrder(int clientId, int productId, int quantity) {
        Product product = productDAO.findById(productId);
        if (product == null || product.getStock() < quantity) {
            return false;
        }

        Order order = new Order();
        order.setClientId(clientId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setOrderDate(new Date(System.currentTimeMillis()));


        try {
            orderDAO.insert(order);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        product.setStock(product.getStock() - quantity);
        productDAO.update(product);

        ClientBLL clientBLL = new ClientBLL();
        Client client = clientBLL.findClientById(clientId);
        double totalPrice = product.getPrice() * quantity;

        Bill bill = new Bill(0, order.getId(), client.getName(), product.getName(), quantity, totalPrice);
        Bill createdBill = billDAO.insert(bill);

        return createdBill != null;
    }

    /**
     * Finds an order by its ID.
     *
     * @param id the order ID
     * @return the found Order object, or null if not found
     */
    public Order findOrderById(int id) {
        return orderDAO.findById(id);
    }
}
