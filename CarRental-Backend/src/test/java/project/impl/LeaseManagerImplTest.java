package project.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import project.CarManager;
import project.CustomerManager;
import project.LeaseManager;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * These tests are for Lease Manager
 * @author Daniel Jurca
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MySpringTestConfig.class})
@Transactional
public class LeaseManagerImplTest {
    @Autowired
    private LeaseManager manager;

    private CarManager carManager;
    private CustomerManager customerManager;

    @Before
    public void setUp() {
        carManager = manager.getCarManager();
        customerManager = manager.getCustomerManager();
    }

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

    private CustomerBuilder sampleJamesCustomerBuilder() {
        return new CustomerBuilder()
                .fullName("James Bond")
                .address("tajemna 762 10")
                .phoneNumber("007007007");
    }

    private LeaseBuilder sampleLeaseFirstLeaseBuilder() {
        Customer customer = sampleJamesCustomerBuilder().build();
        customerManager.createCustomer(customer);
        Car car = sampleAstonMartinCarBuilder().build();
        carManager.createCar(car);
        return new LeaseBuilder()
                .customer(customer)
                .leasedCar(car)
                .start(LocalDate.of(2018, 2, 10))
                .end(LocalDate.of(2018, 3, 25))
                .price(new BigDecimal(1350));
    }


    private LeaseBuilder sampleLeaseSecondLeaseBuilder() {
        Car car = sampleBMWCarBuilder().build();
        carManager.createCar(car);
        Customer customer = sampleJamesCustomerBuilder().build();
        customerManager.createCustomer(customer);
        return new LeaseBuilder()
                .customer(customer)
                .leasedCar(car)
                .start(LocalDate.of(2018, 1, 1))
                .end(LocalDate.of(2018, 1, 30))
                .price(new BigDecimal(60000));
    }


    @Test
    public void createLease() {
        Lease lease = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(lease);
        Long leaseId = lease.getId();

        assertThat(leaseId).isNotNull();
        assertThat(manager.getLeaseById(leaseId))
                .isNotSameAs(lease)
                .isEqualToComparingFieldByField(lease);
    }


    @Test(expected = IllegalArgumentException.class)
    public void createNullLease() {
        manager.createLease(null);
    }

