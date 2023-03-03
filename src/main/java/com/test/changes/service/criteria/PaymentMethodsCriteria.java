package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.PaymentMethods} entity. This class is used
 * in {@link com.test.changes.web.rest.PaymentMethodsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /payment-methods?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentMethodsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter cardNumber;

    private StringFilter cardHolderName;

    private StringFilter expirationDate;

    private LongFilter customerId;

    private Boolean distinct;

    public PaymentMethodsCriteria() {}

    public PaymentMethodsCriteria(PaymentMethodsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.cardNumber = other.cardNumber == null ? null : other.cardNumber.copy();
        this.cardHolderName = other.cardHolderName == null ? null : other.cardHolderName.copy();
        this.expirationDate = other.expirationDate == null ? null : other.expirationDate.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PaymentMethodsCriteria copy() {
        return new PaymentMethodsCriteria(this);
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

    public StringFilter getCardNumber() {
        return cardNumber;
    }

    public StringFilter cardNumber() {
        if (cardNumber == null) {
            cardNumber = new StringFilter();
        }
        return cardNumber;
    }

    public void setCardNumber(StringFilter cardNumber) {
        this.cardNumber = cardNumber;
    }

    public StringFilter getCardHolderName() {
        return cardHolderName;
    }

    public StringFilter cardHolderName() {
        if (cardHolderName == null) {
            cardHolderName = new StringFilter();
        }
        return cardHolderName;
    }

    public void setCardHolderName(StringFilter cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public StringFilter getExpirationDate() {
        return expirationDate;
    }

    public StringFilter expirationDate() {
        if (expirationDate == null) {
            expirationDate = new StringFilter();
        }
        return expirationDate;
    }

    public void setExpirationDate(StringFilter expirationDate) {
        this.expirationDate = expirationDate;
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
        final PaymentMethodsCriteria that = (PaymentMethodsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(cardNumber, that.cardNumber) &&
            Objects.equals(cardHolderName, that.cardHolderName) &&
            Objects.equals(expirationDate, that.expirationDate) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, cardHolderName, expirationDate, customerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentMethodsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (cardNumber != null ? "cardNumber=" + cardNumber + ", " : "") +
            (cardHolderName != null ? "cardHolderName=" + cardHolderName + ", " : "") +
            (expirationDate != null ? "expirationDate=" + expirationDate + ", " : "") +
            (customerId != null ? "customerId=" + customerId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
