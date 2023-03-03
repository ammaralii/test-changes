package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Colors.
 */
@Entity
@Table(name = "colors")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "colors")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Colors implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "coloruid", nullable = false)
    private Integer coloruid;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "colors")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "colors" }, allowSetters = true)
    private Set<Cars> cars = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Colors id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getColoruid() {
        return this.coloruid;
    }

    public Colors coloruid(Integer coloruid) {
        this.setColoruid(coloruid);
        return this;
    }

    public void setColoruid(Integer coloruid) {
        this.coloruid = coloruid;
    }

    public String getName() {
        return this.name;
    }

    public Colors name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Cars> getCars() {
        return this.cars;
    }

    public void setCars(Set<Cars> cars) {
        if (this.cars != null) {
            this.cars.forEach(i -> i.removeColor(this));
        }
        if (cars != null) {
            cars.forEach(i -> i.addColor(this));
        }
        this.cars = cars;
    }

    public Colors cars(Set<Cars> cars) {
        this.setCars(cars);
        return this;
    }

    public Colors addCar(Cars cars) {
        this.cars.add(cars);
        cars.getColors().add(this);
        return this;
    }

    public Colors removeCar(Cars cars) {
        this.cars.remove(cars);
        cars.getColors().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Colors)) {
            return false;
        }
        return id != null && id.equals(((Colors) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Colors{" +
            "id=" + getId() +
            ", coloruid=" + getColoruid() +
            ", name='" + getName() + "'" +
            "}";
    }
}
