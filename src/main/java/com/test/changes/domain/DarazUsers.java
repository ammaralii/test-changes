package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A DarazUsers.
 */
@Entity
@Table(name = "daraz_users")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "darazusers")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DarazUsers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @NotNull
    @Size(max = 255)
    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @NotNull
    @Size(max = 255)
    @Column(name = "phone", length = 255, nullable = false)
    private String phone;

    @ManyToOne
    @JsonIgnoreProperties(
        value = {
            "manager", "roles", "addressesUsers", "darazusersManagers", "orderdeliveryDeliverymanagers", "orderdeliveryDeliveryboys",
        },
        allowSetters = true
    )
    private DarazUsers manager;

    @ManyToMany
    @JoinTable(
        name = "rel_daraz_users__role",
        joinColumns = @JoinColumn(name = "daraz_users_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @JsonIgnoreProperties(value = { "users" }, allowSetters = true)
    private Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Set<Addresses> addressesUsers = new HashSet<>();

    @OneToMany(mappedBy = "manager")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = {
            "manager", "roles", "addressesUsers", "darazusersManagers", "orderdeliveryDeliverymanagers", "orderdeliveryDeliveryboys",
        },
        allowSetters = true
    )
    private Set<DarazUsers> darazusersManagers = new HashSet<>();

    @OneToMany(mappedBy = "deliveryManager")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "order", "shippingAddress", "deliveryManager", "deliveryBoy" }, allowSetters = true)
    private Set<OrderDelivery> orderdeliveryDeliverymanagers = new HashSet<>();

    @OneToMany(mappedBy = "deliveryBoy")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "order", "shippingAddress", "deliveryManager", "deliveryBoy" }, allowSetters = true)
    private Set<OrderDelivery> orderdeliveryDeliveryboys = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DarazUsers id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public DarazUsers fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public DarazUsers email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public DarazUsers phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public DarazUsers getManager() {
        return this.manager;
    }

    public void setManager(DarazUsers darazUsers) {
        this.manager = darazUsers;
    }

    public DarazUsers manager(DarazUsers darazUsers) {
        this.setManager(darazUsers);
        return this;
    }

    public Set<Roles> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Roles> roles) {
        this.roles = roles;
    }

    public DarazUsers roles(Set<Roles> roles) {
        this.setRoles(roles);
        return this;
    }

    public DarazUsers addRole(Roles roles) {
        this.roles.add(roles);
        roles.getUsers().add(this);
        return this;
    }

    public DarazUsers removeRole(Roles roles) {
        this.roles.remove(roles);
        roles.getUsers().remove(this);
        return this;
    }

    public Set<Addresses> getAddressesUsers() {
        return this.addressesUsers;
    }

    public void setAddressesUsers(Set<Addresses> addresses) {
        if (this.addressesUsers != null) {
            this.addressesUsers.forEach(i -> i.setUser(null));
        }
        if (addresses != null) {
            addresses.forEach(i -> i.setUser(this));
        }
        this.addressesUsers = addresses;
    }

    public DarazUsers addressesUsers(Set<Addresses> addresses) {
        this.setAddressesUsers(addresses);
        return this;
    }

    public DarazUsers addAddressesUser(Addresses addresses) {
        this.addressesUsers.add(addresses);
        addresses.setUser(this);
        return this;
    }

    public DarazUsers removeAddressesUser(Addresses addresses) {
        this.addressesUsers.remove(addresses);
        addresses.setUser(null);
        return this;
    }

    public Set<DarazUsers> getDarazusersManagers() {
        return this.darazusersManagers;
    }

    public void setDarazusersManagers(Set<DarazUsers> darazUsers) {
        if (this.darazusersManagers != null) {
            this.darazusersManagers.forEach(i -> i.setManager(null));
        }
        if (darazUsers != null) {
            darazUsers.forEach(i -> i.setManager(this));
        }
        this.darazusersManagers = darazUsers;
    }

    public DarazUsers darazusersManagers(Set<DarazUsers> darazUsers) {
        this.setDarazusersManagers(darazUsers);
        return this;
    }

    public DarazUsers addDarazusersManager(DarazUsers darazUsers) {
        this.darazusersManagers.add(darazUsers);
        darazUsers.setManager(this);
        return this;
    }

    public DarazUsers removeDarazusersManager(DarazUsers darazUsers) {
        this.darazusersManagers.remove(darazUsers);
        darazUsers.setManager(null);
        return this;
    }

    public Set<OrderDelivery> getOrderdeliveryDeliverymanagers() {
        return this.orderdeliveryDeliverymanagers;
    }

    public void setOrderdeliveryDeliverymanagers(Set<OrderDelivery> orderDeliveries) {
        if (this.orderdeliveryDeliverymanagers != null) {
            this.orderdeliveryDeliverymanagers.forEach(i -> i.setDeliveryManager(null));
        }
        if (orderDeliveries != null) {
            orderDeliveries.forEach(i -> i.setDeliveryManager(this));
        }
        this.orderdeliveryDeliverymanagers = orderDeliveries;
    }

    public DarazUsers orderdeliveryDeliverymanagers(Set<OrderDelivery> orderDeliveries) {
        this.setOrderdeliveryDeliverymanagers(orderDeliveries);
        return this;
    }

    public DarazUsers addOrderdeliveryDeliverymanager(OrderDelivery orderDelivery) {
        this.orderdeliveryDeliverymanagers.add(orderDelivery);
        orderDelivery.setDeliveryManager(this);
        return this;
    }

    public DarazUsers removeOrderdeliveryDeliverymanager(OrderDelivery orderDelivery) {
        this.orderdeliveryDeliverymanagers.remove(orderDelivery);
        orderDelivery.setDeliveryManager(null);
        return this;
    }

    public Set<OrderDelivery> getOrderdeliveryDeliveryboys() {
        return this.orderdeliveryDeliveryboys;
    }

    public void setOrderdeliveryDeliveryboys(Set<OrderDelivery> orderDeliveries) {
        if (this.orderdeliveryDeliveryboys != null) {
            this.orderdeliveryDeliveryboys.forEach(i -> i.setDeliveryBoy(null));
        }
        if (orderDeliveries != null) {
            orderDeliveries.forEach(i -> i.setDeliveryBoy(this));
        }
        this.orderdeliveryDeliveryboys = orderDeliveries;
    }

    public DarazUsers orderdeliveryDeliveryboys(Set<OrderDelivery> orderDeliveries) {
        this.setOrderdeliveryDeliveryboys(orderDeliveries);
        return this;
    }

    public DarazUsers addOrderdeliveryDeliveryboy(OrderDelivery orderDelivery) {
        this.orderdeliveryDeliveryboys.add(orderDelivery);
        orderDelivery.setDeliveryBoy(this);
        return this;
    }

    public DarazUsers removeOrderdeliveryDeliveryboy(OrderDelivery orderDelivery) {
        this.orderdeliveryDeliveryboys.remove(orderDelivery);
        orderDelivery.setDeliveryBoy(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DarazUsers)) {
            return false;
        }
        return id != null && id.equals(((DarazUsers) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DarazUsers{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phone='" + getPhone() + "'" +
            "}";
    }
}
