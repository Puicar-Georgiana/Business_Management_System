package Presentation;

import BusinessLogic.ClientBLL;
import Model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * The ClientView class provides a GUI for managing clients.
 * It allows users to add, edit, delete, and view client information.
 */
public class ClientView implements ActionListener {
    private JFrame frame;
    private JPanel panel;
    private JTable clientTable;
    private JButton add, edit, delete, refresh, back;
    private ClientBLL clientBLL;

    /**
     * Constructs the client management window with all GUI components and event listeners.
     */
    public ClientView() {
        clientBLL = new ClientBLL();
        frame = new JFrame("Client Management");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        clientTable = new JTable();
        refreshTable();

        add = new JButton("Add Client");
        edit = new JButton("Edit Client");
        delete = new JButton("Delete Client");
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

        panel.add(new JScrollPane(clientTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Refreshes the client table with the current list of clients from the database.
     */
    private void refreshTable() {
        List<Client> clients = clientBLL.getClients();
        DefaultTableModel model = Presentation.TableUtils.buildTableModelFromList(clients);
        clientTable.setModel(model);
    }

    /**
     * Opens dialogs to input new client data and adds the client to the database.
     */
    private void addClient() {
        String name = JOptionPane.showInputDialog("Enter client name:");
        String address = JOptionPane.showInputDialog("Enter client address:");
        String email = JOptionPane.showInputDialog("Enter client email:");
        String phone = JOptionPane.showInputDialog("Enter client phone:");

        List<String> inputs = Arrays.asList(name, address, email, phone);
        if (inputs.stream().allMatch(input -> input != null && !input.isEmpty())) {
            Client client = new Client();
            client.setName(name);
            client.setAddress(address);
            client.setEmail(email);
            client.setPhone(phone);

            clientBLL.addClient(client);
            refreshTable();
            JOptionPane.showMessageDialog(frame, "Client added successfully!");
        }
    }

    /**
     * Edits the selected client by showing dialogs for new values and updating the database.
     */
    private void editClient() {
        int row = clientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a client to edit.");
            return;
        }

        try {
            int idColumnIndex = clientTable.getColumnModel().getColumnIndex("id");
            int clientId = Integer.parseInt(clientTable.getValueAt(row, idColumnIndex).toString());

            String name = JOptionPane.showInputDialog("Enter new name:", clientTable.getValueAt(row, clientTable.getColumnModel().getColumnIndex("name")));
            String address = JOptionPane.showInputDialog("Enter new address:", clientTable.getValueAt(row, clientTable.getColumnModel().getColumnIndex("address")));
            String email = JOptionPane.showInputDialog("Enter new email:", clientTable.getValueAt(row, clientTable.getColumnModel().getColumnIndex("email")));
            String phone = JOptionPane.showInputDialog("Enter new phone:", clientTable.getValueAt(row, clientTable.getColumnModel().getColumnIndex("phone")));

            List<String> inputs = Arrays.asList(name, address, email, phone);
            if (inputs.stream().allMatch(input -> input != null)) {
                Client client = new Client();
                client.setId(clientId);
                client.setName(name);
                client.setAddress(address);
                client.setEmail(email);
                client.setPhone(phone);

                clientBLL.updateClient(client);
                refreshTable();
                JOptionPane.showMessageDialog(frame, "Client updated successfully!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Error parsing client ID.");
            ex.printStackTrace();
        }
    }

    /**
     * Deletes the selected client from the database after confirmation.
     */
    private void deleteClient() {
        int row = clientTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a client to delete.");
            return;
        }

        int idColumnIndex = clientTable.getColumnModel().getColumnIndex("id");
        int clientId = Integer.parseInt(clientTable.getValueAt(row, idColumnIndex).toString());

        int confirmation = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to delete this client?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            clientBLL.deleteClient(clientId);
            refreshTable();
            JOptionPane.showMessageDialog(frame, "Client deleted successfully!");
        }
    }

    /**
     * Handles button click events for add, edit, delete, refresh, and back actions.
     *
     * @param e the ActionEvent triggered by user interaction
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            addClient();
        } else if (e.getSource() == edit) {
            editClient();
        } else if (e.getSource() == delete) {
            deleteClient();
        } else if (e.getSource() == refresh) {
            refreshTable();
        } else if (e.getSource() == back) {
            frame.dispose();
        }
    }

    /**
     * The main method to start the client management interface.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientView::new);
    }
}
