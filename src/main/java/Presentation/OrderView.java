package Presentation;

import BusinessLogic.BillBLL;
import BusinessLogic.ClientBLL;
import BusinessLogic.OrderBLL;
import BusinessLogic.ProductBLL;
import Model.Bill;
import Model.Client;
import Model.Order;
import Model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * OrderView is a GUI class to manage orders.
 * It allows users to view, create orders, show and create bills.
 */
public class OrderView implements ActionListener {
    private JFrame frame;
    private JPanel panel;
    private JTable orderTable;
    private JButton refresh, back, createOrder, showBill, createBill, saveBill;
    private OrderBLL orderBLL;
    private ClientBLL clientBLL;
    private ProductBLL productBLL;
    private BillBLL billBLL;
    private JTextArea billDetailsText;
    private Bill selectedBill;

    /**
     * Constructor initializes the GUI and business logic objects.
     * Sets up the main frame, buttons, table, and event listeners.
     */
    public OrderView() {
        orderBLL = new OrderBLL();
        clientBLL = new ClientBLL();
        productBLL = new ProductBLL();
        billBLL = new BillBLL();

        frame = new JFrame("Order Management");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        orderTable = new JTable();
        refreshTable();

        billDetailsText = new JTextArea();
        billDetailsText.setEditable(false);
        billDetailsText.setFont(new Font("Monospaced", Font.PLAIN, 14));
        billDetailsText.setLineWrap(true);
        billDetailsText.setWrapStyleWord(true);

        refresh = new JButton("Refresh");
        back = new JButton("Back");
        createOrder = new JButton("Create Order");
        showBill = new JButton("Show Bill");
        createBill = new JButton("Create Bill");
        saveBill = new JButton("Save Bill");

        refresh.addActionListener(this);
        back.addActionListener(this);
        createOrder.addActionListener(this);
        showBill.addActionListener(this);
        createBill.addActionListener(this);
        saveBill.addActionListener(this);

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(new JScrollPane(billDetailsText), BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(refresh);
        buttonPanel.add(back);
        buttonPanel.add(createOrder);
        buttonPanel.add(showBill);
        buttonPanel.add(createBill);
        buttonPanel.add(saveBill);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);

        orderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedRow = orderTable.getSelectedRow();
                    if (selectedRow != -1) {
                        showSelectedOrderBill(selectedRow);
                    }
                }
            }
        });
    }

    /**
     * Refreshes the order table with latest data from the database.
     */
    private void refreshTable() {
        List<Order> orders = orderBLL.getOrders();
        DefaultTableModel model = TableUtils.buildTableModelFromList(orders);
        orderTable.setModel(model);
    }

    /**
     * Shows the bill details for the selected order row.
     * @param rowIndex the index of the selected order row
     */
    private void showSelectedOrderBill(int rowIndex) {
        try {
            int orderId = (int) orderTable.getModel().getValueAt(rowIndex, 0);
            selectedBill = billBLL.getBillByOrderId(orderId);
            if (selectedBill == null) {
                Order order = orderBLL.findOrderById(orderId);
                if (order != null) {
                    Client client = clientBLL.findClientById(order.getClientId());
                    Product product = productBLL.findProductById(order.getProductId());
                    if (client != null && product != null) {
                        double totalPrice = product.getPrice() * order.getQuantity();
                        selectedBill = new Bill(0, order.getId(), client.getName(), product.getName(), order.getQuantity(), totalPrice);
                    }
                }
            }

            if (selectedBill != null) {
                String details = String.format(
                        "Bill ID: %d\nOrder ID: %d\nClient: %s\nProduct: %s\nQuantity: %d\nTotal Price: %.2f",
                        selectedBill.id(), selectedBill.orderId(), selectedBill.clientName(), selectedBill.productName(), selectedBill.quantity(), selectedBill.totalPrice()
                );
                billDetailsText.setText(details);
            } else {
                billDetailsText.setText("No bill available for this order.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error displaying bill details: " + ex.getMessage());
        }
    }

    /**
     * Handles button click events.
     * @param e the event triggered by button click
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == refresh) {
            refreshTable();
        } else if (e.getSource() == back) {
            frame.dispose();
        } else if (e.getSource() == createOrder) {
            createNewOrder();
        } else if (e.getSource() == showBill) {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow != -1) {
                showSelectedOrderBill(selectedRow);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an order first.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == createBill) {
            createBillForOrder();
        } else if (e.getSource() == saveBill && selectedBill != null) {
            saveBillToDatabase();
        }
    }

    /**
     * Saves the currently selected bill to the database.
     */
    private void saveBillToDatabase() {
        Bill savedBill = billBLL.createBill(selectedBill);
        if (savedBill != null) {
            JOptionPane.showMessageDialog(frame, "Bill saved successfully!");
            selectedBill = savedBill;
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to save bill.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates a new bill for the selected order.
     * Shows error if no order is selected or if data is invalid.
     */
    private void createBillForOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(frame, "Please select an order first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int orderId = (int) orderTable.getModel().getValueAt(selectedRow, 0);

        Order order = orderBLL.findOrderById(orderId);
        if (order == null) {
            JOptionPane.showMessageDialog(frame, "Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = clientBLL.findClientById(order.getClientId());
        Product product = productBLL.findProductById(order.getProductId());
        if (client == null || product == null) {
            JOptionPane.showMessageDialog(frame, "Client or product not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalPrice = product.getPrice() * order.getQuantity();
        Bill bill = new Bill(0, order.getId(), client.getName(), product.getName(), order.getQuantity(), totalPrice);

        Bill createdBill = billBLL.createBill(bill);
        if (createdBill != null) {
            JOptionPane.showMessageDialog(frame, "Bill created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            selectedBill = createdBill;
            showSelectedOrderBill(selectedRow);
        } else {
            JOptionPane.showMessageDialog(frame, "Failed to create bill.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens a new window to create a new order.
     * User selects client, product and quantity.
     * Validates input and stock availability.
     */
    private void createNewOrder() {
        List<Client> clients = clientBLL.getClients();
        List<Product> products = productBLL.getProducts();

        JFrame orderFrame = new JFrame("Create New Order");
        orderFrame.setSize(500, 400);
        orderFrame.setLocationRelativeTo(frame);
        orderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<Client> clientComboBox = new JComboBox<>(clients.toArray(new Client[0]));
        JComboBox<Product> productComboBox = new JComboBox<>(products.toArray(new Product[0]));
        JTextField quantityField = new JTextField();

        mainPanel.add(createLabeledPanel("Select Client:", clientComboBox));
        mainPanel.add(createLabeledPanel("Select Product:", productComboBox));
        mainPanel.add(createLabeledPanel("Quantity:", quantityField));

        JButton createButton = new JButton("Create Order");
        JButton cancelButton = new JButton("Cancel");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        createButton.addActionListener(e -> {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(orderFrame, "Invalid quantity. Enter a positive integer.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client client = (Client) clientComboBox.getSelectedItem();
            Product product = (Product) productComboBox.getSelectedItem();

            if (client == null || product == null) {
                JOptionPane.showMessageDialog(orderFrame, "Please select both client and product.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (product.getStock() < quantity) {
                JOptionPane.showMessageDialog(orderFrame, "Not enough stock available.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = orderBLL.createOrder(client.getId(), product.getId(), quantity);
            if (success) {
                JOptionPane.showMessageDialog(orderFrame, "Order created successfully! You can now create a bill for this order.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
                orderFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(orderFrame, "Order could not be created.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> orderFrame.dispose());

        orderFrame.add(mainPanel);
        orderFrame.setVisible(true);
    }

    /**
     * Helper method to create a JPanel with a label and input component side by side.
     * @param labelText the text to show in the label
     * @param inputField the input component (like JComboBox, JTextField)
     * @return JPanel containing the label and input field
     */
    private JPanel createLabeledPanel(String labelText, JComponent inputField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        inputField.setPreferredSize(new Dimension(250, 25));
        panel.add(label);
        panel.add(inputField);
        return panel;
    }

    /**
     * Main method to start the application.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(OrderView::new);
    }
}
