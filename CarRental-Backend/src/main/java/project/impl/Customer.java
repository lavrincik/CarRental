package project.impl;

import java.util.Objects;

/**
 *
 * @author Daniel Jurca
 */
public class Customer {
    private String fullName;
    private Long id;
    private String address;
    private String phoneNumber;

    public Customer() {
    }

    public Customer(Long id, String fullName, String address, String phoneNumber) {
        this.fullName = fullName;
        this.id = id;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Customer{");
        sb.append("fullName='").append(fullName).append('\'');
        sb.append(", id=").append(id);
        sb.append(", address='").append(address).append('\'');
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (fullName != null ? !fullName.equals(customer.fullName) : customer.fullName != null) return false;
        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;
        if (address != null ? !address.equals(customer.address) : customer.address != null) return false;
        return phoneNumber != null ? phoneNumber.equals(customer.phoneNumber) : customer.phoneNumber == null;
    }

    @Override
    public int hashCode() {
        int result = fullName != null ? fullName.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        return result;
    }
}
