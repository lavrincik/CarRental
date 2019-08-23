package project;

import org.springframework.cglib.core.Local;
import org.springframework.format.datetime.DateFormatter;
import project.carRentInterface.CarTableModel;
import project.carRentInterface.CustomerTableModel;
import project.carRentInterface.LeaseTableModel;
import project.impl.*;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;


/**
 * @author Daniel Jurca
 */

public class CarRentalForm {
    private JPanel mainPanel;
    private JTable carTable;
    private JTextField carTypeTextField;
    private JTextField descriptionTextField;
    private JFormattedTextField priceTextField;
    private JButton addCarButton;
    private JButton deleteCarButton;

    private JTable customerTable;
    private JTextField fullNameTextField;
    private JTextField addressTestField;
    private JTextField phoneTextField;
    private JButton addCustomerButton;
    private JButton deleteCustomerButton;

    private JTable leaseTable;
    private JFormattedTextField startTextField;
    private JFormattedTextField endTextField;
    private JButton addLeaseButton;
    private JButton deleteLeaseButton;
    public final static DateFormat dateFormatter =
            DateFormat.getDateInstance(DateFormat.SHORT, Main.local.getLocale());
    public final static DateTimeFormatter dateformat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Main.local.getLocale());

    public CarRentalForm() {
        CarTableModel carTableModel = (CarTableModel) carTable.getModel();

        addCarButton.addActionListener(e -> {
            String carBrand = carTypeTextField.getText();
            String description = descriptionTextField.getText();
            String price = priceTextField.getText();
            carTableModel.addCar(new Car(null, carBrand, description, new BigDecimal(price)));
        });

        deleteCarButton.addActionListener(e -> {
            int[] selectedRows = carTable.getSelectedRows();
            ArrayList<Integer> toDelete = new ArrayList<>();
            for (int selectedRow : selectedRows) {
                int rowIndex = carTable.convertRowIndexToModel(selectedRow);
                toDelete.add(rowIndex);
            }
            carTableModel.deleteCarsByRow(toDelete);
        });
        deleteCarButton.setEnabled(false);

        CustomerTableModel customerTableModel = ((CustomerTableModel) customerTable.getModel());

        addCustomerButton.addActionListener(e -> {
            String fullName = fullNameTextField.getText();
            String address = addressTestField.getText();
            String phoneNumber = phoneTextField.getText();
            Customer customer = new Customer(null, fullName, address, phoneNumber);
            customerTableModel.addCustomer(customer);
        });

        deleteCustomerButton.addActionListener(e -> {
            int[] selectedRows = customerTable.getSelectedRows();
            ArrayList<Integer> toDelete = new ArrayList<>();
            for (int selectedRow : selectedRows) {
                int rowIndex = customerTable.convertRowIndexToModel(selectedRow);
                toDelete.add(rowIndex);
            }
            customerTableModel.deleteCustomersByRow(toDelete);
        });
        deleteCustomerButton.setEnabled(false);


        LeaseTableModel leaseTableModel = ((LeaseTableModel) leaseTable.getModel());

        addLeaseButton.addActionListener(e -> {
            LocalDate start = LocalDate.parse(startTextField.getText(), CarRentalForm.dateformat);
            LocalDate end = LocalDate.parse(endTextField.getText(), CarRentalForm.dateformat);
            Customer customer = customerTableModel.getCustomerAt(customerTable.getSelectedRow());
            Car car = carTableModel.getCarAt(carTable.getSelectedRow());
            BigDecimal price = car.getDailyPrice();
            Lease lease = new Lease(null, customer, car, start, end, price);
            leaseTableModel.addLease(lease);
        });

        deleteLeaseButton.addActionListener(e -> {
            int[] selectedRows = leaseTable.getSelectedRows();
            ArrayList<Integer> toDelete = new ArrayList<>();
            for (int selectedRow : selectedRows) {
                int rowIndex = leaseTable.convertRowIndexToModel(selectedRow);
                toDelete.add(rowIndex);
            }
            leaseTableModel.deleteLeasesByRow(toDelete);
        });
        addLeaseButton.setEnabled(false);
        deleteLeaseButton.setEnabled(false);
    }

    public JPanel getTopPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        //DataSource dataSource = Main.createMemoryDatabase();
        DataSource dataSource = Main.connectDatabase();
        CarManager carManager = new CarManagerImpl(dataSource);
        CustomerManager customerManager = new CustomerManagerImpl(dataSource);
        LeaseManager leaseManager = new LeaseManagerImpl(dataSource, customerManager, carManager);

        carTable = new JTable();
        carTable.setModel(new CarTableModel(carManager));
        carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carTable.getSelectionModel().addListSelectionListener(e -> {
            deleteCarButton.setEnabled(carTable.getSelectedRowCount()>0);
            addLeaseButton.setEnabled(customerTable.getSelectedRowCount()>0 && carTable.getSelectedRowCount()>0);
        });
        carTable.getColumnModel().getColumn(0).setMinWidth(40);
        carTable.getColumnModel().getColumn(0).setMaxWidth(40);
        carTable.getColumnModel().getColumn(1).setMinWidth(200);
        carTable.getColumnModel().getColumn(2).setMinWidth(200);
        carTable.getColumnModel().getColumn(3).setMinWidth(100);
        carTable.getColumnModel().getColumn(3).setMaxWidth(100);

        //priceTextField = new JFormattedTextField(1000);
        //DefaultFormatter fmt = new NumberFormatter(new DecimalFormat("#.##"));
        //fmt.setValueClass(priceTextField.getValue().getClass());
        //DefaultFormatterFactory fmtFactory = new DefaultFormatterFactory(fmt, fmt, fmt);
        //priceTextField.setFormatterFactory(fmtFactory);

        DecimalFormat numberFormat = new DecimalFormat("#");
        priceTextField = new JFormattedTextField(numberFormat);

        customerTable = new JTable();
        customerTable.setModel(new CustomerTableModel(customerManager));
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            deleteCustomerButton.setEnabled(customerTable.getSelectedRowCount()>0);
            addLeaseButton.setEnabled(customerTable.getSelectedRowCount()>0 && carTable.getSelectedRowCount()>0);
        });
        customerTable.getColumnModel().getColumn(0).setMinWidth(40);
        customerTable.getColumnModel().getColumn(0).setMaxWidth(40);
        customerTable.getColumnModel().getColumn(1).setMinWidth(150);
        customerTable.getColumnModel().getColumn(2).setMinWidth(150);
        customerTable.getColumnModel().getColumn(3).setMinWidth(100);


        leaseTable = new JTable();
        leaseTable.setModel(new LeaseTableModel(leaseManager));
        leaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leaseTable.getSelectionModel().addListSelectionListener(e -> {
            deleteLeaseButton.setEnabled(leaseTable.getSelectedRowCount()>0);
        });


        leaseTable.getColumnModel().getColumn(0).setMinWidth(40);
        leaseTable.getColumnModel().getColumn(0).setMaxWidth(40);
        leaseTable.getColumnModel().getColumn(1).setMinWidth(150);
        leaseTable.getColumnModel().getColumn(2).setMinWidth(150);
        leaseTable.getColumnModel().getColumn(3).setMinWidth(80);
        leaseTable.getColumnModel().getColumn(4).setMinWidth(80);
        leaseTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(new JFormattedTextField(dateFormatter)));
        leaseTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JFormattedTextField(dateFormatter)));
        startTextField = new JFormattedTextField(dateFormatter);
        endTextField = new JFormattedTextField(dateFormatter);
        startTextField.setValue(Date.valueOf(LocalDate.now()));
        endTextField.setValue(Date.valueOf(LocalDate.now().plusDays(2)));
    }

}