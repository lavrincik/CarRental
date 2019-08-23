package project.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.transaction.annotation.Transactional;
import project.CustomerManager;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;

import javax.sql.DataSource;
import java.util.List;

/**
 *
 * @author Daniel Jurca
 */
public class CustomerManagerImpl implements CustomerManager {
    private JdbcTemplate jdbc;
    final static Logger log = LoggerFactory.getLogger(CustomerManagerImpl.class);

    public CustomerManagerImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    public void createCustomer(Customer customer) {
        log.debug("Creating customer");
        if(customer == null) {
            log.error("Customer is null");
            throw new IllegalArgumentException("Customer can't be null!");
        }
        if(customer.getFullName() == null || customer.getAddress() == null || customer.getPhoneNumber()== null) {
            log.error("Some of the properties are null");
            throw new ValidationException("Some of the properties are null!");
        }
        if(customer.getId() != null) {
            log.error("Customer already exists");
            throw new IllegalEntityException("Customer already exists");
        }
        if(customer.getFullName().isEmpty()|| customer.getAddress().isEmpty()|| customer.getPhoneNumber().isEmpty()) {
            log.error("Some of the properties are empty");
            throw new ValidationException("Some of the properties are empty!");
        }

        String phone = customer.getPhoneNumber();
        if (!phone.matches("[0-9]+")) {
            log.error("Wrong format of phone number");
            throw new ValidationException("Wrong format of phone number");
        }

        SimpleJdbcInsert insertCustomer = new SimpleJdbcInsert(jdbc)
                .withTableName("customers").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("fullName", customer.getFullName())
                .addValue("address", customer.getAddress())
                .addValue("phoneNumber", customer.getPhoneNumber());

        Number id = insertCustomer.executeAndReturnKey(parameters);
        customer.setId(id.longValue());
        log.debug("Customer created");
    }

    public Customer getCustomerById(Long id) {
        log.debug("Finding customer");
        if(id == null) {
            log.error("Id is null");
            throw new IllegalArgumentException("Id is null!");
        }
        try{
            log.debug("Customer found");
            return jdbc.queryForObject("SELECT * FROM customers WHERE id=?", customerMapper, id);
        }catch(EmptyResultDataAccessException e){
            log.error("Customer not found");
            return null;
        }
    }

    @Transactional
    public List<Customer> findAllCustomers() {
        log.debug("Finding all customers");
        return jdbc.query("SELECT * FROM customers", customerMapper);
    }

    public List<Customer> findCustomerByName(String fullName){
        log.debug("Finding Customer by name");
        if(fullName == null) {
            log.error("Customer's name is null");
            throw new IllegalArgumentException("Name is null!");
        }
        log.debug("Customer found");
        return jdbc.query("SELECT * FROM customers WHERE fullname=?", customerMapper, fullName);
    }

    public void updateCustomer(Customer customer) {
        log.debug("Updating customer");
        if(customer == null) {
            log.error("Customer is null");
            throw new IllegalArgumentException("Customer can not be null!");
        }
        if(customer.getId() == null) {
            log.error("Id is null");
            throw new IllegalEntityException("Id can not be null!");
        }
        if(customer.getFullName() == null || customer.getAddress() == null || customer.getPhoneNumber()== null) {
            log.error("Some of the properties are null");
            throw new ValidationException("Some of the properties are null!");
        }
        if(customer.getFullName().isEmpty()|| customer.getAddress().isEmpty()|| customer.getPhoneNumber().isEmpty()) {
            log.error("Some of the properties are empty");
            throw new ValidationException("Some of the properties are empty!");
        }
        String phone = customer.getPhoneNumber();
        if (!phone.matches("[0-9]+")) {
            log.error("Wrong format of phone number");
            throw new ValidationException("Wrong format of phone number");
        }

        if(jdbc.update("UPDATE customers set fullName=?,address=?,phoneNumber=? where id=?",
                customer.getFullName(), customer.getAddress(), customer.getPhoneNumber(), customer.getId()) == 0){
            log.error("Customer does not exist");
            throw new IllegalEntityException("Customer doesn't exist!");
        }
        log.debug("Customer updated");
    }

    public void deleteCustomer(Customer customer) {
        log.debug("Deleting customer");
        if(customer == null) {
            log.error("Customer is null");
            throw new IllegalArgumentException("Customer can not be null!");
        }
        if(customer.getId() == null) {
            log.error("Id is null");
            throw new IllegalEntityException("Id can not be null!");
        }

        if(jdbc.update("DELETE FROM customers WHERE id=?", customer.getId())== 0) {
            log.error("Customer does not exist");
            throw new IllegalEntityException("Non existing customer!");
        }
        log.debug("Customer deleted");
    }

    private RowMapper<Customer> customerMapper = (rs, rowNum) ->
            new Customer(rs.getLong("id"),
                    rs.getString("fullName"),
                    rs.getString("address"),
                    rs.getString("phoneNumber"));
}
