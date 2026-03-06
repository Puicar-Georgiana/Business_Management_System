package Model;

import java.sql.Date;
import java.util.Objects;

/**
 * Represents an order placed by a client for a product.
 */
public class Order {
    private int id;
    private int clientId;
    private int productId;
    private int quantity;
    private Date orderDate;

    /**
     * Creates an empty Order object.
     */
    public Order() {}

    /**
     * Creates an Order with specified values.
     *
     * @param id         the order ID
     * @param clientId   the ID of the client who placed the order
     * @param productId  the ID of the product ordered
     * @param quantity   the quantity of the product (must be >= 0)
     * @param orderDate  the date the order was placed
     */
    public Order(int id, int clientId, int productId, int quantity, Date orderDate) {
        this.id = id;
        this.clientId = clientId;
        this.productId = productId;
        setQuantity(quantity);
        this.orderDate = orderDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", orderDate=" + orderDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return id == order.id &&
                clientId == order.clientId &&
                productId == order.productId &&
                quantity == order.quantity &&
                Objects.equals(orderDate, order.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, productId, quantity, orderDate);
    }
}
