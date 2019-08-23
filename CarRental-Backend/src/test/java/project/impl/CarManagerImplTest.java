package project.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import project.CarManager;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for Car Manager
 *
 * @author Daniel Jurca
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MySpringTestConfig.class})
@Transactional
public class CarManagerImplTest {

    @Autowired
    private CarManager manager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CarBuilder sampleBMWCarBuilder() {
        return new CarBuilder()
                .carBrand("BMW")
                .description("I'm german.")
                .dailyPrice(new BigDecimal(1350));
    }

    private CarBuilder sampleAstonMartinCarBuilder() {
        return new CarBuilder()
                .carBrand("Aston Martin")
                .description("James Bond is my father!")
                .dailyPrice(new BigDecimal(2000));
    }

    @Test
    public void createCar() {
        Car car = sampleBMWCarBuilder().build();
        manager.createCar(car);
        Long carId = car.getId();

        assertThat(carId).isNotNull();
        assertThat(manager.getCarById(carId))
                .isNotSameAs(car)
                .isEqualToComparingFieldByField(car);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullCar() {
        manager.createCar(null);
    }

    @Test
    public void createCarWithExistingId() {
        Car car = sampleBMWCarBuilder()
                .id(1L)
                .build();
        expectedException.expect(IllegalEntityException.class);
        manager.createCar(car);
    }

    @Test
    public void createCarWithNullCarBrand() {
        Car car = sampleBMWCarBuilder()
                .carBrand(null)
                .build();
        assertThatThrownBy(() -> manager.createCar(car))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCarWithEmptyCarBrand() {
        Car car = sampleBMWCarBuilder()
                .carBrand("")
                .build();
        assertThatThrownBy(() -> manager.createCar(car))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCarWithNullDailyPrice() {
        Car car = sampleBMWCarBuilder()
                .dailyPrice(null)
                .build();
        assertThatThrownBy(() -> manager.createCar(car))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCarWithNullDescription() {
        Car car = sampleBMWCarBuilder()
                .description(null)
                .build();
        assertThatThrownBy(() -> manager.createCar(car))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCarWithNegativePrice() {
        Car car = sampleBMWCarBuilder()
                .dailyPrice(new BigDecimal(-1))
                .build();
        assertThatThrownBy(() -> manager.createCar(car))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void getCarById() {
        Car car = sampleBMWCarBuilder().build();
        manager.createCar(car);
        Long carId = car.getId();

        assertThat(carId).isNotNull();
        assertThat(manager.getCarById(carId))
                .isEqualToComparingFieldByField(car);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCarWithNullId() {
        manager.getCarById(null);
    }

    @Test
    public void findAllCars() {
        assertThat(manager.findAllCars()).isEmpty();

        Car bmw = sampleBMWCarBuilder().build();
        Car astonMartin = sampleAstonMartinCarBuilder().build();

        manager.createCar(bmw);
        manager.createCar(astonMartin);

        assertThat(manager.findAllCars())
                .usingFieldByFieldElementComparator()
                .containsOnly(bmw,astonMartin);
    }

    @Test
    public void updateCarBrand() {
        Car carForUpdate = sampleBMWCarBuilder().build();
        Car anotherCar = sampleAstonMartinCarBuilder().build();
        manager.createCar(carForUpdate);
        manager.createCar(anotherCar);

        carForUpdate.setCarBrand("Alfa Romeo");

        manager.updateCar(carForUpdate);

        assertThat(manager.getCarById(carForUpdate.getId()))
                .isEqualToComparingFieldByField(carForUpdate);

        assertThat(manager.getCarById(anotherCar.getId()))
                .isEqualToComparingFieldByField(anotherCar);
    }

    @Test
    public void updateDescription() {
        Car carForUpdate = sampleBMWCarBuilder().build();
        Car anotherCar = sampleAstonMartinCarBuilder().build();
        manager.createCar(carForUpdate);
        manager.createCar(anotherCar);

        carForUpdate.setDescription("Something is updated!");

        manager.updateCar(carForUpdate);

        assertThat(manager.getCarById(carForUpdate.getId()))
                .isEqualToComparingFieldByField(carForUpdate);

        assertThat(manager.getCarById(anotherCar.getId()))
                .isEqualToComparingFieldByField(anotherCar);
    }

    @Test
    public void updateDailyPrice() {
        Car carForUpdate = sampleBMWCarBuilder().build();
        Car anotherCar = sampleAstonMartinCarBuilder().build();
        manager.createCar(carForUpdate);
        manager.createCar(anotherCar);

        carForUpdate.setDailyPrice(new BigDecimal(1800));
        manager.updateCar(carForUpdate);

        assertThat(manager.getCarById(carForUpdate.getId()))
                .isEqualToComparingFieldByField(carForUpdate);
        assertThat(manager.getCarById(anotherCar.getId()))
                .isEqualToComparingFieldByField(anotherCar);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullCar() {
        manager.updateCar(null);
    }

    @Test
    public void updateCarWithNullId() {
        Car car = sampleBMWCarBuilder()
                .id(null)
                .build();
        expectedException.expect(IllegalEntityException.class);
        manager.updateCar(car);
    }

    @Test
    public void updateNonExistingCar() {
        Car car = sampleBMWCarBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.updateCar(car);
    }

    @Test
    public void updateCarWithNullCarBrand() {
        Car car = sampleBMWCarBuilder().build();
        manager.createCar(car);
        car.setCarBrand(null);

        expectedException.expect(ValidationException.class);
        manager.updateCar(car);
    }

    @Test
    public void updateCarWithNullDescription() {
        Car car = sampleBMWCarBuilder().build();
        manager.createCar(car);
        car.setDescription(null);

        expectedException.expect(ValidationException.class);
        manager.updateCar(car);
    }

    @Test
    public void deleteCar() {
        Car bmw = sampleBMWCarBuilder().build();
        Car astonMartin = sampleAstonMartinCarBuilder().build();
        manager.createCar(bmw);
        manager.createCar(astonMartin);

        assertThat(manager.getCarById(bmw.getId())).isNotNull();
        assertThat(manager.getCarById(astonMartin.getId())).isNotNull();
        manager.deleteCar(bmw);

        assertThat(manager.getCarById(bmw.getId())).isNull();
        assertThat(manager.getCarById(astonMartin.getId())).isNotNull();
    }


    @Test(expected = IllegalArgumentException.class)
    public void deleteNullCar() {
        manager.deleteCar(null);
    }

    @Test
    public void deleteCarWithNullId() {
        Car car = sampleBMWCarBuilder().id(null).build();
        expectedException.expect(IllegalEntityException.class);
        manager.deleteCar(car);
    }

    @Test
    public void deleteNonExistingCar() {
        Car car = sampleBMWCarBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.deleteCar(car);
    }
}