package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.Customers} entity. This class is used
 * in {@link com.test.changes.web.rest.CustomersResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomersCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fullName;

    private StringFilter email;

    private StringFilter phone;

    private LongFilter ordersCustomerId;

    private LongFilter paymentmethodsCustomerId;

    private Boolean distinct;

    public CustomersCriteria() {}

    public CustomersCriteria(CustomersCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.ordersCustomerId = other.ordersCustomerId == null ? null : other.ordersCustomerId.copy();
        this.paymentmethodsCustomerId = other.paymentmethodsCustomerId == null ? null : other.paymentmethodsCustomerId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CustomersCriteria copy() {
        return new CustomersCriteria(this);
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

    public StringFilter getFullName() {
        return fullName;
    }

    public StringFilter fullName() {
        if (fullName == null) {
            fullName = new StringFilter();
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public StringFilter phone() {
        if (phone == null) {
            phone = new StringFilter();
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public LongFilter getOrdersCustomerId() {
        return ordersCustomerId;
    }

    public LongFilter ordersCustomerId() {
        if (ordersCustomerId == null) {
            ordersCustomerId = new LongFilter();
        }
        return ordersCustomerId;
    }

    public void setOrdersCustomerId(LongFilter ordersCustomerId) {
        this.ordersCustomerId = ordersCustomerId;
    }

    public LongFilter getPaymentmethodsCustomerId() {
        return paymentmethodsCustomerId;
    }

    public LongFilter paymentmethodsCustomerId() {
        if (paymentmethodsCustomerId == null) {
            paymentmethodsCustomerId = new LongFilter();
        }
        return paymentmethodsCustomerId;
    }

    public void setPaymentmethodsCustomerId(LongFilter paymentmethodsCustomerId) {
        this.paymentmethodsCustomerId = paymentmethodsCustomerId;
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
        final CustomersCriteria that = (CustomersCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(ordersCustomerId, that.ordersCustomerId) &&
            Objects.equals(paymentmethodsCustomerId, that.paymentmethodsCustomerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email, phone, ordersCustomerId, paymentmethodsCustomerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomersCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (fullName != null ? "fullName=" + fullName + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (phone != null ? "phone=" + phone + ", " : "") +
            (ordersCustomerId != null ? "ordersCustomerId=" + ordersCustomerId + ", " : "") +
            (paymentmethodsCustomerId != null ? "paymentmethodsCustomerId=" + paymentmethodsCustomerId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
