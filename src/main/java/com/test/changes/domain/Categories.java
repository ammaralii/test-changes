package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Categories.
 */
@Entity
@Table(name = "categories")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "categories")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(max = 100)
    @Column(name = "detail", length = 100, nullable = false)
    private String detail;

    @OneToMany(mappedBy = "category")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "category", "orderdetailsProducts", "productdetailsProducts" }, allowSetters = true)
    private Set<Products> productsCategories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Categories id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Categories name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return this.detail;
    }

    public Categories detail(String detail) {
        this.setDetail(detail);
        return this;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Set<Products> getProductsCategories() {
        return this.productsCategories;
    }

    public void setProductsCategories(Set<Products> products) {
        if (this.productsCategories != null) {
            this.productsCategories.forEach(i -> i.setCategory(null));
        }
        if (products != null) {
            products.forEach(i -> i.setCategory(this));
        }
        this.productsCategories = products;
    }

    public Categories productsCategories(Set<Products> products) {
        this.setProductsCategories(products);
        return this;
    }

    public Categories addProductsCategory(Products products) {
        this.productsCategories.add(products);
        products.setCategory(this);
        return this;
    }

    public Categories removeProductsCategory(Products products) {
        this.productsCategories.remove(products);
        products.setCategory(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Categories)) {
            return false;
        }
        return id != null && id.equals(((Categories) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Categories{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", detail='" + getDetail() + "'" +
            "}";
    }
}
