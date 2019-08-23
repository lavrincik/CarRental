package project.impl;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author Daniel Jurca
 */
public class Car {
    private String carBrand;
    private Long id;
    private String description;
    private BigDecimal dailyPrice;

    public Car() {
    }

    public Car(Long id, String carBrand, String description, BigDecimal dailyPrice) {
        this.carBrand = carBrand;
        this.id = id;
        this.description = description;
        this.dailyPrice = dailyPrice;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Car{");
        sb.append("carBrand='").append(carBrand).append('\'');
        sb.append(", id=").append(id);
        sb.append(", description='").append(description).append('\'');
        sb.append(", dailyPrice=").append(dailyPrice);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Car car = (Car) o;

        if (carBrand != null ? !carBrand.equals(car.carBrand) : car.carBrand != null) return false;
        if (id != null ? !id.equals(car.id) : car.id != null) return false;
        if (description != null ? !description.equals(car.description) : car.description != null) return false;
        return dailyPrice != null ? dailyPrice.equals(car.dailyPrice) : car.dailyPrice == null;
    }

    @Override
    public int hashCode() {
        int result = carBrand != null ? carBrand.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (dailyPrice != null ? dailyPrice.hashCode() : 0);
        return result;
    }
}
