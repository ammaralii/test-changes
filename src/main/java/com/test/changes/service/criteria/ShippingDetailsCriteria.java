package com.test.changes.service.criteria;

import com.test.changes.domain.enumeration.ShippingMethod;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.ShippingDetails} entity. This class is used
 * in {@link com.test.changes.web.rest.ShippingDetailsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /shipping-details?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ShippingDetailsCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ShippingMethod
     */
    public static class ShippingMethodFilter extends Filter<ShippingMethod> {

        public ShippingMethodFilter() {}

        public ShippingMethodFilter(ShippingMethodFilter filter) {
            super(filter);
        }

        @Override
        public ShippingMethodFilter copy() {
            return new ShippingMethodFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter shippingAddress;

    private ShippingMethodFilter shippingMethod;

    private LocalDateFilter estimatedDeliveryDate;

    private LongFilter orderId;

    private LongFilter orderdeliveryShippingaddressId;

    private Boolean distinct;

    public ShippingDetailsCriteria() {}

    public ShippingDetailsCriteria(ShippingDetailsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.shippingAddress = other.shippingAddress == null ? null : other.shippingAddress.copy();
        this.shippingMethod = other.shippingMethod == null ? null : other.shippingMethod.copy();
        this.estimatedDeliveryDate = other.estimatedDeliveryDate == null ? null : other.estimatedDeliveryDate.copy();
        this.orderId = other.orderId == null ? null : other.orderId.copy();
        this.orderdeliveryShippingaddressId =
            other.orderdeliveryShippingaddressId == null ? null : other.orderdeliveryShippingaddressId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ShippingDetailsCriteria copy() {
        return new ShippingDetailsCriteria(this);
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

    public StringFilter getShippingAddress() {
        return shippingAddress;
    }

    public StringFilter shippingAddress() {
        if (shippingAddress == null) {
            shippingAddress = new StringFilter();
        }
        return shippingAddress;
    }

    public void setShippingAddress(StringFilter shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public ShippingMethodFilter getShippingMethod() {
        return shippingMethod;
    }

    public ShippingMethodFilter shippingMethod() {
        if (shippingMethod == null) {
            shippingMethod = new ShippingMethodFilter();
        }
        return shippingMethod;
    }

    public void setShippingMethod(ShippingMethodFilter shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public LocalDateFilter getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public LocalDateFilter estimatedDeliveryDate() {
        if (estimatedDeliveryDate == null) {
            estimatedDeliveryDate = new LocalDateFilter();
        }
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateFilter estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
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

    public LongFilter getOrderdeliveryShippingaddressId() {
        return orderdeliveryShippingaddressId;
    }

    public LongFilter orderdeliveryShippingaddressId() {
        if (orderdeliveryShippingaddressId == null) {
            orderdeliveryShippingaddressId = new LongFilter();
        }
        return orderdeliveryShippingaddressId;
    }

    public void setOrderdeliveryShippingaddressId(LongFilter orderdeliveryShippingaddressId) {
        this.orderdeliveryShippingaddressId = orderdeliveryShippingaddressId;
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
        final ShippingDetailsCriteria that = (ShippingDetailsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(shippingAddress, that.shippingAddress) &&
            Objects.equals(shippingMethod, that.shippingMethod) &&
            Objects.equals(estimatedDeliveryDate, that.estimatedDeliveryDate) &&
            Objects.equals(orderId, that.orderId) &&
            Objects.equals(orderdeliveryShippingaddressId, that.orderdeliveryShippingaddressId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shippingAddress, shippingMethod, estimatedDeliveryDate, orderId, orderdeliveryShippingaddressId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ShippingDetailsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (shippingAddress != null ? "shippingAddress=" + shippingAddress + ", " : "") +
            (shippingMethod != null ? "shippingMethod=" + shippingMethod + ", " : "") +
            (estimatedDeliveryDate != null ? "estimatedDeliveryDate=" + estimatedDeliveryDate + ", " : "") +
            (orderId != null ? "orderId=" + orderId + ", " : "") +
            (orderdeliveryShippingaddressId != null ? "orderdeliveryShippingaddressId=" + orderdeliveryShippingaddressId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
