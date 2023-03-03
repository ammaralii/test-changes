package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.changes.domain.enumeration.ShippingMethod;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A ShippingDetails.
 */
@Entity
@Table(name = "shipping_details")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "shippingdetails")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShippingDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 32)
    @Column(name = "shipping_address", length = 32, nullable = false)
    private String shippingAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_method", nullable = false)
    private ShippingMethod shippingMethod;

    @NotNull
    @Column(name = "estimated_delivery_date", nullable = false)
    private LocalDate estimatedDeliveryDate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "customer", "orderdeliveryOrders", "orderdetailsOrders", "shippingdetailsOrders" }, allowSetters = true)
    private Orders order;

    @OneToMany(mappedBy = "shippingAddress")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "order", "shippingAddress", "deliveryManager", "deliveryBoy" }, allowSetters = true)
    private Set<OrderDelivery> orderdeliveryShippingaddresses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ShippingDetails id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShippingAddress() {
        return this.shippingAddress;
    }

    public ShippingDetails shippingAddress(String shippingAddress) {
        this.setShippingAddress(shippingAddress);
        return this;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public ShippingMethod getShippingMethod() {
        return this.shippingMethod;
    }

    public ShippingDetails shippingMethod(ShippingMethod shippingMethod) {
        this.setShippingMethod(shippingMethod);
        return this;
    }

    public void setShippingMethod(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public LocalDate getEstimatedDeliveryDate() {
        return this.estimatedDeliveryDate;
    }

    public ShippingDetails estimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.setEstimatedDeliveryDate(estimatedDeliveryDate);
        return this;
    }

    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public Orders getOrder() {
        return this.order;
    }

    public void setOrder(Orders orders) {
        this.order = orders;
    }

    public ShippingDetails order(Orders orders) {
        this.setOrder(orders);
        return this;
    }

    public Set<OrderDelivery> getOrderdeliveryShippingaddresses() {
        return this.orderdeliveryShippingaddresses;
    }

    public void setOrderdeliveryShippingaddresses(Set<OrderDelivery> orderDeliveries) {
        if (this.orderdeliveryShippingaddresses != null) {
            this.orderdeliveryShippingaddresses.forEach(i -> i.setShippingAddress(null));
        }
        if (orderDeliveries != null) {
            orderDeliveries.forEach(i -> i.setShippingAddress(this));
        }
        this.orderdeliveryShippingaddresses = orderDeliveries;
    }

    public ShippingDetails orderdeliveryShippingaddresses(Set<OrderDelivery> orderDeliveries) {
        this.setOrderdeliveryShippingaddresses(orderDeliveries);
        return this;
    }

    public ShippingDetails addOrderdeliveryShippingaddress(OrderDelivery orderDelivery) {
        this.orderdeliveryShippingaddresses.add(orderDelivery);
        orderDelivery.setShippingAddress(this);
        return this;
    }

    public ShippingDetails removeOrderdeliveryShippingaddress(OrderDelivery orderDelivery) {
        this.orderdeliveryShippingaddresses.remove(orderDelivery);
        orderDelivery.setShippingAddress(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ShippingDetails)) {
            return false;
        }
        return id != null && id.equals(((ShippingDetails) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShippingDetails{" +
            "id=" + getId() +
            ", shippingAddress='" + getShippingAddress() + "'" +
            ", shippingMethod='" + getShippingMethod() + "'" +
            ", estimatedDeliveryDate='" + getEstimatedDeliveryDate() + "'" +
            "}";
    }
}
