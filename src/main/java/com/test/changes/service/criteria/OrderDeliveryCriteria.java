package com.test.changes.service.criteria;

import com.test.changes.domain.enumeration.ShippingStatus;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.OrderDelivery} entity. This class is used
 * in {@link com.test.changes.web.rest.OrderDeliveryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /order-deliveries?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderDeliveryCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ShippingStatus
     */
    public static class ShippingStatusFilter extends Filter<ShippingStatus> {

        public ShippingStatusFilter() {}

        public ShippingStatusFilter(ShippingStatusFilter filter) {
            super(filter);
        }

        @Override
        public ShippingStatusFilter copy() {
            return new ShippingStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter deliveryDate;

    private DoubleFilter deliveryCharge;

    private ShippingStatusFilter shippingStatus;

    private LongFilter orderId;

    private LongFilter shippingAddressId;

    private LongFilter deliveryManagerId;

    private LongFilter deliveryBoyId;

    private Boolean distinct;

    public OrderDeliveryCriteria() {}

    public OrderDeliveryCriteria(OrderDeliveryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.deliveryDate = other.deliveryDate == null ? null : other.deliveryDate.copy();
        this.deliveryCharge = other.deliveryCharge == null ? null : other.deliveryCharge.copy();
        this.shippingStatus = other.shippingStatus == null ? null : other.shippingStatus.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.shippingAddressId = other.shippingAddressId == null ? null : other.shippingAddressId.copy();
        this.deliveryManagerId = other.deliveryManagerId == null ? null : other.deliveryManagerId.copy();
        this.deliveryBoyId = other.deliveryBoyId == null ? null : other.deliveryBoyId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OrderDeliveryCriteria copy() {
        return new OrderDeliveryCriteria(this);
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

    public LocalDateFilter getDeliveryDate() {
        return deliveryDate;
    }

    public LocalDateFilter deliveryDate() {
        if (deliveryDate == null) {
            deliveryDate = new LocalDateFilter();
        }
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateFilter deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public DoubleFilter getDeliveryCharge() {
        return deliveryCharge;
    }

    public DoubleFilter deliveryCharge() {
        if (deliveryCharge == null) {
            deliveryCharge = new DoubleFilter();
        }
        return deliveryCharge;
    }

    public void setDeliveryCharge(DoubleFilter deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public ShippingStatusFilter getShippingStatus() {
        return shippingStatus;
    }

    public ShippingStatusFilter shippingStatus() {
        if (shippingStatus == null) {
            shippingStatus = new ShippingStatusFilter();
        }
        return shippingStatus;
    }

    public void setShippingStatus(ShippingStatusFilter shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public LongFilter getOrderId() {
        return orderId;
    }

    public LongFilter orderId() {
        if (orderId == null) {
            orderId = new LongFilter();
        }
        return orderId;
    }

    public void setOrderId(LongFilter orderId) {
        this.orderId = orderId;
    }

    public LongFilter getShippingAddressId() {
        return shippingAddressId;
    }

    public LongFilter shippingAddressId() {
        if (shippingAddressId == null) {
            shippingAddressId = new LongFilter();
        }
        return shippingAddressId;
    }

    public void setShippingAddressId(LongFilter shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public LongFilter getDeliveryManagerId() {
        return deliveryManagerId;
    }

    public LongFilter deliveryManagerId() {
        if (deliveryManagerId == null) {
            deliveryManagerId = new LongFilter();
        }
        return deliveryManagerId;
    }

    public void setDeliveryManagerId(LongFilter deliveryManagerId) {
        this.deliveryManagerId = deliveryManagerId;
    }

    public LongFilter getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public LongFilter deliveryBoyId() {
        if (deliveryBoyId == null) {
            deliveryBoyId = new LongFilter();
        }
        return deliveryBoyId;
    }

    public void setDeliveryBoyId(LongFilter deliveryBoyId) {
        this.deliveryBoyId = deliveryBoyId;
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
        final OrderDeliveryCriteria that = (OrderDeliveryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(deliveryDate, that.deliveryDate) &&
            Objects.equals(deliveryCharge, that.deliveryCharge) &&
            Objects.equals(shippingStatus, that.shippingStatus) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(shippingAddressId, that.shippingAddressId) &&
            Objects.equals(deliveryManagerId, that.deliveryManagerId) &&
            Objects.equals(deliveryBoyId, that.deliveryBoyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            deliveryDate,
            deliveryCharge,
            shippingStatus,
            orderId,
            shippingAddressId,
            deliveryManagerId,
            deliveryBoyId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDeliveryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (deliveryDate != null ? "deliveryDate=" + deliveryDate + ", " : "") +
            (deliveryCharge != null ? "deliveryCharge=" + deliveryCharge + ", " : "") +
            (shippingStatus != null ? "shippingStatus=" + shippingStatus + ", " : "") +
            (orderId != null ? "orderId=" + orderId + ", " : "") +
            (shippingAddressId != null ? "shippingAddressId=" + shippingAddressId + ", " : "") +
            (deliveryManagerId != null ? "deliveryManagerId=" + deliveryManagerId + ", " : "") +
            (deliveryBoyId != null ? "deliveryBoyId=" + deliveryBoyId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
