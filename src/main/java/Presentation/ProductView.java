package Presentation;

import BusinessLogic.ProductBLL;
import Model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents the Product management GUI.
 * It allows the user to add, edit, delete, and refresh products.
 */
public class ProductView implements ActionListener {
    private JFrame frame;
    private JPanel panel;
    private JTable productTable;
    private JButton add, edit, delete, refresh, back;
    private ProductBLL productBLL;

    /**
     * Constructor initializes the GUI components and loads products.
     */
    public ProductView() {
        productBLL = new ProductBLL();
        frame = new JFrame("Product Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        productTable = new JTable();
        refreshTable();

        add = new JButton("Add Product");
        edit = new JButton("Edit Product");
        delete = new JButton("Delete Product");
        refresh = new JButton("Refresh");
        back = new JButton("Back");

        add.addActionListener(this);
        edit.addActionListener(this);
        delete.addActionListener(this);
        refresh.addActionListener(this);
        back.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(add);
        buttonPanel.add(edit);
        buttonPanel.add(delete);
        buttonPanel.add(refresh);
        buttonPanel.add(back);

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Loads the product list and updates the table model.
     */
    private void refreshTable() {
        List<Product> products = productBLL.getProducts();
        DefaultTableModel model = Presentation.TableUtils.buildTableModelFromList(products);
        productTable.setModel(model);
    }

    /**
     * Prompts the user to enter product details and adds a new product.
     */
    private void addProduct() {
        String name = JOptionPane.showInputDialog("Enter product name:");
        String priceStr = JOptionPane.showInputDialog("Enter product price:");
        String stockStr = JOptionPane.showInputDialog("Enter product stock:");

        List<String> inputs = Arrays.asList(name, priceStr, stockStr);
        if (inputs.stream().allMatch(input -> input != null && !input.isEmpty())) {
            try {
                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);

                Product product = new Product();
                product.setName(name);
                product.setPrice(price);
                product.setStock(stock);

                productBLL.addProduct(product);
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Product added successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid input format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Allows the user to edit the selected product's details.
     */
    private void editProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a product to edit.");
            return;
        }

        int productId = Integer.parseInt(productTable.getValueAt(row, 0).toString());
        String name = JOptionPane.showInputDialog("Enter new name:", productTable.getValueAt(row, 1));
        String priceStr = JOptionPane.showInputDialog("Enter new price:", productTable.getValueAt(row, 2));
        String stockStr = JOptionPane.showInputDialog("Enter new stock:", productTable.getValueAt(row, 3));

        List<String> inputs = Arrays.asList(name, priceStr, stockStr);
        if (inputs.stream().allMatch(input -> input != null)) {
            try {
                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);

                Product product = new Product();
                product.setId(productId);
                product.setName(name);
                product.setPrice(price);
                product.setStock(stock);

                productBLL.updateProduct(product);
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Product updated successfully!");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid input format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes the selected product after user confirmation.
     */
    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a product to delete.");
            return;
        }

        int productId = Integer.parseInt(productTable.getValueAt(row, 0).toString());
        int confirmation = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete this product?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            productBLL.deleteProduct(productId);
            refreshTable();
            JOptionPane.showMessageDialog(frame, "Product deleted successfully!");
        }
    }

    /**
     * Handles button clicks for add, edit, delete, refresh, and back.
     *
     * @param e The action event triggered by a button.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            addProduct();
        } else if (e.getSource() == edit) {
            editProduct();
        } else if (e.getSource() == delete) {
            deleteProduct();
        } else if (e.getSource() == refresh) {
            refreshTable();
        } else if (e.getSource() == back) {
            frame.dispose();
        }
    }

    /**
     * Main method to launch the ProductView GUI.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductView::new);
    }
}
