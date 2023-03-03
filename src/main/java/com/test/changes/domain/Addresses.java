package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Addresses.
 */
@Entity
@Table(name = "addresses")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "addresses")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Addresses implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 30)
    @Column(name = "street", length = 30, nullable = false)
    private String street;

    @NotNull
    @Size(max = 30)
    @Column(name = "city", length = 30, nullable = false)
    private String city;

    @NotNull
    @Size(max = 30)
    @Column(name = "state", length = 30, nullable = false)
    private String state;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = {
            "manager", "roles", "addressesUsers", "darazusersManagers", "orderdeliveryDeliverymanagers", "orderdeliveryDeliveryboys",
        },
        allowSetters = true
    )
    private DarazUsers user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Addresses id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return this.street;
    }

    public Addresses street(String street) {
        this.setStreet(street);
        return this;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return this.city;
    }

    public Addresses city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public Addresses state(String state) {
        this.setState(state);
        return this;
    }

    public void setState(String state) {
        this.state = state;
    }

    public DarazUsers getUser() {
        return this.user;
    }

    public void setUser(DarazUsers darazUsers) {
        this.user = darazUsers;
    }

    public Addresses user(DarazUsers darazUsers) {
        this.setUser(darazUsers);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Addresses)) {
            return false;
        }
        return id != null && id.equals(((Addresses) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Addresses{" +
            "id=" + getId() +
            ", street='" + getStreet() + "'" +
            ", city='" + getCity() + "'" +
            ", state='" + getState() + "'" +
            "}";
    }
}
