package project;

import project.exception.IllegalEntityException;
import project.exception.ValidationException;
import project.impl.Car;

import java.util.List;

/**
 * Interface with operations for cars
 * @author Daniel Jurca
 */
public interface CarManager {

    /**
     * Save car to database
     * daily price must be unsigned
     * @param car - car you want to add to database
     * @throws IllegalArgumentException when car is null
     * @throws IllegalEntityException when car already exists (same id)
     * @throws ValidationException when car has some null properties (brand, description, price)
     * @throws ValidationException when price of car is negative
     */
    void createCar(Car car) throws IllegalEntityException, ValidationException;

    /**
     * Find car in the database by id
     * @param id - id of car you want to find
     * @return car from database with given id
     * @throws IllegalArgumentException when given id is null.
     */
    Car getCarById(Long id);

    /**
     * Find all cars from database
     * @return list of cars in the database
     */
    List<Car> findAllCars();

    /**
     * Update car in the database
     * @param car - car with updated properties
     * @throws IllegalArgumentException when "updating" car is null
     * @throws IllegalEntityException when car has null id or car does not exists
     * @throws ValidationException when car brand or description is null
     */
    void updateCar(Car car) throws IllegalEntityException, ValidationException;

    /**
     * Delete car from the database
     * @param car - car you want to delete
     * @throws IllegalArgumentException when deleting car is null
     * @throws IllegalEntityException when car you want to delete has null id or car does not exists
     */
    void deleteCar(Car car) throws IllegalEntityException;
}
