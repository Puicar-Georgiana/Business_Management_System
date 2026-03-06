package Model;

/**
 * Represents a product with a name, price, and stock quantity.
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;

    /**
     * Creates an empty Product object.
     */
    public Product() {}

    /**
     * Creates a Product with specified values.
     *
     * @param id     the product ID
     * @param name   the product name
     * @param price  the product price
     * @param stock  the quantity in stock
     */
    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /** @return the product ID */
    public int getId() { return id; }

    /** @param id the product ID to set */
    public void setId(int id) { this.id = id; }

    /** @return the product name */
    public String getName() { return name; }

    /** @param name the product name to set */
    public void setName(String name) { this.name = name; }

    /** @return the product price */
    public double getPrice() { return price; }

    /** @param price the product price to set */
    public void setPrice(double price) { this.price = price; }

    /** @return the quantity in stock */
    public int getStock() { return stock; }

    /** @param stock the stock quantity to set */
    public void setStock(int stock) { this.stock = stock; }

    /**
     * Returns a string representation of the product.
     *
     * @return string describing the product
     */
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
