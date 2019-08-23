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
import project.exception.IllegalEntityException;
import project.exception.ValidationException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of operations with Cars
 * @author Daniel Jurca
 */
public class CarManagerImpl implements CarManager {
    private JdbcTemplate jdbc;
    final static Logger log = LoggerFactory.getLogger(CarManagerImpl.class);

    public CarManagerImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    public void createCar(Car car) throws IllegalEntityException, ValidationException {
        log.debug("Creating car");
        if(car == null) {
            log.error("Car is null");
            throw new IllegalArgumentException("Car can't be null!");
        }
        if(car.getCarBrand() == null || car.getDescription() == null || car.getDailyPrice()== null) {
            log.error("Some of the properties are null");
            throw new ValidationException("Some of the properties are null!");
        }
        if(car.getId() != null) {
            log.error("Car already exists");
            throw new IllegalEntityException("Car already exists");
        }
        if(car.getDailyPrice().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Price of car is negative");
            throw new ValidationException("Price can't be lower than 0!");
        }
        if(car.getCarBrand().isEmpty()) {
            log.error("Car has no name");
            throw new ValidationException("Car Brand can't be empty!");
        }

        SimpleJdbcInsert insertCar = new SimpleJdbcInsert(jdbc)
                .withTableName("cars").usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("carBrand", car.getCarBrand())
                .addValue("description", car.getDescription())
                .addValue("dailyPrice", car.getDailyPrice());

        Number id = insertCar.executeAndReturnKey(parameters);
        car.setId(id.longValue());
        log.debug("Car created");
    }

    public Car getCarById(Long id) {
        log.debug("Finding car");
        if(id == null) {
            log.error("Id is null");
            throw new IllegalArgumentException("Id is null!");
        }

        try{
            log.debug("Car found");
            return jdbc.queryForObject("SELECT * FROM cars WHERE id=?", carMapper, id);
        }catch(EmptyResultDataAccessException e){
            log.error("Car not found");
            return null;
        }
    }

    public List<Car> findAllCars() {
        log.debug("Finding all cars");
        return jdbc.query("SELECT * FROM cars", carMapper);
    }


    public void updateCar(Car car) throws IllegalEntityException, ValidationException {
        log.debug("Updating car");
        if(car == null) {
            log.error("Car is null");
            throw new IllegalArgumentException("Car is null!");
        }
        if(car.getId() == null) {
            log.error("Id is null");
            throw new IllegalEntityException("Id can not be null!");
        }
        if(car.getCarBrand() == null || car.getDescription() == null || car.getDailyPrice()== null) {
            log.error("Some of the properties are null");
            throw new ValidationException("Some of the properties are null!");
        }

        if(jdbc.update("UPDATE cars set carBrand=?,description=?,dailyPrice=? where id=?",
                car.getCarBrand(), car.getDescription(), car.getDailyPrice(), car.getId()) == 0) {
            log.error("Car was not updated");
            throw new IllegalEntityException("Car was not updated!");
        }
        log.debug("Car updated");
    }

    public void deleteCar(Car car) throws IllegalEntityException {
        log.debug("Deleting car");
        if(car == null) {
            log.error("Car is null");
            throw new IllegalArgumentException("Car is null!");
        }
        if(car.getId() == null) {
            log.error("Id is null");
            throw new IllegalEntityException("Id can not be null!");
        }

        if(jdbc.update("DELETE FROM cars WHERE id=?", car.getId()) == 0){
            log.error("Car was not deleted");
            throw new IllegalEntityException("Car was not deleted");
        }
        log.debug("Car deleted");
    }

    private RowMapper<Car> carMapper = (rs, rowNum) ->
            new Car(rs.getLong("id"),
                    rs.getString("carBrand"),
                    rs.getString("description"),
                    rs.getBigDecimal("dailyPrice"));
}
