package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A PaymentMethods.
 */
@Entity
@Table(name = "payment_methods")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "paymentmethods")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaymentMethods implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 32)
    @Column(name = "card_number", length = 32, nullable = false)
    private String cardNumber;

    @NotNull
    @Size(max = 32)
    @Column(name = "card_holder_name", length = 32, nullable = false)
    private String cardHolderName;

    @NotNull
    @Size(max = 10)
    @Column(name = "expiration_date", length = 10, nullable = false)
    private String expirationDate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "ordersCustomers", "paymentmethodsCustomers" }, allowSetters = true)
    private Customers customer;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaymentMethods id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public PaymentMethods cardNumber(String cardNumber) {
        this.setCardNumber(cardNumber);
        return this;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return this.cardHolderName;
    }

    public PaymentMethods cardHolderName(String cardHolderName) {
        this.setCardHolderName(cardHolderName);
        return this;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public PaymentMethods expirationDate(String expirationDate) {
        this.setExpirationDate(expirationDate);
        return this;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Customers getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customers customers) {
        this.customer = customers;
    }

    public PaymentMethods customer(Customers customers) {
        this.setCustomer(customers);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentMethods)) {
            return false;
        }
        return id != null && id.equals(((PaymentMethods) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentMethods{" +
            "id=" + getId() +
            ", cardNumber='" + getCardNumber() + "'" +
            ", cardHolderName='" + getCardHolderName() + "'" +
            ", expirationDate='" + getExpirationDate() + "'" +
            "}";
    }
}
