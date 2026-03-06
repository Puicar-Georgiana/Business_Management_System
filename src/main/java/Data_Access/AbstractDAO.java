package Data_Access;

import Connection.ConnectionFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generic abstract Data Access Object that provides CRUD operations
 *
 * @param <T> the type of object to manage
 */
public abstract class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type;

    /**
     * Constructor that infers the actual type parameter at runtime.
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * @return the name of the table, assumed to match the simple class name
     */
    protected String getTableName() {
        return type.getSimpleName();
    }

    private String createSelectQuery(String field) {
        return "SELECT * FROM `" + getTableName() + "` WHERE " + field + " = ?";
    }

    /**
     * Retrieves all records from the corresponding table.
     *
     * @return a list of objects of type T
     */
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> results = null;

        try {
            String query = "SELECT * FROM `" + getTableName() + "`";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            results = createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return results;
    }

    /**
     * Finds a record by its primary key (id).
     *
     * @param id the id to search for
     * @return an object of type T or null if not found
     */
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        T result = null;

        try {
            String query = createSelectQuery("id");
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            List<T> results = createObjects(resultSet);
            if (results != null && !results.isEmpty()) {
                result = results.get(0);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return result;
    }

    /**
     * Creates a list of objects of type T from a result set.
     *
     * @param resultSet the SQL result set
     * @return a list of objects
     */
    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        Constructor<T> constructor = null;

        if (type.isRecord()) {
            try {
                constructor = (Constructor<T>) type.getConstructors()[0];
            } catch (SecurityException e) {
                LOGGER.log(Level.SEVERE, "Cannot access constructor for record: " + type.getName());
                return list;
            }
        } else {
            for (Constructor<?> ctor : type.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == 0) {
                    constructor = (Constructor<T>) ctor;
                    break;
                }
            }

            if (constructor == null) {
                LOGGER.log(Level.SEVERE, "No no-arg constructor found for " + type.getName());
                return list;
            }
        }

        try {
            while (resultSet.next()) {
                T instance;

                if (type.isRecord()) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    Object[] parameters = new Object[parameterTypes.length];

                    for (int i = 0; i < parameterTypes.length; i++) {
                        Class<?> paramType = parameterTypes[i];
                        String paramName = constructor.getParameters()[i].getName();
                        Object value = getFieldValue(resultSet, paramName, paramType);
                        parameters[i] = convertValue(paramType, value);
                    }

                    instance = constructor.newInstance(parameters);
                } else {
                    constructor.setAccessible(true);
                    instance = constructor.newInstance();

                    for (Field field : type.getDeclaredFields()) {
                        String fieldName = field.getName();
                        Class<?> fieldType = field.getType();
                        Object value = getFieldValue(resultSet, fieldName, fieldType);
                        value = convertValue(fieldType, value);

                        if (value != null) {
                            PropertyDescriptor pd = new PropertyDescriptor(fieldName, type);
                            Method setter = pd.getWriteMethod();
                            setter.invoke(instance, value);
                        }
                    }
                }

                list.add(instance);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating object: " + e.getMessage(), e);
        }

        return list;
    }

    private Object getFieldValue(ResultSet resultSet, String fieldName, Class<?> fieldType) throws SQLException {
        if (fieldType == int.class || fieldType == Integer.class) {
            return resultSet.getInt(fieldName);
        } else if (fieldType == double.class || fieldType == Double.class) {
            return resultSet.getDouble(fieldName);
        } else if (fieldType == String.class) {
            return resultSet.getString(fieldName);
        } else if (fieldType == java.sql.Date.class) {
            return resultSet.getDate(fieldName);
        } else if (fieldType == long.class || fieldType == Long.class) {
            return resultSet.getLong(fieldName);
        } else {
            return resultSet.getObject(fieldName);
        }
    }

    private Object convertValue(Class<?> targetType, Object value) {
        if (value == null) return null;

        if (targetType == int.class || targetType == Integer.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        } else if (targetType == double.class || targetType == Double.class) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        } else if (targetType == long.class || targetType == Long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
        } else if (targetType == java.sql.Date.class && value instanceof Timestamp) {
            return new java.sql.Date(((Timestamp) value).getTime());
        }

        return value;
    }

    /**
     * Inserts a new object into the database.
     * Not supported for records.
     *
     * @param t the object to insert
     * @return the object with the generated ID (if applicable)
     * @throws SQLException in case of database error
     */
    public T insert(T t) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        if (type.isRecord()) {
            LOGGER.log(Level.SEVERE, "Insert operation not supported for records: " + type.getName());
            return t;
        }

        StringBuilder fieldsSb = new StringBuilder();
        StringBuilder valuesSb = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.getName().equalsIgnoreCase("id")) {
                fieldsSb.append(field.getName()).append(",");
                valuesSb.append("?,");
                try {
                    values.add(field.get(t));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }

        if (fieldsSb.length() > 0) fieldsSb.setLength(fieldsSb.length() - 1);
        if (valuesSb.length() > 0) valuesSb.setLength(valuesSb.length() - 1);

        String query = "INSERT INTO `" + getTableName() + "` (" + fieldsSb + ") VALUES (" + valuesSb + ")";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }

            statement.executeUpdate();
            rs = statement.getGeneratedKeys();

            if (rs.next()) {
                int generatedId = rs.getInt(1);
                Field idField = type.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(t, generatedId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(rs);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return t;
    }

    /**
     * Updates an existing object in the database.
     * Not supported for records.
     *
     * @param t the object to update
     * @return the updated object
     */
    public T update(T t) {
        if (type.isRecord()) {
            LOGGER.log(Level.SEVERE, "Update operation not supported for records: " + type.getName());
            return t;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        StringBuilder querySb = new StringBuilder();
        List<Object> values = new ArrayList<>();
        Object idValue = null;

        querySb.append("UPDATE `").append(getTableName()).append("` SET ");

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(t);
                if (!field.getName().equalsIgnoreCase("id")) {
                    querySb.append(field.getName()).append(" = ?, ");
                    values.add(value);
                } else {
                    idValue = value;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        querySb.setLength(querySb.length() - 2);
        querySb.append(" WHERE id = ?");

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(querySb.toString());

            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i + 1, values.get(i));
            }
            statement.setObject(values.size() + 1, idValue);

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }

        return t;
    }

    /**
     * Deletes a given object from the database based on its ID.
     * Not supported for records.
     *
     * @param t the object to delete
     */
    public void delete(T t) {
        if (type.isRecord()) {
            LOGGER.log(Level.SEVERE, "Delete operation not supported for records: " + type.getName());
            return;
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            String query = "DELETE FROM `" + getTableName() + "` WHERE id = ?";
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            Field idField = type.getDeclaredField("id");
            idField.setAccessible(true);
            statement.setInt(1, idField.getInt(t));
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }
}
