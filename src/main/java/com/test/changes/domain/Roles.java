package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Roles.
 */
@Entity
@Table(name = "roles")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "roles")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Roles implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "role_pr_id", nullable = false)
    private Integer rolePrId;

    @NotNull
    @Size(max = 30)
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(
        value = {
            "manager", "roles", "addressesUsers", "darazusersManagers", "orderdeliveryDeliverymanagers", "orderdeliveryDeliveryboys",
        },
        allowSetters = true
    )
    private Set<DarazUsers> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Roles id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRolePrId() {
        return this.rolePrId;
    }

    public Roles rolePrId(Integer rolePrId) {
        this.setRolePrId(rolePrId);
        return this;
    }

    public void setRolePrId(Integer rolePrId) {
        this.rolePrId = rolePrId;
    }

    public String getName() {
        return this.name;
    }

    public Roles name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<DarazUsers> getUsers() {
        return this.users;
    }

    public void setUsers(Set<DarazUsers> darazUsers) {
        if (this.users != null) {
            this.users.forEach(i -> i.removeRole(this));
        }
        if (darazUsers != null) {
            darazUsers.forEach(i -> i.addRole(this));
        }
        this.users = darazUsers;
    }

    public Roles users(Set<DarazUsers> darazUsers) {
        this.setUsers(darazUsers);
        return this;
    }

    public Roles addUser(DarazUsers darazUsers) {
        this.users.add(darazUsers);
        darazUsers.getRoles().add(this);
        return this;
    }

    public Roles removeUser(DarazUsers darazUsers) {
        this.users.remove(darazUsers);
        darazUsers.getRoles().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Roles)) {
            return false;
        }
        return id != null && id.equals(((Roles) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Roles{" +
            "id=" + getId() +
            ", rolePrId=" + getRolePrId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
