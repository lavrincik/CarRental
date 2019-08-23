package project.impl;

/**
 * Customer builder for tests
 * @author Marek Lavrincik
 */
public class CustomerBuilder {
    private String fullName;
    private Long id;
    private String address;
    private String phoneNumber;

    public CustomerBuilder fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public CustomerBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder address(String address) {
        this.address = address;
        return this;
    }

    public CustomerBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    /**
     * Creates new instance of Customer with given properties.
     *
     * @return new instance of Customer with given properties.
     */
    public Customer build() {
        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setId(id);
        customer.setAddress(address);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }
}
