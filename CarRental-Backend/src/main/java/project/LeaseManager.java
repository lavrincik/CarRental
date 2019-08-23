package project;

import project.exception.IllegalEntityException;
import project.exception.ValidationException;
import project.impl.Car;
import project.impl.Customer;
import project.impl.Lease;

import java.util.List;

/**
 * Interface with operations for leases
 * @author Daniel Jurca
 */
public interface LeaseManager {

    /**
     * Save lease to database
     * price must be unsigned
     * @param lease - lease you want to add to database
     * @throws IllegalArgumentException when lease is null
     * @throws IllegalEntityException when lease already exists (same id)
     * @throws ValidationException when lease has some null properties or end is before start
     * @throws ValidationException when price of lease is negative
     */
    void createLease(Lease lease);

    /**
     * Find lease in the database by id
     * @param id - id of lease you want to find
     * @return lease from database with given id
     * @throws IllegalArgumentException when given id is null.
     */
    Lease getLeaseById(Long id);

    /**
     * Find all leases from database
     * @return list of leases in the database
     */
    List<Lease> findAllLeases();

    /**
     * Update lease in the database
     * @param lease - lease with updated properties
     * @throws IllegalArgumentException when "updating" lease is null
     * @throws IllegalEntityException when lease has null id or lease does not exists
     * @throws ValidationException when lease has some null properties
     */
    void updateLease(Lease lease);

    /**
     * Delete lease from the database
     * @param lease - lease you want to delete
     * @throws IllegalArgumentException when deleting lease is null
     * @throws IllegalEntityException when lease you want to delete has null id or lease does not exists
     */
    void deleteLease(Lease lease);

    /**
     * Find leases with given customer
     * @param customer - customer in leases you want to find
     * @return Collection of leases of the customer, null if customer does not exist
     * @throws IllegalArgumentException when customer is null
     * @throws IllegalEntityException when customers id is null
     *
     */
    List<Lease> findLeasesForCustomer(Customer customer);

    /**
     * Find leases with given car
     * @param car - car in leases you want to find
     * @return Collection of leases with the car, null if car does not exist
     * @throws IllegalArgumentException when car is null
     * @throws IllegalEntityException when cars id is null
     */
    List<Lease> findLeasesForCar(Car car);

    /**
     * Find unleased cars
     * @return Collection of unleased cars
     */
    List<Car> findUnleasedCars();

    /**
     * Find leased cars
     * @return Collection of leased cars
     */
    List<Car> findLeasedCars();

    CarManager getCarManager();

    CustomerManager getCustomerManager();
}

