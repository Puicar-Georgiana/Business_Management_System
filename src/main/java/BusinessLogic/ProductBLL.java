package BusinessLogic;

import Data_Access.ProductDAO;
import Model.Product;

import java.util.List;

/**
 * Business Logic Layer for handling operations related to Product entities.
 */
public class ProductBLL {
    private ProductDAO productDAO;

    /**
     * Constructs a new ProductBLL instance and initializes the ProductDAO.
     */
    public ProductBLL() {
        this.productDAO = new ProductDAO();
    }

    /**
     * Retrieves all products.
     *
     * @return a list of all Product objects
     */
    public List<Product> getProducts() {
        return productDAO.findAll();
    }

    /**
     * Adds a new product.
     *
     * @param product the Product object to be added
     */
    public void addProduct(Product product) {
        try {
            productDAO.insert(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing product.
     *
     * @param product the Product object with updated data
     */
    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId the ID of the product to be deleted
     */
    public void deleteProduct(int productId) {
        Product product = new Product();
        product.setId(productId);
        productDAO.delete(product);
    }

    /**
     * Finds a product by its ID.
     *
     * @param id the product ID
     * @return the found Product object, or null if not found
     */
    public Product findProductById(int id) {
        return productDAO.findById(id);
    }
}
