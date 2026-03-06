package Model;

/**
 * Represents a bill for an order.
 *
 * @param id          the ID of the bill
 * @param orderId     the ID of the related order
 * @param clientName  the name of the client
 * @param productName the name of the product
 * @param quantity    the quantity ordered
 * @param totalPrice  the total price of the order
 */
public record Bill(int id, int orderId, String clientName, String productName,
                   int quantity, double totalPrice) {
}
