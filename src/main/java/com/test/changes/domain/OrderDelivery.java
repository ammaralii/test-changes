package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.changes.domain.enumeration.ShippingStatus;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A OrderDelivery.
 */
@Entity
@Table(name = "order_delivery")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orderdelivery")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "delivery_charge")
    private Double deliveryCharge;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status", nullable = false)
    private ShippingStatus shippingStatus;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "customer", "orderdeliveryOrders", "orderdetailsOrders", "shippingdetailsOrders" }, allowSetters = true)
    private Orders order;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "order", "orderdeliveryShippingaddresses" }, allowSetters = true)
    private ShippingDetails shippingAddress;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "manager", "roles", "addressesUsers", "darazusersManagers", "orderdeliveryDeliverymanagers", "orderdeliveryDeliveryboys",
        },
        allowSetters = true
    )
    private DarazUsers deliveryManager;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "manager", "roles", "addressesUsers", "darazusersManagers", "orderdeliveryDeliverymanagers", "orderdeliveryDeliveryboys",
        },
        allowSetters = true
    )
    private DarazUsers deliveryBoy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderDelivery id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDeliveryDate() {
        return this.deliveryDate;
    }

    public OrderDelivery deliveryDate(LocalDate deliveryDate) {
        this.setDeliveryDate(deliveryDate);
        return this;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Double getDeliveryCharge() {
        return this.deliveryCharge;
    }

    public OrderDelivery deliveryCharge(Double deliveryCharge) {
        this.setDeliveryCharge(deliveryCharge);
        return this;
    }

    public void setDeliveryCharge(Double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public ShippingStatus getShippingStatus() {
        return this.shippingStatus;
    }

    public OrderDelivery shippingStatus(ShippingStatus shippingStatus) {
        this.setShippingStatus(shippingStatus);
        return this;
    }

    public void setShippingStatus(ShippingStatus shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Orders getOrder() {
        return this.order;
    }

    public void setOrder(Orders orders) {
        this.order = orders;
    }

    public OrderDelivery order(Orders orders) {
        this.setOrder(orders);
        return this;
    }

    public ShippingDetails getShippingAddress() {
        return this.shippingAddress;
    }

    public void setShippingAddress(ShippingDetails shippingDetails) {
        this.shippingAddress = shippingDetails;
    }

    public OrderDelivery shippingAddress(ShippingDetails shippingDetails) {
        this.setShippingAddress(shippingDetails);
        return this;
    }

    public DarazUsers getDeliveryManager() {
        return this.deliveryManager;
    }

    public void setDeliveryManager(DarazUsers darazUsers) {
        this.deliveryManager = darazUsers;
    }

    public OrderDelivery deliveryManager(DarazUsers darazUsers) {
        this.setDeliveryManager(darazUsers);
        return this;
    }

    public DarazUsers getDeliveryBoy() {
        return this.deliveryBoy;
    }

    public void setDeliveryBoy(DarazUsers darazUsers) {
        this.deliveryBoy = darazUsers;
    }

    public OrderDelivery deliveryBoy(DarazUsers darazUsers) {
        this.setDeliveryBoy(darazUsers);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderDelivery)) {
            return false;
        }
        return id != null && id.equals(((OrderDelivery) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDelivery{" +
            "id=" + getId() +
            ", deliveryDate='" + getDeliveryDate() + "'" +
            ", deliveryCharge=" + getDeliveryCharge() +
            ", shippingStatus='" + getShippingStatus() + "'" +
            "}";
    }
}
