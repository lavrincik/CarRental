package project.impl;

import java.math.BigDecimal;

/**
 * This is a car builder for tests
 * @author Daniel Jurca
 */
public class CarBuilder {
    private String carBrand;
    private Long id;
    private String description;
    private BigDecimal dailyPrice;

    public CarBuilder carBrand(String carBrand) {
        this.carBrand = carBrand;
        return this;
    }

    public CarBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CarBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CarBuilder dailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
        return this;
    }

    /**
     * Creates new instance of Car with given properties.
     *
     * @return new instance of Car with given properties.
     */
    public Car build() {
        Car car = new Car();
        car.setCarBrand(carBrand);
        car.setId(id);
        car.setDescription(description);
        car.setDailyPrice(dailyPrice);
        return car;
    }
}
