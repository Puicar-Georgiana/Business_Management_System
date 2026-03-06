package Data_Access;

import Model.Bill;
import Connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for handling operations related to bills.
 * This class provides methods to insert a bill and retrieve bills from the database.
 */
public class BillDAO {
    private static final String INSERT_BILL_SQL = "INSERT INTO Log (orderId, clientName, productName, quantity, totalPrice) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_ALL_BILLS_SQL = "SELECT * FROM Log";
    private static final String FIND_BILL_BY_ORDER_ID_SQL = "SELECT * FROM Log WHERE orderId = ?";

    /**
     * Inserts a new bill into the database.
     *
     * @param bill the Bill object to be inserted
     * @return the inserted Bill object with generated ID, or null if insertion failed
     */
    public Bill insert(Bill bill) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(INSERT_BILL_SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, bill.orderId());
            statement.setString(2, bill.clientName());
            statement.setString(3, bill.productName());
            statement.setInt(4, bill.quantity());
            statement.setDouble(5, bill.totalPrice());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return new Bill(generatedId, bill.orderId(), bill.clientName(), bill.productName(), bill.quantity(), bill.totalPrice());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(generatedKeys);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return null;
    }

    /**
     * Retrieves all bills from the database.
     *
     * @return a list of Bill objects
     */
    public List<Bill> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Bill> bills = new ArrayList<>();

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(FIND_ALL_BILLS_SQL);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int orderId = resultSet.getInt("orderId");
                String clientName = resultSet.getString("clientName");
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                double totalPrice = resultSet.getDouble("totalPrice");

                bills.add(new Bill(id, orderId, clientName, productName, quantity, totalPrice));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return bills;
    }

    /**
     * Finds a bill by its order ID.
     *
     * @param orderId the order ID to search for
     * @return the matching Bill object, or null if not found
     */
    public Bill findByOrderId(int orderId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(FIND_BILL_BY_ORDER_ID_SQL);
            statement.setInt(1, orderId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String clientName = resultSet.getString("clientName");
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                double totalPrice = resultSet.getDouble("totalPrice");

                return new Bill(id, orderId, clientName, productName, quantity, totalPrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return null;
    }
}
