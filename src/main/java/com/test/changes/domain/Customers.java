package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Customers.
 */
@Entity
@Table(name = "customers")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "customers")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @NotNull
    @Size(max = 255)
    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @NotNull
    @Size(max = 255)
    @Column(name = "phone", length = 255, nullable = false)
    private String phone;

    @OneToMany(mappedBy = "customer")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer", "orderdeliveryOrders", "orderdetailsOrders", "shippingdetailsOrders" }, allowSetters = true)
    private Set<Orders> ordersCustomers = new HashSet<>();

    @OneToMany(mappedBy = "customer")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private Set<PaymentMethods> paymentmethodsCustomers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customers id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public Customers fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public Customers email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public Customers phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Orders> getOrdersCustomers() {
        return this.ordersCustomers;
    }

    public void setOrdersCustomers(Set<Orders> orders) {
        if (this.ordersCustomers != null) {
            this.ordersCustomers.forEach(i -> i.setCustomer(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setCustomer(this));
        }
        this.ordersCustomers = orders;
    }

    public Customers ordersCustomers(Set<Orders> orders) {
        this.setOrdersCustomers(orders);
        return this;
    }

    public Customers addOrdersCustomer(Orders orders) {
        this.ordersCustomers.add(orders);
        orders.setCustomer(this);
        return this;
    }

    public Customers removeOrdersCustomer(Orders orders) {
        this.ordersCustomers.remove(orders);
        orders.setCustomer(null);
        return this;
    }

    public Set<PaymentMethods> getPaymentmethodsCustomers() {
        return this.paymentmethodsCustomers;
    }

    public void setPaymentmethodsCustomers(Set<PaymentMethods> paymentMethods) {
        if (this.paymentmethodsCustomers != null) {
            this.paymentmethodsCustomers.forEach(i -> i.setCustomer(null));
        }
        if (paymentMethods != null) {
            paymentMethods.forEach(i -> i.setCustomer(this));
        }
        this.paymentmethodsCustomers = paymentMethods;
    }

    public Customers paymentmethodsCustomers(Set<PaymentMethods> paymentMethods) {
        this.setPaymentmethodsCustomers(paymentMethods);
        return this;
    }

    public Customers addPaymentmethodsCustomer(PaymentMethods paymentMethods) {
        this.paymentmethodsCustomers.add(paymentMethods);
        paymentMethods.setCustomer(this);
        return this;
    }

    public Customers removePaymentmethodsCustomer(PaymentMethods paymentMethods) {
        this.paymentmethodsCustomers.remove(paymentMethods);
        paymentMethods.setCustomer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customers)) {
            return false;
        }
        return id != null && id.equals(((Customers) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customers{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}
