package Presentation;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 * Utility class to build a table model from a list of objects.
 */
public class TableUtils {

    /**
     * Converts a list of objects into a DefaultTableModel for use in JTable.
     * Each property of the object's class becomes a column in the table.
     *
     * @param <T> The type of objects in the list.
     * @param objects The list of objects to convert into a table model.
     * @return A DefaultTableModel containing the object data in rows and columns.
     */
    public static <T> DefaultTableModel buildTableModelFromList(List<T> objects) {
        DefaultTableModel model = new DefaultTableModel();

        if (objects == null || objects.isEmpty()) {
            return model;
        }

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(objects.get(0).getClass());
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                if (!"class".equals(pd.getName())) {
                    model.addColumn(pd.getName());
                }
            }

            for (T obj : objects) {
                Object[] rowData = new Object[model.getColumnCount()];
                int idx = 0;
                for (PropertyDescriptor pd : pds) {
                    if (!"class".equals(pd.getName())) {
                        Method readMethod = pd.getReadMethod();
                        if (readMethod != null) {
                            rowData[idx++] = readMethod.invoke(obj);
                        }
                    }
                }
                model.addRow(rowData);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        return model;
    }
}
