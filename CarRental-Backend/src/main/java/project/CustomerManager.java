package project;

import project.impl.Customer;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;

import java.util.List;

/**
 * Interface with operations for customers
 * @author Marek Lavrincik
 */
public interface CustomerManager {

    /**
     * Save customer to database
     *
     * @param customer - customer you want to add to database
     * @throws IllegalArgumentException when customer is null
     * @throws IllegalEntityException when customer has already assigned id
     * @throws ValidationException when customer has some null or empty properties (fullName, address, phoneNumber)
     */
    void createCustomer(Customer customer) throws IllegalEntityException, ValidationException;

    /**
     * Find customer in database by id
     *
     * @param id - id of the customer you want to find
     * @throws IllegalArgumentException when given id is null
     * @return customer from database by given id
     */
    Customer getCustomerById(Long id);

    /**
     * Returns a list of all customers in database
     *
     * @return list of all customers in database
     */
    List<Customer> findAllCustomers();

    /**
     * Returns a list of all customers in database by fullName
     *
     * @param fullName - full name of customers you want to find
     * @return list of all customers in database by given fullName
     */
    List<Customer> findCustomerByName(String fullName);

    /**
     * Updates customer in database
     *
     * @param customer - updated customer to be stored into database
     * @throws IllegalArgumentException when customer is null
     * @throws IllegalEntityException when customer has null id or does not exist in database
     * @throws ValidationException when customer has some null or empty properties (fullName, address, phoneNumber)
     */
    void updateCustomer(Customer customer) throws IllegalEntityException, ValidationException;

    /**
     * Deletes customer from database
     *
     * @param customer - customer to be deleted from database
     * @throws IllegalArgumentException when customer is null
     * @throws IllegalEntityException when customer has null id or does not exist in database
     */
    void deleteCustomer(Customer customer) throws IllegalEntityException;
}
