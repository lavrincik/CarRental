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
import project.CustomerManager;
import project.exception.IllegalEntityException;
import project.exception.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for CustomerManager
 *
 * @author Marek Lavrincik
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MySpringTestConfig.class})
@Transactional
public class CustomerManagerImplTest {

    @Autowired
    private CustomerManager manager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CustomerBuilder sampleZeroBuilder() {
        return new CustomerBuilder()
                .address("address0")
                .fullName("fullName0")
                .id(null)
                .phoneNumber("0");
    }

    private CustomerBuilder sampleOneBuilder() {
        return new CustomerBuilder()
                .address("address1")
                .fullName("fullName1")
                .id(null)
                .phoneNumber("1");
    }

    @Test
    public void createCustomer() {
        Customer customer = sampleOneBuilder().build();
        manager.createCustomer(customer);

        Long customerId = customer.getId();
        assertThat(customerId).isNotNull();

        assertThat(manager.getCustomerById(customerId))
                .isNotSameAs(customer)
                .isEqualToComparingFieldByField(customer);
    }

    @Test
    public void findAllCustomers() {
        assertThat(manager.findAllCustomers()).isEmpty();

        Customer c1 = sampleOneBuilder().build();
        Customer c2 = sampleOneBuilder().build();

        manager.createCustomer(c1);
        manager.createCustomer(c2);

        assertThat(manager.findAllCustomers())
                .usingFieldByFieldElementComparator()
                .containsOnly(c1, c2);
    }

    @Test
    public void findCustomerByName() {
        Customer c1 = sampleOneBuilder().fullName("a").build();
        Customer c2 = sampleOneBuilder().fullName("a").build();

        manager.createCustomer(c1);
        manager.createCustomer(c2);

        assertThat(manager.findCustomerByName("a"))
                .usingFieldByFieldElementComparator()
                .containsOnly(c1, c2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullCustomer() {
        manager.createCustomer(null);
    }

    @Test
    public void createCustomerWithExistingId() {
        Customer customer = sampleOneBuilder().id(1L).build();
        assertThatThrownBy(() -> manager.createCustomer(customer))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void createCustomerWithNullFullName() {
        Customer customer = sampleZeroBuilder().fullName(null).build();
        assertThatThrownBy(() -> manager.createCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCustomerWithNullAddress() {
        Customer customer = sampleZeroBuilder().address(null).build();
        assertThatThrownBy(() -> manager.createCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCustomerWithNullPhoneNumber() {
        Customer customer = sampleZeroBuilder().phoneNumber(null).build();
        assertThatThrownBy(() -> manager.createCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCustomerWithLetterPhoneNumber() {
        Customer customer = sampleZeroBuilder().phoneNumber("a").build();
        assertThatThrownBy(() -> manager.createCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCustomerWithEmptyFullName() {
        Customer customer = sampleZeroBuilder().fullName("").build();
        assertThatThrownBy(() -> manager.createCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCustomerWithEmptyAddress() {
        Customer customer = sampleZeroBuilder().address("").build();
        assertThatThrownBy(() -> manager.createCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void createCustomerWithEmptyPhoneNumber() {
        Customer customer = sampleZeroBuilder().phoneNumber("").build();
        assertThatThrownBy(() -> manager.createCustomer(customer))
                .isInstanceOf(ValidationException.class);
    }

    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }

    private void testUpdateCustomer(Operation<Customer> updateOperation) {
        // Let us create two customers, one will be used for testing the update
        // and another one will be used for verification that other objects are
        // not affected by update operation
        Customer sourceCustomer = sampleOneBuilder().build();
        Customer anotherCustomer = sampleOneBuilder().build();
        manager.createCustomer(sourceCustomer);
        manager.createCustomer(anotherCustomer);

        // Performa the update operation ...
        updateOperation.callOn(sourceCustomer);

        // ... and save updated customer to database
        manager.updateCustomer(sourceCustomer);

        // Check if customer was properly updated
        assertThat(manager.getCustomerById(sourceCustomer.getId()))
                .isEqualToComparingFieldByField(sourceCustomer);

        // Check if updates didn't affected other records
        assertThat(manager.getCustomerById(anotherCustomer.getId()))
                .isEqualToComparingFieldByField(anotherCustomer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullCustomer() {
        manager.updateCustomer(null);
    }

    @Test
    public void updateCustomerFullName() {
        testUpdateCustomer((customer) -> customer.setFullName("New Name"));
    }

    @Test
    public void updateCustomerPhoneNumber() {
        testUpdateCustomer((customer) -> customer.setPhoneNumber("123"));
    }

    @Test
    public void updateCustomerAddress() {
        testUpdateCustomer((customer) -> customer.setAddress("ABC"));
    }

    @Test
    public void updateCustomerWithNullId() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setId(null)))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void updateCustomerWithNonExistingId() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setId(1L)))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void updateCustomerWithNullFullName() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setFullName(null)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateCustomerWithNullAddress() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setAddress(null)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateCustomerWithNullPhoneNumber() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setPhoneNumber(null)))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateCustomerWithLetterPhoneNumber() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setPhoneNumber("a")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateCustomerWithEmptyFullName() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setFullName("")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateCustomerWithEmptyAddress() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setAddress("")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateCustomerWithEmptyPhoneNumber() {
        assertThatThrownBy(() -> testUpdateCustomer((customer) -> customer.setPhoneNumber("")))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void deleteCustomer() {
        Customer g1 = sampleZeroBuilder().build();
        Customer g2 = sampleOneBuilder().build();
        manager.createCustomer(g1);
        manager.createCustomer(g2);

        assertThat(manager.getCustomerById(g1.getId())).isNotNull();
        assertThat(manager.getCustomerById(g2.getId())).isNotNull();

        manager.deleteCustomer(g1);

        assertThat(manager.getCustomerById(g1.getId())).isNull();
        assertThat(manager.getCustomerById(g2.getId())).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullCustomer() {
        manager.deleteCustomer(null);
    }

    @Test
    public void deleteCustomerWithNullId() {
        Customer customer = sampleZeroBuilder().id(null).build();
        assertThatThrownBy(() -> manager.deleteCustomer(customer))
                .isInstanceOf(IllegalEntityException.class);
    }

    @Test
    public void deleteCustomerWithNonExistingId() {
        Customer customer = sampleZeroBuilder().id(1L).build();
        assertThatThrownBy(() -> manager.deleteCustomer(customer))
                .isInstanceOf(IllegalEntityException.class);
    }

}