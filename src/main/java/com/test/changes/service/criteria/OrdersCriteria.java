package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.Orders} entity. This class is used
 * in {@link com.test.changes.web.rest.OrdersResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrdersCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter orderDate;

    private IntegerFilter totalAmount;

    private LongFilter customerId;

    private LongFilter orderdeliveryOrderId;

    private LongFilter orderdetailsOrderId;

    private LongFilter shippingdetailsOrderId;

    private Boolean distinct;

    public OrdersCriteria() {}

    public OrdersCriteria(OrdersCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.orderDate = other.orderDate == null ? null : other.orderDate.copy();
        this.totalAmount = other.totalAmount == null ? null : other.totalAmount.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
        this.orderdeliveryOrderId = other.orderdeliveryOrderId == null ? null : other.orderdeliveryOrderId.copy();
        this.orderdetailsOrderId = other.orderdetailsOrderId == null ? null : other.orderdetailsOrderId.copy();
        this.shippingdetailsOrderId = other.shippingdetailsOrderId == null ? null : other.shippingdetailsOrderId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrdersCriteria copy() {
        return new OrdersCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getOrderDate() {
        return orderDate;
    }

    public LocalDateFilter orderDate() {
        if (orderDate == null) {
            orderDate = new LocalDateFilter();
        }
        return orderDate;
    }

    public void setOrderDate(LocalDateFilter orderDate) {
        this.orderDate = orderDate;
    }

    public IntegerFilter getTotalAmount() {
        return totalAmount;
    }

    public IntegerFilter totalAmount() {
        if (totalAmount == null) {
            totalAmount = new IntegerFilter();
        }
        return totalAmount;
    }

    public void setTotalAmount(IntegerFilter totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public LongFilter customerId() {
        if (customerId == null) {
            customerId = new LongFilter();
        }
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getOrderdeliveryOrderId() {
        return orderdeliveryOrderId;
    }

    public LongFilter orderdeliveryOrderId() {
        if (orderdeliveryOrderId == null) {
            orderdeliveryOrderId = new LongFilter();
        }
        return orderdeliveryOrderId;
    }

    public void setOrderdeliveryOrderId(LongFilter orderdeliveryOrderId) {
        this.orderdeliveryOrderId = orderdeliveryOrderId;
    }

    public LongFilter getOrderdetailsOrderId() {
        return orderdetailsOrderId;
    }

    public LongFilter orderdetailsOrderId() {
        if (orderdetailsOrderId == null) {
            orderdetailsOrderId = new LongFilter();
        }
        return orderdetailsOrderId;
    }

    public void setOrderdetailsOrderId(LongFilter orderdetailsOrderId) {
        this.orderdetailsOrderId = orderdetailsOrderId;
    }

    public LongFilter getShippingdetailsOrderId() {
        return shippingdetailsOrderId;
    }

    public LongFilter shippingdetailsOrderId() {
        if (shippingdetailsOrderId == null) {
            shippingdetailsOrderId = new LongFilter();
        }
        return shippingdetailsOrderId;
    }

    public void setShippingdetailsOrderId(LongFilter shippingdetailsOrderId) {
        this.shippingdetailsOrderId = shippingdetailsOrderId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrdersCriteria that = (OrdersCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(orderDate, that.orderDate) &&
            Objects.equals(totalAmount, that.totalAmount) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(orderdeliveryOrderId, that.orderdeliveryOrderId) &&
            Objects.equals(orderdetailsOrderId, that.orderdetailsOrderId) &&
            Objects.equals(shippingdetailsOrderId, that.shippingdetailsOrderId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            orderDate,
            totalAmount,
            customerId,
            orderdeliveryOrderId,
            orderdetailsOrderId,
            shippingdetailsOrderId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrdersCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (orderDate != null ? "orderDate=" + orderDate + ", " : "") +
            (totalAmount != null ? "totalAmount=" + totalAmount + ", " : "") +
            (customerId != null ? "customerId=" + customerId + ", " : "") +
            (orderdeliveryOrderId != null ? "orderdeliveryOrderId=" + orderdeliveryOrderId + ", " : "") +
            (orderdetailsOrderId != null ? "orderdetailsOrderId=" + orderdetailsOrderId + ", " : "") +
            (shippingdetailsOrderId != null ? "shippingdetailsOrderId=" + shippingdetailsOrderId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
