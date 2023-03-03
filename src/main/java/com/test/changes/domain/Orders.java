package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Orders.
 */
@Entity
@Table(name = "orders")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orders")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "ordersCustomers", "paymentmethodsCustomers" }, allowSetters = true)
    private Customers customer;

    @OneToMany(mappedBy = "order")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "order", "shippingAddress", "deliveryManager", "deliveryBoy" }, allowSetters = true)
    private Set<OrderDelivery> orderdeliveryOrders = new HashSet<>();

    @OneToMany(mappedBy = "order")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "order", "product" }, allowSetters = true)
    private Set<OrderDetails> orderdetailsOrders = new HashSet<>();

    @OneToMany(mappedBy = "order")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "order", "orderdeliveryShippingaddresses" }, allowSetters = true)
    private Set<ShippingDetails> shippingdetailsOrders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Orders id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getOrderDate() {
        return this.orderDate;
    }

    public Orders orderDate(LocalDate orderDate) {
        this.setOrderDate(orderDate);
        return this;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getTotalAmount() {
        return this.totalAmount;
    }

    public Orders totalAmount(Integer totalAmount) {
        this.setTotalAmount(totalAmount);
        return this;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Customers getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customers customers) {
        this.customer = customers;
    }

    public Orders customer(Customers customers) {
        this.setCustomer(customers);
        return this;
    }

    public Set<OrderDelivery> getOrderdeliveryOrders() {
        return this.orderdeliveryOrders;
    }

    public void setOrderdeliveryOrders(Set<OrderDelivery> orderDeliveries) {
        if (this.orderdeliveryOrders != null) {
            this.orderdeliveryOrders.forEach(i -> i.setOrder(null));
        }
        if (orderDeliveries != null) {
            orderDeliveries.forEach(i -> i.setOrder(this));
        }
        this.orderdeliveryOrders = orderDeliveries;
    }

    public Orders orderdeliveryOrders(Set<OrderDelivery> orderDeliveries) {
        this.setOrderdeliveryOrders(orderDeliveries);
        return this;
    }

    public Orders addOrderdeliveryOrder(OrderDelivery orderDelivery) {
        this.orderdeliveryOrders.add(orderDelivery);
        orderDelivery.setOrder(this);
        return this;
    }

    public Orders removeOrderdeliveryOrder(OrderDelivery orderDelivery) {
        this.orderdeliveryOrders.remove(orderDelivery);
        orderDelivery.setOrder(null);
        return this;
    }

    public Set<OrderDetails> getOrderdetailsOrders() {
        return this.orderdetailsOrders;
    }

    public void setOrderdetailsOrders(Set<OrderDetails> orderDetails) {
        if (this.orderdetailsOrders != null) {
            this.orderdetailsOrders.forEach(i -> i.setOrder(null));
        }
        if (orderDetails != null) {
            orderDetails.forEach(i -> i.setOrder(this));
        }
        this.orderdetailsOrders = orderDetails;
    }

    public Orders orderdetailsOrders(Set<OrderDetails> orderDetails) {
        this.setOrderdetailsOrders(orderDetails);
        return this;
    }

    public Orders addOrderdetailsOrder(OrderDetails orderDetails) {
        this.orderdetailsOrders.add(orderDetails);
        orderDetails.setOrder(this);
        return this;
    }

    public Orders removeOrderdetailsOrder(OrderDetails orderDetails) {
        this.orderdetailsOrders.remove(orderDetails);
        orderDetails.setOrder(null);
        return this;
    }

    public Set<ShippingDetails> getShippingdetailsOrders() {
        return this.shippingdetailsOrders;
    }

    public void setShippingdetailsOrders(Set<ShippingDetails> shippingDetails) {
        if (this.shippingdetailsOrders != null) {
            this.shippingdetailsOrders.forEach(i -> i.setOrder(null));
        }
        if (shippingDetails != null) {
            shippingDetails.forEach(i -> i.setOrder(this));
        }
        this.shippingdetailsOrders = shippingDetails;
    }

    public Orders shippingdetailsOrders(Set<ShippingDetails> shippingDetails) {
        this.setShippingdetailsOrders(shippingDetails);
        return this;
    }

    public Orders addShippingdetailsOrder(ShippingDetails shippingDetails) {
        this.shippingdetailsOrders.add(shippingDetails);
        shippingDetails.setOrder(this);
        return this;
    }

    public Orders removeShippingdetailsOrder(ShippingDetails shippingDetails) {
        this.shippingdetailsOrders.remove(shippingDetails);
        shippingDetails.setOrder(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Orders)) {
            return false;
        }
        return id != null && id.equals(((Orders) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Orders{" +
            "id=" + getId() +
            ", orderDate='" + getOrderDate() + "'" +
            ", totalAmount=" + getTotalAmount() +
            "}";
    }
}
