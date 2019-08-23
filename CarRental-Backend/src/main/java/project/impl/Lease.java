package project.impl;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author Daniel Jurca
 */
public class Lease {
    private Customer customer;
    private Car leasedCar;
    private LocalDate start;
    private LocalDate end;
    private BigDecimal price;
    private Long id;

    public Lease() {
    }

    public Lease(Long id, Customer customer, Car leasedCar, LocalDate start, LocalDate end, BigDecimal price) {
        this.customer = customer;
        this.leasedCar = leasedCar;
        this.start = start;
        this.end = end;
        this.price = price;
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Car getLeasedCar() {
        return leasedCar;
    }

    public void setLeasedCar(Car leasedCar) {
        this.leasedCar = leasedCar;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Lease{");
        sb.append("customer=").append(customer);
        sb.append(", leasedCar=").append(leasedCar);
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", price=").append(price);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Lease lease = (Lease) o;

        if (customer != null ? !customer.equals(lease.customer) : lease.customer != null) return false;
        if (leasedCar != null ? !leasedCar.equals(lease.leasedCar) : lease.leasedCar != null) return false;
        if (start != null ? !start.equals(lease.start) : lease.start != null) return false;
        if (end != null ? !end.equals(lease.end) : lease.end != null) return false;
        if (price != null ? !price.equals(lease.price) : lease.price != null) return false;
        return id != null ? id.equals(lease.id) : lease.id == null;
    }

    @Override
    public int hashCode() {
        int result = customer != null ? customer.hashCode() : 0;
        result = 31 * result + (leasedCar != null ? leasedCar.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
