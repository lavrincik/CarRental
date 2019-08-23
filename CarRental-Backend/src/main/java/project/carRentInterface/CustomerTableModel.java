package project.carRentInterface;

import project.CustomerManager;
import project.Main;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;
import project.impl.Customer;
import project.impl.CustomerManagerImpl;
import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;


/**
 * @author Daniel Jurca
 */
public class CustomerTableModel extends AbstractTableModel {

    private List<Customer> customers;
    private CustomerManager customerManager;

    public CustomerTableModel(CustomerManager customerManager) {
        this.customerManager = customerManager;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                customers = customerManager.findAllCustomers();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }

    public void addCustomer(Customer customer) {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                try{
                    customerManager.createCustomer(customer);
                }catch(IllegalEntityException | ValidationException e){
                    e.printStackTrace();
                }
                customers = customerManager.findAllCustomers();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                customer.setId((Long) value);
                break;
            case 1:
                customer.setFullName((String) value);
                break;
            case 2:
                customer.setAddress((String) value);
                break;
            case 3:
                customer.setPhoneNumber((String) value);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                try{
                    customerManager.updateCustomer(customer);
                }catch(IllegalEntityException | ValidationException e){
                    e.printStackTrace();
                } finally {
                    customers = customerManager.findAllCustomers();
                    fireTableDataChanged();
                }
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
            case 2:
            case 3:
                return true;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }


    @Override
    public int getRowCount() {
        return customers.size();
    }

    public Customer getCustomerAt(int row) {return customers.get(row);}

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
            case 2:
            case 3:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Main.local.getString("Id");
            case 1:
                return Main.local.getString("Full name");
            case 2:
                return Main.local.getString("Address");
            case 3:
                return Main.local.getString("Phone number");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer customer = customers.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return customer.getId();
            case 1:
                return customer.getFullName();
            case 2:
                return customer.getAddress();
            case 3:
                return customer.getPhoneNumber();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void deleteCustomersByRow(List<Integer> toDelete) {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {

                toDelete.sort(Collections.reverseOrder());
                for (int row : toDelete) {
                    try{
                        customerManager.deleteCustomer(customers.get(row));
                    }catch(IllegalEntityException e){
                        e.printStackTrace();
                    }
                }
                customers = customerManager.findAllCustomers();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }
}