    @Test
    public void createLeaseWithExistingId() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .id(1L)
                .build();
        expectedException.expect(IllegalEntityException.class);
        manager.createLease(lease);
    }

    @Test
    public void createLeaseWithNullCustomer() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .customer(null)
                .build();
        assertThatThrownBy(() -> manager.createLease(lease))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createLeaseWithNullLeasedCar() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .leasedCar(null)
                .build();
        assertThatThrownBy(() -> manager.createLease(lease))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createLeaseWithNullPrice() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .price(null)
                .build();
        assertThatThrownBy(() -> manager.createLease(lease))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createLeaseWithNegativePrice() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .price(new BigDecimal(-1))
                .build();
        assertThatThrownBy(() -> manager.createLease(lease))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createLeaseWithNullStart() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .start(null)
                .build();
        assertThatThrownBy(() -> manager.createLease(lease))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createLeaseWithNullEnd() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .end(null)
                .build();
        assertThatThrownBy(() -> manager.createLease(lease))
                .isInstanceOf(ValidationException.class);
    }


    @Test
    public void createLeaseEndBeforeStart() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .start(LocalDate.of(2010,12,6))
                .end(LocalDate.of(2010,12,2))
                .build();
        expectedException.expect(ValidationException.class);
        manager.createLease(lease);
    }


    @Test
    public void createLeaseSameDay() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .start(LocalDate.of(2010,12,21))
                .end(LocalDate.of(2010,12,21))
                .build();
        manager.createLease(lease);

        assertThat(manager.getLeaseById(lease.getId()))
                .isNotNull()
                .isEqualToComparingFieldByField(lease);
    }


    @Test
    public void getLeaseById() {
        Lease lease = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(lease);
        Long leaseId = lease.getId();

        assertThat(leaseId).isNotNull();
        assertThat(manager.getLeaseById(leaseId))
                .isEqualToComparingFieldByField(lease);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLeaseWithNullId() {
        manager.getLeaseById(null);
    }


    @Test
    public void findAllLeases() {
        assertThat(manager.findAllLeases()).isEmpty();

        Car bmw = sampleBMWCarBuilder().build();
        Car astonMartin = sampleAstonMartinCarBuilder().build();

        carManager.createCar(bmw);
        carManager.createCar(astonMartin);

        Lease leaseFirst = sampleLeaseFirstLeaseBuilder()
                .leasedCar(bmw)
                .build();
        Lease leaseSecond = sampleLeaseFirstLeaseBuilder()
                .leasedCar(astonMartin)
                .start(LocalDate.of(2006,10,30))
                .end(LocalDate.of(2019, 9, 15))
                .build();

        manager.createLease(leaseFirst);
        manager.createLease(leaseSecond);

        assertThat(manager.findAllLeases())
                .usingFieldByFieldElementComparator()
                .containsOnly(leaseFirst,leaseSecond);
    }



    @Test
    public void updateCustomer() {
        Lease leaseForUpdate = sampleLeaseFirstLeaseBuilder().build();
        Customer james = sampleJamesCustomerBuilder().build();
        manager.createLease(leaseForUpdate);
        customerManager.createCustomer(james);

        leaseForUpdate.setCustomer(james);

        manager.updateLease(leaseForUpdate);

        assertThat(manager.getLeaseById(leaseForUpdate.getId()).getCustomer())
                .isEqualToComparingFieldByField(leaseForUpdate.getCustomer());
    }

    @Test
    public void updateLeasedCar() {
        Lease leaseForUpdate = sampleLeaseSecondLeaseBuilder().build();
        Car astonMartin = sampleAstonMartinCarBuilder().build();
        manager.createLease(leaseForUpdate);
        carManager.createCar(astonMartin);

        leaseForUpdate.setLeasedCar(astonMartin);

        manager.updateLease(leaseForUpdate);

        assertThat(manager.getLeaseById(leaseForUpdate.getId()))
                .isEqualToComparingFieldByField(leaseForUpdate);
    }

    @Test
    public void updateStart() {
        Lease leaseForUpdate = sampleLeaseFirstLeaseBuilder()
                .build();
        manager.createLease(leaseForUpdate);
        leaseForUpdate.setStart(LocalDate.of(2000,3,25));
        manager.updateLease(leaseForUpdate);

        assertThat(manager.getLeaseById(leaseForUpdate.getId()))
                .isEqualToComparingFieldByField(leaseForUpdate);
    }

    @Test
    public void updateEnd() {
        Lease leaseForUpdate = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(leaseForUpdate);
        leaseForUpdate.setEnd(LocalDate.of(2018,3,25));
        manager.updateLease(leaseForUpdate);

        assertThat(manager.getLeaseById(leaseForUpdate.getId()))
                .isEqualToComparingFieldByField(leaseForUpdate);
    }


    @Test
    public void updatePrice() {
        Lease leaseForUpdate = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(leaseForUpdate);

        leaseForUpdate.setPrice(new BigDecimal(5000));
        manager.updateLease(leaseForUpdate);

        assertThat(manager.getLeaseById(leaseForUpdate.getId()).getPrice())
                .isEqualTo(leaseForUpdate.getPrice());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullLease() {
        manager.updateLease(null);
    }

    @Test
    public void updateLeaseWithNullId() {
        Lease lease = sampleLeaseFirstLeaseBuilder()
                .id(null)
                .build();
        expectedException.expect(IllegalEntityException.class);
        manager.updateLease(lease);
    }

    @Test
    public void updateNonExistingLease() {
        Lease lease = sampleLeaseFirstLeaseBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.updateLease(lease);
    }

    @Test
    public void updateLeaseWithNullCustomer() {
        Lease lease = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(lease);
        lease.setCustomer(null);
        expectedException.expect(ValidationException.class);
        manager.updateLease(lease);
    }

    @Test
    public void updateLeaseWithNullCar() {
        Lease lease = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(lease);
        lease.setLeasedCar(null);

        expectedException.expect(ValidationException.class);
        manager.updateLease(lease);
    }

    @Test
    public void updateLeaseWithNullStart() {
        Lease lease = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(lease);
        lease.setStart(null);

        expectedException.expect(ValidationException.class);
        manager.updateLease(lease);
    }

    @Test
    public void updateLeaseWithNullEnd() {
        Lease lease = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(lease);
        lease.setEnd(null);

        expectedException.expect(ValidationException.class);
        manager.updateLease(lease);
    }

    @Test
    public void updateLeaseWithNullPrice() {
        Lease lease = sampleLeaseFirstLeaseBuilder().build();
        manager.createLease(lease);
        lease.setPrice(null);

        expectedException.expect(ValidationException.class);
        manager.updateLease(lease);
    }


    @Test
    public void deleteLease() {
        Lease first = sampleLeaseFirstLeaseBuilder().build();
        Lease second = sampleLeaseSecondLeaseBuilder().build();
        manager.createLease(first);
        manager.createLease(second);

        assertThat(manager.getLeaseById(first.getId())).isNotNull();
        assertThat(manager.getLeaseById(second.getId())).isNotNull();
        manager.deleteLease(first);

        assertThat(manager.getLeaseById(first.getId())).isNull();
        assertThat(manager.getLeaseById(second.getId())).isNotNull();

    }


    @Test(expected = IllegalArgumentException.class)
    public void deleteNullLease() {
        manager.deleteLease(null);
    }

    @Test
    public void deleteLeaseWithNullId() {
        Lease lease = sampleLeaseSecondLeaseBuilder().id(null).build();
        expectedException.expect(IllegalEntityException.class);
        manager.deleteLease(lease);
    }

    @Test
    public void deleteNonExistingLease() {
        Lease lease = sampleLeaseSecondLeaseBuilder().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        manager.deleteLease(lease);
    }


    @Test
    public void findLeasesForCustomer() {
        Customer customer = sampleJamesCustomerBuilder().build();
        customerManager.createCustomer(customer);
        assertThat(manager.findLeasesForCustomer(customer)).isEmpty();

        Lease lease = sampleLeaseFirstLeaseBuilder().customer(customer).build();
        manager.createLease(lease);
        assertThat(manager.findLeasesForCustomer(customer))
                .usingFieldByFieldElementComparator()
                .containsOnly(lease);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findLeasesForNullCustomer() {
        manager.findLeasesForCustomer(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void findLeasesForCustomerWithNullId() {
        manager.findLeasesForCustomer(sampleJamesCustomerBuilder().id(null).build());
    }

    @Test
    public void findLeasesForNonExistingCustomer() {
        assertThat(manager.findLeasesForCustomer(sampleJamesCustomerBuilder().id(1L).build())).isNull();
    }


    @Test
    public void findLeasesForCar() {
        Car car = sampleBMWCarBuilder().build();
        carManager.createCar(car);
        assertThat(manager.findLeasesForCar(car)).isEmpty();

        Lease lease = sampleLeaseFirstLeaseBuilder().leasedCar(car).build();
        manager.createLease(lease);
        assertThat(manager.findLeasesForCar(car))
                .usingFieldByFieldElementComparator()
                .containsOnly(lease);

    }

    @Test(expected = IllegalArgumentException.class)
    public void findLeasesForNullCar() {
        manager.findLeasesForCar(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void findLeasesForCarWithNullId() {
        manager.findLeasesForCar(sampleBMWCarBuilder().id(null).build());
    }

    @Test
    public void findLeasesForNonExistingCar() {
        assertThat(manager.findLeasesForCar(sampleBMWCarBuilder().id(1L).build())).isNull();

    }

    @Test
    public void findUnleasedCarsLeasedNow() {
        assertThat(manager.findUnleasedCars()).isEmpty();

        Lease lease = sampleLeaseFirstLeaseBuilder()
                .start(LocalDate.now().minusDays(10))
                .end(LocalDate.now().plusDays(8))
                .build();

        manager.createLease(lease);

        assertThat(manager.findUnleasedCars()).isEmpty();
    }

    @Test
    public void findUnleasedCarsNotLeasedNow() {
        assertThat(manager.findUnleasedCars()).isEmpty();

        Car notLeased = sampleBMWCarBuilder().build();
        carManager.createCar(notLeased);

        Lease lease = sampleLeaseFirstLeaseBuilder()
                .start(LocalDate.now().minusDays(3))
                .end(LocalDate.now().plusDays(2))
                .build();

        manager.createLease(lease);

        assertThat(manager.findUnleasedCars())
                .usingFieldByFieldElementComparator()
                .containsOnly(notLeased);
    }

    @Test
    public void findLeasedCarsLeasedNow() {
        assertThat(manager.findUnleasedCars()).isEmpty();

        Car car = sampleBMWCarBuilder().build();
        carManager.createCar(car);

        Lease lease = sampleLeaseFirstLeaseBuilder()
                .start(LocalDate.now().minusDays(3))
                .end(LocalDate.now().plusDays(2))
                .leasedCar(car)
                .build();

        manager.createLease(lease);

        assertThat(manager.findLeasedCars())
                .usingFieldByFieldElementComparator()
                .containsOnly(car);

    }

    @Test
    public void findLeasedCarsNotLeasedNow() {
        assertThat(manager.findUnleasedCars()).isEmpty();

        Lease lease = sampleLeaseFirstLeaseBuilder()
                .start(LocalDate.now().minusDays(3))
                .end(LocalDate.now().minusDays(1))
                .build();

        manager.createLease(lease);

        assertThat(manager.findLeasedCars()).isEmpty();
    }

}
