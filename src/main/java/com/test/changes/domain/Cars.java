package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Cars.
 */
@Entity
@Table(name = "cars")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cars")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Cars implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "caruid", nullable = false)
    private Integer caruid;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(name = "rel_cars__color", joinColumns = @JoinColumn(name = "cars_id"), inverseJoinColumns = @JoinColumn(name = "color_id"))
    @JsonIgnoreProperties(value = { "cars" }, allowSetters = true)
    private Set<Colors> colors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cars id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCaruid() {
        return this.caruid;
    }

    public Cars caruid(Integer caruid) {
        this.setCaruid(caruid);
        return this;
    }

    public void setCaruid(Integer caruid) {
        this.caruid = caruid;
    }

    public String getName() {
        return this.name;
    }

    public Cars name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Colors> getColors() {
        return this.colors;
    }

    public void setColors(Set<Colors> colors) {
        this.colors = colors;
    }

    public Cars colors(Set<Colors> colors) {
        this.setColors(colors);
        return this;
    }

    public Cars addColor(Colors colors) {
        this.colors.add(colors);
        colors.getCars().add(this);
        return this;
    }

    public Cars removeColor(Colors colors) {
        this.colors.remove(colors);
        colors.getCars().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cars)) {
            return false;
        }
        return id != null && id.equals(((Cars) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cars{" +
            "id=" + getId() +
            ", caruid=" + getCaruid() +
            ", name='" + getName() + "'" +
            "}";
    }
}
