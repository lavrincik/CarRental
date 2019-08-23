package project.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This is a lease builder for tests
 * @author Daniel Jurca
 */
public class LeaseBuilder {
    private Customer customer;
    private Car leasedCar;
    private LocalDate start;
    private LocalDate end;
    private BigDecimal price;
    private Long id;

    public LeaseBuilder customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public LeaseBuilder leasedCar(Car leasedCar) {
        this.leasedCar = leasedCar;
        return this;
    }

    public LeaseBuilder start(LocalDate start) {
        this.start = start;
        return this;
    }

    public LeaseBuilder end(LocalDate end) {
        this.end = end;
        return this;
    }

    public LeaseBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public LeaseBuilder id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Creates new instance of Lease with given properties.
     *
     * @return new instance of Lease with given properties.
     */
    public Lease build() {
        Lease lease = new Lease();
        lease.setCustomer(customer);
        lease.setLeasedCar(leasedCar);
        lease.setStart(start);
        lease.setEnd(end);
        lease.setPrice(price);
        lease.setId(id);
        return lease;
    }
}
