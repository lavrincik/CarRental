package project.carRentInterface;

import project.CarRentalForm;
import project.LeaseManager;
import project.Main;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;
import project.impl.Car;
import project.impl.Customer;
import project.impl.Lease;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * @author Daniel Jurca
 */
public class LeaseTableModel extends AbstractTableModel {
    private List<Lease> leases;
    private LeaseManager leaseManager;

    public LeaseTableModel(LeaseManager leaseManager) {
        this.leaseManager = leaseManager;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                leases = leaseManager.findAllLeases();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }

    public void addLease(Lease lease) {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                try{
                    leaseManager.createLease(lease);
                }catch(IllegalEntityException | ValidationException e){
                    e.printStackTrace();
                }
                leases = leaseManager.findAllLeases();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Lease lease = leases.get(rowIndex);
        switch (columnIndex) {
            case 0:
                lease.setId((Long) value);
                break;
            case 1:
                lease.setCustomer((Customer) value);
                break;
            case 2:
                lease.setLeasedCar((Car) value);
                break;
            case 3:
                lease.setStart(LocalDate.parse((String) value, CarRentalForm.dateformat));
                break;
            case 4:
                lease.setEnd(LocalDate.parse((String) value, CarRentalForm.dateformat));
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                try{
                    leaseManager.updateLease(lease);
                }catch(IllegalEntityException | ValidationException e){
                    e.printStackTrace();
                } finally {
                    leases = leaseManager.findAllLeases();
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
            case 1:
            case 2:
                return false;
            case 3:
            case 4:
                return true;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }


    @Override
    public int getRowCount() {
        return leases.size();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return Customer.class;
            case 2:
                return Car.class;
            case 3:
            case 4:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Main.local.getString("Id");
            case 1:
                return Main.local.getString("Customer");
            case 2:
                return Main.local.getString("Leased car");
            case 3:
                return Main.local.getString("Start date");
            case 4:
                return Main.local.getString("End date");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Lease lease = leases.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return lease.getId();
            case 1:
                return lease.getCustomer().getFullName();
            case 2:
                return lease.getLeasedCar().getCarBrand();
            case 3:
                return lease.getStart().format(CarRentalForm.dateformat);//LocalDate.parse();//.format(DateTimeFormatter.ofPattern("d MMM uuuu"));
            case 4:
                return lease.getEnd().format(CarRentalForm.dateformat);//.format(DateTimeFormatter.ofPattern("d MMM uuuu"));
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }


    public void deleteLeasesByRow(List<Integer> toDelete) {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {

                toDelete.sort(Collections.reverseOrder());
                for (int row : toDelete) {
                    try{
                        leaseManager.deleteLease(leases.get(row));
                    }catch(IllegalEntityException e){
                        e.printStackTrace();
                    }
                }
                leases = leaseManager.findAllLeases();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }
}
