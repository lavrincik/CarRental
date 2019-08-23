package project.carRentInterface;

import project.CarManager;
import project.Main;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;
import project.impl.Car;
import project.impl.CarManagerImpl;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Daniel Jurca
 */
public class CarTableModel extends AbstractTableModel {

    private List<Car> cars;
    private CarManager carManager;

    public CarTableModel(CarManager carManager) {
        this.carManager = carManager;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                cars = carManager.findAllCars();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }

    public void addCar(Car car) {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                try{
                    carManager.createCar(car);
                }catch(IllegalEntityException | ValidationException e){
                    e.printStackTrace();
                }
                cars = carManager.findAllCars();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        Car car = cars.get(rowIndex);
        switch (columnIndex) {
            case 0:
                car.setId((Long) value);
                break;
            case 1:
                car.setCarBrand((String) value);
                break;
            case 2:
                car.setDescription((String) value);
                break;
            case 3:
                car.setDailyPrice((BigDecimal) value);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {
                try{
                    carManager.updateCar(car);
                }catch(IllegalEntityException | ValidationException e){
                    e.printStackTrace();
                } finally {
                    cars = carManager.findAllCars();
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
            case 1:
            case 2:
            case 3:
                return true;
            case 0:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }


    @Override
    public int getRowCount() {
        return cars.size();
    }

    public Car getCarAt(int row) {return cars.get(row);}

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
            case 2:
                return String.class;
            case 3:
                return BigDecimal.class;
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
                return Main.local.getString("Car type");
            case 2:
                return Main.local.getString("Description");
            case 3:
                return Main.local.getString("Daily price");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Car car = cars.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return car.getId();
            case 1:
                return car.getCarBrand();
            case 2:
                return car.getDescription();
            case 3:
                return car.getDailyPrice();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void deleteCarsByRow(List<Integer> toDelete) {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected Void doInBackground() throws Exception {

                toDelete.sort(Collections.reverseOrder());
                for (int row : toDelete) {
                    try{
                        carManager.deleteCar(cars.get(row));
                    }catch(IllegalEntityException e){
                        e.printStackTrace();
                    }
                }
                cars = carManager.findAllCars();
                fireTableDataChanged();
                return null;
            }
        };
        worker.execute();
    }
}

