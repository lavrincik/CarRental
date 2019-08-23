package project.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import project.CarManager;
import project.CustomerManager;
import project.LeaseManager;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

/**
 * Implementation of operations with Leases
 * @author Daniel Jurca
 */
public class LeaseManagerImpl implements LeaseManager {
    private JdbcTemplate jdbc;
    private CustomerManager customerManager;
    private CarManager carManager;
    final static Logger log = LoggerFactory.getLogger(LeaseManagerImpl.class);

    public LeaseManagerImpl(DataSource dataSource, CustomerManager customerManager, CarManager carManager) {
        this.jdbc = new JdbcTemplate(dataSource);
        this.customerManager = customerManager;
        this.carManager = carManager;
    }

    public CarManager getCarManager() {
        return carManager;
    }

    public CustomerManager getCustomerManager() {
        return customerManager;
    }

    public void createLease(Lease lease) {
        log.debug("Creating new lease");
        if(lease == null) {
            log.error("Lease is null");
            throw new IllegalArgumentException("Lease can't be null!");
        }
        if(lease.getCustomer() == null || lease.getLeasedCar() == null || lease.getPrice()== null ||
                lease.getStart() == null || lease.getEnd() == null) {
            log.error("Some of the properties are null");
            throw new ValidationException("Some of the properties are null!");
        }
        if(lease.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Price is negative");
            throw new ValidationException("Price can not be negative!");
        }
        if(lease.getId() != null) {
            log.error("Id already exists");
            throw new IllegalEntityException("Lease already exists");
        }
        if(lease.getEnd().isBefore(lease.getStart())) throw new ValidationException("End of lease is before start!");

        SimpleJdbcInsert insertLease = new SimpleJdbcInsert(jdbc)
                .withTableName("leases").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("LEASEDCARID", lease.getLeasedCar().getId())
                .addValue("CUSTOMERID", lease.getCustomer().getId())
                .addValue("STARTDATE", Date.valueOf(lease.getStart()))
                .addValue("ENDDATE", Date.valueOf(lease.getEnd()))
                .addValue("PRICE", lease.getPrice());

        Number id = insertLease.executeAndReturnKey(parameters);
        lease.setId(id.longValue());
        log.debug("Lease created");
    }


    public Lease getLeaseById(Long id) {
        log.debug("Finding lease");
        if(id == null) {
            log.error("Id is null");
            throw new IllegalArgumentException("Id is null!");
        }
        try{
            log.debug("Lease found");
            return jdbc.queryForObject("SELECT * FROM leases WHERE id=?", leaseMapper, id);
        }catch(EmptyResultDataAccessException e){
            log.error("Lease not found");
            return null;
        }
    }

    public List<Lease> findAllLeases() {
        log.debug("Finding all leases");
        return jdbc.query("SELECT * FROM leases", leaseMapper);
    }

    public void updateLease(Lease lease) {
        log.debug("Updating lease");
        if(lease == null) {
            log.error("Lease is null");
            throw new IllegalArgumentException("Lease is null!");
        }
        if(lease.getId() == null) {
            log.error("Id is null");
            throw new IllegalEntityException("Id can not be null!");
        }
        if(lease.getCustomer() == null || lease.getLeasedCar() == null || lease.getPrice()== null ||
                lease.getStart() == null || lease.getEnd() == null) {
            log.error("Some of the properties are null");
            throw new ValidationException("Some of the properties are null!");
        }
        if(lease.getEnd().isBefore(lease.getStart())) throw new ValidationException("End of lease is before start!");
        if(jdbc.update("UPDATE leases set LEASEDCARID=?,CUSTOMERID=?,STARTDATE=?, ENDDATE=?,PRICE=? where id=?",
                lease.getLeasedCar().getId(), lease.getCustomer().getId(),
                Date.valueOf(lease.getStart()), Date.valueOf(lease.getEnd()), lease.getPrice(), lease.getId()) == 0) {
            log.error("Lease was not updated");
            throw new IllegalEntityException("Lease was not updated!");
        }
        log.debug("Lease updated");
    }

    public void deleteLease(Lease lease) {
        log.debug("Deleting lease");
        if(lease == null) {
            log.error("Lease is null");
            throw new IllegalArgumentException("Lease is null!");
        }
        if(lease.getId() == null) {
            log.error("Id is null");
            throw new IllegalEntityException("Id can not be null!");
        }

        if(jdbc.update("DELETE FROM leases WHERE id=?", lease.getId()) == 0){
            log.error("Lease was not deleted");
            throw new IllegalEntityException("Lease was not deleted");
        }
        log.debug("Lease deleted");
    }

    public List<Lease> findLeasesForCustomer(Customer customer) {
        log.debug("Finding all leases for customer");
        if(customer == null) {
            log.error("Customer is null");
            throw new IllegalArgumentException("Customer can not be null!");
        }
        if(customer.getId() == null) {
            log.error("Id is null");
            throw new IllegalEntityException("ID can not be null");
        }
        if(customerManager.getCustomerById(customer.getId()) == null) {
            log.error("Customer does not exists");
            return null;
        }
        log.debug("All leases found");
        return jdbc.query("SELECT * FROM leases WHERE CUSTOMERID=?", leaseMapper, customer.getId());
    }

    public List<Lease> findLeasesForCar(Car car) {
        log.debug("Finding all leases for car");
        if(car == null) {
            log.error("Car is null");
            throw new IllegalArgumentException("Car can not be null!");
        }
        if(car.getId() == null) {
            log.error("Id is null");
            throw new IllegalEntityException("ID can not be null");
        }
        if(carManager.getCarById(car.getId()) == null) {
            log.error("Car does not exists");
            return null;
        }
        log.debug("All leases found");
        return jdbc.query("SELECT * FROM leases WHERE leasedcarid=?", leaseMapper, car.getId());
    }

    public List<Car> findUnleasedCars() {
        log.debug("All unleased cars found");
        return jdbc.query("SELECT * FROM cars WHERE id NOT IN (SELECT leasedcarid FROM leases WHERE startdate <= CURRENT DATE AND enddate > CURRENT DATE)", carMapper);

    }

    public List<Car> findLeasedCars() {
        log.debug("All leased cars found");
        return jdbc.query("SELECT * FROM cars WHERE id IN (SELECT leasedcarid FROM leases WHERE startdate <= CURRENT DATE AND enddate > CURRENT DATE)", carMapper);
    }

    private RowMapper<Lease> leaseMapper = (rs, rowNum) ->
            new Lease(rs.getLong("id"),
                    customerManager.getCustomerById(rs.getLong("CUSTOMERID")),
                    carManager.getCarById(rs.getLong("LEASEDCARID")),
                    rs.getDate("STARTDATE").toLocalDate(),
                    rs.getDate("ENDDATE").toLocalDate(),
                    rs.getBigDecimal("PRICE"));

    private RowMapper<Car> carMapper = (rs, rowNum) ->
            new Car(rs.getLong("id"),
                    rs.getString("carBrand"),
                    rs.getString("description"),
                    rs.getBigDecimal("dailyPrice"));
}
